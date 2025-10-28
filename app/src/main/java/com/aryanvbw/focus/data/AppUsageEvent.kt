package com.aryanvbw.focus.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Entity to track app usage events for analytics
 */
@Entity(tableName = "app_usage_events")
data class AppUsageEvent(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val packageName: String,
    val appName: String,
    val category: String, // e.g., "social", "productivity", "entertainment"
    val startTime: Date,
    val endTime: Date?,
    val usageDurationMs: Long,
    val timestamp: Date = Date()
)
