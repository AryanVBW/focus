package com.focus.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import java.util.Date

/**
 * Entity that represents a blocked content event
 */
@Entity(tableName = "blocked_content_events")
data class BlockedContentEvent(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val timestamp: Long,
    val appPackage: String,
    val contentType: String
)

/**
 * Entity that represents app usage statistics
 */
@Entity(tableName = "app_usage_stats")
data class AppUsageStats(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val date: Long,
    val appPackage: String,
    val timeSpentMs: Long,
    val blockedCount: Int
)

/**
 * Utility class to convert between types for Room database
 */
class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}
