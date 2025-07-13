package com.aryanvbw.focus.data

import androidx.lifecycle.LiveData
import androidx.room.*
import java.util.Date

// Data class to hold app stats results
data class AppStatCount(val appPackage: String, val count: Int)

// Data class to hold content type stats results
data class ContentTypeStatCount(val contentType: String, val count: Int)

@Dao
interface BlockedContentEventDao {
    @Insert
    suspend fun insert(event: BlockedContentEvent)

    @Query("SELECT COUNT(*) FROM blocked_content_events WHERE timestamp >= :startTime")
    suspend fun getBlockCountSince(startTime: Date): Int

    @Query("SELECT * FROM blocked_content_events ORDER BY timestamp DESC")
    fun getAllEvents(): LiveData<List<BlockedContentEvent>>

    @Query("SELECT COUNT(*) FROM blocked_content_events WHERE appPackage = :packageName")
    suspend fun getCountForApp(packageName: String): Int

    @Query("SELECT COUNT(*) FROM blocked_content_events WHERE contentType = :type")
    suspend fun getCountForContentType(type: String): Int

    @Query("SELECT COUNT(*) FROM blocked_content_events WHERE timestamp >= :startTime AND timestamp <= :endTime")
    suspend fun getBlockCountForPeriod(startTime: Date, endTime: Date): Int
    
    // Temporary method to simulate getTodayBlockCount from previous implementation
    suspend fun getTodayBlockCount(): Int {
        val calendar = java.util.Calendar.getInstance()
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
        calendar.set(java.util.Calendar.MINUTE, 0)
        calendar.set(java.util.Calendar.SECOND, 0)
        val startOfDay = calendar.time
        return getBlockCountSince(startOfDay)
    }
    
    // Get events between dates
    @Query("SELECT * FROM blocked_content_events WHERE timestamp >= :startDate AND timestamp <= :endDate ORDER BY timestamp DESC")
    suspend fun getEventsBetweenDates(startDate: Date, endDate: Date): List<BlockedContentEvent>
    
    // Get app-specific stats for a time period
    @Query("SELECT appPackage, COUNT(*) as count FROM blocked_content_events WHERE timestamp >= :startTime AND timestamp <= :endTime GROUP BY appPackage ORDER BY count DESC")
    suspend fun getAppStatsForPeriod(startTime: Date, endTime: Date): List<AppStatCount>
    
    // Get content-type-specific stats for a time period
    @Query("SELECT contentType, COUNT(*) as count FROM blocked_content_events WHERE timestamp >= :startTime AND timestamp <= :endTime GROUP BY contentType ORDER BY count DESC")
    suspend fun getContentTypeStatsForPeriod(startTime: Date, endTime: Date): List<ContentTypeStatCount>
    
    // LiveData methods for real-time updates
    @Query("SELECT COUNT(*) FROM blocked_content_events WHERE DATE(timestamp/1000, 'unixepoch') = DATE('now')")
    fun getTodayBlockedCount(): LiveData<Int>
    
    @Query("SELECT COUNT(*) * 2 FROM blocked_content_events WHERE DATE(timestamp/1000, 'unixepoch') = DATE('now')")
    fun getEstimatedTimeSavedMinutes(): LiveData<Int>
}
