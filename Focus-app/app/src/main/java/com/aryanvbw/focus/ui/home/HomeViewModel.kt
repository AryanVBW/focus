package com.aryanvbw.focus.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.aryanvbw.focus.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = FocusRepository(application)
    
    // Existing properties for Focus Mode
    private val _todayBlockedCount = MutableLiveData<Int>()
    val todayBlockedCount: LiveData<Int> = _todayBlockedCount
    
    private val _estimatedTimeSavedMinutes = MutableLiveData<Int>()
    val estimatedTimeSavedMinutes: LiveData<Int> = _estimatedTimeSavedMinutes
    
    // New properties for app mode management
    private val _currentMode = MutableLiveData<AppMode?>()
    val currentMode: LiveData<AppMode?> = _currentMode
    
    // New properties for Normal Mode
    private val _usageInsights = MutableLiveData<UsageInsights?>()
    val usageInsights: LiveData<UsageInsights?> = _usageInsights
    
    private val _todayUsageSummary = MutableLiveData<List<AppUsageSummary>>()
    val todayUsageSummary: LiveData<List<AppUsageSummary>> = _todayUsageSummary

    init {
        loadCurrentMode()
        loadTodayStats()
        loadUsageInsights()
    }
    
    private fun loadCurrentMode() {
        viewModelScope.launch {
            try {
                val mode = repository.getCurrentMode()
                _currentMode.postValue(mode)
            } catch (e: Exception) {
                // Create default normal mode if none exists
                repository.setAppMode("normal")
                _currentMode.postValue(AppMode(mode = "normal"))
            }
        }
    }
    
    fun setAppMode(mode: String) {
        viewModelScope.launch {
            try {
                repository.setAppMode(mode)
                val updatedMode = repository.getCurrentMode()
                _currentMode.postValue(updatedMode)
                
                // Refresh relevant data based on mode
                when (mode) {
                    "normal" -> loadUsageInsights()
                    "focus" -> loadTodayStats()
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
    
    private fun loadTodayStats() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Get today's date
                val calendar = Calendar.getInstance()
                val startTime = calendar.apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                }.time
                
                // This will be populated by observing repository LiveData
                repository.getTodayBlockedCount().observeForever { count ->
                    _todayBlockedCount.postValue(count ?: 0)
                }
                
                repository.getEstimatedTimeSavedMinutes().observeForever { minutes ->
                    _estimatedTimeSavedMinutes.postValue(minutes ?: 0)
                }
                
            } catch (e: Exception) {
                // Handle exceptions
                _todayBlockedCount.postValue(0)
                _estimatedTimeSavedMinutes.postValue(0)
            }
        }
    }
    
    private fun loadUsageInsights() {
        viewModelScope.launch {
            try {
                val insights = repository.getUsageInsights()
                _usageInsights.postValue(insights)
                
                // Also load today's usage summary for Normal Mode
                repository.getTodayUsageSummary().observeForever { summary ->
                    _todayUsageSummary.postValue(summary ?: emptyList())
                }
            } catch (e: Exception) {
                _usageInsights.postValue(null)
                _todayUsageSummary.postValue(emptyList())
            }
        }
    }
    
    fun refreshData() {
        loadCurrentMode()
        loadTodayStats()
        loadUsageInsights()
    }
}
