package com.aryanvbw.focus.ui.insights

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.aryanvbw.focus.data.*
import kotlinx.coroutines.launch

/**
 * ViewModel for Usage Insights screen in Normal Mode
 */
class InsightsViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository = FocusRepository(application)
    
    // Usage data
    val todayUsageSummary: LiveData<List<AppUsageSummary>> = repository.getTodayUsageSummary()
    val weeklyUsageSummary: LiveData<List<AppUsageSummary>> = repository.getWeeklyUsageSummary()
    
    // App limits
    val allLimits: LiveData<List<AppLimit>> = repository.getAllLimits()
    val enabledLimits: LiveData<List<AppLimit>> = repository.getEnabledLimits()
    
    // Notification data
    val todayBlockedNotifications: LiveData<List<NotificationEvent>> = repository.getTodayBlockedNotifications()
    val todayBlockedNotificationCount: LiveData<Int> = repository.getTodayBlockedNotificationCount()
    
    fun addAppLimit(packageName: String, appName: String, limitMinutes: Int) {
        viewModelScope.launch {
            repository.setAppLimit(packageName, appName, limitMinutes)
        }
    }
    
    fun removeAppLimit(packageName: String) {
        viewModelScope.launch {
            repository.removeAppLimit(packageName)
        }
    }
    
    fun toggleLimitEnabled(packageName: String, enabled: Boolean) {
        viewModelScope.launch {
            repository.toggleLimitEnabled(packageName, enabled)
        }
    }
}
