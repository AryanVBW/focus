package com.aryanvbw.focus.ui.progress

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ProgressViewModel : ViewModel() {

    private val _weeklyFocusTime = MutableLiveData<String>().apply {
        value = "25h 30m"
    }
    val weeklyFocusTime: LiveData<String> = _weeklyFocusTime

    private val _dailyStreak = MutableLiveData<Int>().apply {
        value = 5
    }
    val dailyStreak: LiveData<Int> = _dailyStreak

    private val _bestFocusDay = MutableLiveData<String>().apply {
        value = "Wednesday"
    }
    val bestFocusDay: LiveData<String> = _bestFocusDay

    private val _longestSession = MutableLiveData<String>().apply {
        value = "2h 15m"
    }
    val longestSession: LiveData<String> = _longestSession

    private val _distractionsBlocked = MutableLiveData<Int>().apply {
        value = 12
    }
    val distractionsBlocked: LiveData<Int> = _distractionsBlocked

    private val _weeklyChartData = MutableLiveData<List<Float>>().apply {
        value = listOf(3.0f, 4.0f, 4.5f, 3.5f, 4.25f, 3.75f, 3.25f) // Hours for each day
    }
    val weeklyChartData: LiveData<List<Float>> = _weeklyChartData

    private val _navigationEvent = MutableLiveData<String>()
    val navigationEvent: LiveData<String> = _navigationEvent

    fun openMenu() {
        _navigationEvent.value = "menu"
    }

    fun showBestFocusDayDetails() {
        _navigationEvent.value = "best_focus_day_details"
        // TODO: Show detailed breakdown of the best focus day
    }

    fun showLongestSessionDetails() {
        _navigationEvent.value = "longest_session_details"
        // TODO: Show details about the longest session
    }

    fun showDistractionsBlockedDetails() {
        _navigationEvent.value = "distractions_blocked_details"
        // TODO: Show breakdown of blocked distractions
    }

    fun updateWeeklyData() {
        // Simulate updating weekly data
        val currentData = _weeklyChartData.value ?: emptyList()
        val totalHours = currentData.sum()
        val totalMinutes = (totalHours * 60).toInt()
        val hours = totalMinutes / 60
        val minutes = totalMinutes % 60
        
        _weeklyFocusTime.value = "${hours}h ${minutes}m"
        
        // Update best focus day based on chart data
        val days = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
        val maxIndex = currentData.indexOf(currentData.maxOrNull())
        if (maxIndex >= 0 && maxIndex < days.size) {
            _bestFocusDay.value = days[maxIndex]
        }
    }

    fun incrementDailyStreak() {
        val current = _dailyStreak.value ?: 0
        _dailyStreak.value = current + 1
    }

    fun updateLongestSession(sessionDuration: String) {
        _longestSession.value = sessionDuration
    }

    fun incrementDistractionsBlocked() {
        val current = _distractionsBlocked.value ?: 0
        _distractionsBlocked.value = current + 1
    }

    fun addFocusSession(durationMinutes: Int) {
        // Add a new focus session to today's data
        val currentData = _weeklyChartData.value?.toMutableList() ?: mutableListOf()
        if (currentData.isNotEmpty()) {
            // Assuming today is the last day in the chart (Sunday)
            val todayIndex = currentData.size - 1
            currentData[todayIndex] = currentData[todayIndex] + (durationMinutes / 60f)
            _weeklyChartData.value = currentData
            updateWeeklyData()
        }
    }

    fun getWeeklyStats(): Map<String, Any> {
        return mapOf(
            "totalHours" to (_weeklyFocusTime.value ?: "0h 0m"),
            "dailyStreak" to (_dailyStreak.value ?: 0),
            "bestDay" to (_bestFocusDay.value ?: "Monday"),
            "longestSession" to (_longestSession.value ?: "0h 0m"),
            "distractionsBlocked" to (_distractionsBlocked.value ?: 0)
        )
    }
}