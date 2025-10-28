package com.aryanvbw.focus.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Entity to track notification events and control
 */
@Entity(tableName = "notification_events")
data class NotificationEvent(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val packageName: String,
    val appName: String,
    val title: String,
    val content: String,
    val isBlocked: Boolean = false,
    val priority: Int,
    val timestamp: Date = Date()
)
