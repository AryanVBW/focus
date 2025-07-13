package com.aryanvbw.focus.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Entity to store daily app usage limits set by user
 */
@Entity(tableName = "app_limits")
data class AppLimit(
    @PrimaryKey
    val packageName: String,
    val appName: String,
    val dailyLimitMinutes: Int,
    val isEnabled: Boolean = true,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)
