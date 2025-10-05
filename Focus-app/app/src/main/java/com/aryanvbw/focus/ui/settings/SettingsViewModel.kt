package com.aryanvbw.focus.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SettingsViewModel : ViewModel() {
    
    private val _navigationEvent = MutableLiveData<String>()
    val navigationEvent: LiveData<String> = _navigationEvent
    
    fun openTimerLengthSettings() {
        _navigationEvent.value = "timer_length"
    }
    
    fun openShortBreakSettings() {
        _navigationEvent.value = "short_break"
    }
    
    fun openLongBreakSettings() {
        _navigationEvent.value = "long_break"
    }
    
    fun openWebsiteBlockingSettings() {
        _navigationEvent.value = "websites"
    }
    
    fun openScheduleSettings() {
        _navigationEvent.value = "schedules"
    }
    
    fun openMotivationQuotesSettings() {
        _navigationEvent.value = "motivation_quotes"
    }

    fun openThemeSwitchingSettings() {
        _navigationEvent.value = "theme_switch"
    }
    
    fun openBreakRemindersSettings() {
        _navigationEvent.value = "break_reminders"
    }
}