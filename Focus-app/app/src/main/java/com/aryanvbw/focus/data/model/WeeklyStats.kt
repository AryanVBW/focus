package com.aryanvbw.focus.data.model

data class WeeklyStats(
    val focusHours: Int,
    val focusMinutes: Int,
    val appsBlocked: Int,
    val goalsAchieved: Int,
    val totalGoals: Int,
    val streakDays: Int
)
