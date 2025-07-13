package com.aryanvbw.focus.ui.timer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TimerViewModel : ViewModel() {

    private val _focusedHours = MutableLiveData<Float>().apply {
        value = 3.5f
    }
    val focusedHours: LiveData<Float> = _focusedHours

    private val _sessionsCompleted = MutableLiveData<Int>().apply {
        value = 5
    }
    val sessionsCompleted: LiveData<Int> = _sessionsCompleted

    private val _distractionsBlocked = MutableLiveData<Int>().apply {
        value = 12
    }
    val distractionsBlocked: LiveData<Int> = _distractionsBlocked

    private val _timerHours = MutableLiveData<Int>().apply {
        value = 0
    }
    val timerHours: LiveData<Int> = _timerHours

    private val _timerMinutes = MutableLiveData<Int>().apply {
        value = 0
    }
    val timerMinutes: LiveData<Int> = _timerMinutes

    private val _timerSeconds = MutableLiveData<Int>().apply {
        value = 0
    }
    val timerSeconds: LiveData<Int> = _timerSeconds

    private val _isTimerRunning = MutableLiveData<Boolean>().apply {
        value = false
    }
    val isTimerRunning: LiveData<Boolean> = _isTimerRunning

    private var timerJob: Job? = null
    private var totalSeconds = 0

    fun startFocusSession() {
        if (_isTimerRunning.value == true) {
            stopTimer()
        } else {
            startTimer()
        }
    }

    private fun startTimer() {
        _isTimerRunning.value = true
        timerJob = viewModelScope.launch {
            while (_isTimerRunning.value == true) {
                delay(1000)
                totalSeconds++
                updateTimerDisplay()
            }
        }
    }

    private fun stopTimer() {
        _isTimerRunning.value = false
        timerJob?.cancel()
        
        // Update session stats
        if (totalSeconds > 0) {
            val sessionMinutes = totalSeconds / 60
            val currentHours = _focusedHours.value ?: 0f
            _focusedHours.value = currentHours + (sessionMinutes / 60f)
            
            val currentSessions = _sessionsCompleted.value ?: 0
            _sessionsCompleted.value = currentSessions + 1
        }
        
        // Reset timer
        totalSeconds = 0
        updateTimerDisplay()
    }

    private fun updateTimerDisplay() {
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        
        _timerHours.value = hours
        _timerMinutes.value = minutes
        _timerSeconds.value = seconds
    }

    fun blockDistractions() {
        // Increment distractions blocked counter
        val current = _distractionsBlocked.value ?: 0
        _distractionsBlocked.value = current + 1
        
        // TODO: Implement actual blocking logic
    }

    fun viewProgress() {
        // TODO: Navigate to progress fragment
    }

    fun openSettings() {
        // TODO: Navigate to settings fragment
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}