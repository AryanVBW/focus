package com.aryanvbw.focus.data

import androidx.room.*
import androidx.lifecycle.LiveData
import java.util.Date

/**
 * DAO for app usage events
 */
@Dao
interface AppUsageEventDao {
    
    @Query("SELECT * FROM app_usage_events ORDER BY timestamp DESC")
    fun getAllUsageEvents(): LiveData<List<AppUsageEvent>>
    
    @Query("SELECT * FROM app_usage_events WHERE packageName = :packageName ORDER BY timestamp DESC")
    fun getUsageEventsForApp(packageName: String): LiveData<List<AppUsageEvent>>
    
    @Query("SELECT * FROM app_usage_events WHERE DATE(timestamp/1000, 'unixepoch') = DATE('now') ORDER BY timestamp DESC")
    fun getTodayUsageEvents(): LiveData<List<AppUsageEvent>>
    
    @Query("SELECT packageName, appName, SUM(usageDurationMs) as totalUsage FROM app_usage_events WHERE DATE(timestamp/1000, 'unixepoch') = DATE('now') GROUP BY packageName ORDER BY totalUsage DESC")
    fun getTodayUsageSummary(): LiveData<List<AppUsageSummary>>
    
    @Query("SELECT packageName, appName, SUM(usageDurationMs) as totalUsage FROM app_usage_events WHERE DATE(timestamp/1000, 'unixepoch') >= DATE('now', '-7 days') GROUP BY packageName ORDER BY totalUsage DESC")
    fun getWeeklyUsageSummary(): LiveData<List<AppUsageSummary>>
    
    @Insert
    suspend fun insertUsageEvent(event: AppUsageEvent)
    
    @Update
    suspend fun updateUsageEvent(event: AppUsageEvent)
    
    @Delete
    suspend fun deleteUsageEvent(event: AppUsageEvent)
    
    @Query("DELETE FROM app_usage_events WHERE timestamp < :cutoffDate")
    suspend fun deleteOldEvents(cutoffDate: Date)
}

/**
 * Data class for usage summary queries
 */
data class AppUsageSummary(
    val packageName: String,
    val appName: String,
    val totalUsage: Long // in milliseconds
)
