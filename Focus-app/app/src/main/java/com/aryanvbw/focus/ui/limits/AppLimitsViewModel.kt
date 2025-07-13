package com.aryanvbw.focus.ui.limits

import android.app.Application
import android.app.usage.UsageStats
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.aryanvbw.focus.data.AppDatabase
import com.aryanvbw.focus.data.AppLimit
import com.aryanvbw.focus.data.AppLimitDao
import kotlinx.coroutines.launch
import java.util.*

data class AppLimitWithUsage(
    val limit: AppLimit,
    val usageMinutes: Int = 0,
    val appIcon: String? = null
) {
    fun isExceeded(): Boolean = usageMinutes > limit.dailyLimitMinutes
    fun getUsagePercentage(): Int = if (limit.dailyLimitMinutes > 0) {
        minOf(100, (usageMinutes * 100) / limit.dailyLimitMinutes)
    } else 0
    
    fun getRemainingMinutes(): Int = maxOf(0, limit.dailyLimitMinutes - usageMinutes)
}

class AppLimitsViewModel(application: Application) : AndroidViewModel(application) {
    
    private val appLimitDao: AppLimitDao = AppDatabase.getInstance(application).appLimitDao()
    
    private val _appLimits = MutableLiveData<List<AppLimitWithUsage>>()
    val appLimits: LiveData<List<AppLimitWithUsage>> = _appLimits
    
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading
    
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    private var currentUsageStats: Map<String, Int> = emptyMap()
    
    fun loadAppLimits() {
        _loading.value = true
        
        viewModelScope.launch {
            try {
                appLimitDao.getAllLimits().observeForever { limits ->
                    val limitsWithUsage = limits.map { limit ->
                        AppLimitWithUsage(
                            limit = limit,
                            usageMinutes = currentUsageStats[limit.packageName] ?: 0
                        )
                    }
                    _appLimits.value = limitsWithUsage
                    _loading.value = false
                }
            } catch (e: Exception) {
                _error.value = e.message
                _loading.value = false
            }
        }
    }
    
    fun updateUsageData(usageStats: Map<String, UsageStats>) {
        currentUsageStats = usageStats.mapValues { (_, stats) ->
            (stats.totalTimeInForeground / (1000 * 60)).toInt() // Convert to minutes
        }
        
        // Refresh the app limits with updated usage data
        _appLimits.value?.let { currentLimits ->
            val updatedLimits = currentLimits.map { limitWithUsage ->
                limitWithUsage.copy(
                    usageMinutes = currentUsageStats[limitWithUsage.limit.packageName] ?: 0
                )
            }
            _appLimits.value = updatedLimits
        }
    }
    
    fun addAppLimit(packageName: String, appName: String, limitMinutes: Int) {
        viewModelScope.launch {
            try {
                val newLimit = AppLimit(
                    packageName = packageName,
                    appName = appName,
                    dailyLimitMinutes = limitMinutes,
                    isEnabled = true,
                    createdAt = Date(),
                    updatedAt = Date()
                )
                appLimitDao.insertOrUpdateLimit(newLimit)
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }
    
    fun updateAppLimit(limit: AppLimit) {
        viewModelScope.launch {
            try {
                val updatedLimit = limit.copy(updatedAt = Date())
                appLimitDao.updateLimit(updatedLimit)
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }
    
    fun toggleLimitEnabled(limitWithUsage: AppLimitWithUsage, isEnabled: Boolean) {
        viewModelScope.launch {
            try {
                val updatedLimit = limitWithUsage.limit.copy(
                    isEnabled = isEnabled,
                    updatedAt = Date()
                )
                appLimitDao.updateLimit(updatedLimit)
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }
    
    fun deleteAppLimit(limit: AppLimit) {
        viewModelScope.launch {
            try {
                appLimitDao.deleteLimit(limit)
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }
    
    fun showAppUsageDetails(packageName: String) {
        // This could open a detailed analytics view for the specific app
        // For now, we'll just store the package name for potential navigation
    }
}
