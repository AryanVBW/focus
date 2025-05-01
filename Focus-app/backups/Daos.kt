package com.aryanvbw.focus.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * Data Access Object for BlockedContentEvent entities
 */
@Dao
interface BlockedContentEventDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: BlockedContentEvent)
    
    @Query("SELECT * FROM blocked_content_events ORDER BY timestamp DESC")
    fun getAllEvents(): LiveData<List<BlockedContentEvent>>
    
    @Query("SELECT * FROM blocked_content_events WHERE timestamp BETWEEN :startTime AND :endTime ORDER BY timestamp DESC")
    fun getEventsBetweenDates(startTime: Long, endTime: Long): LiveData<List<BlockedContentEvent>>
    
    @Query("SELECT appPackage, COUNT(*) as count FROM blocked_content_events GROUP BY appPackage ORDER BY count DESC")
    fun getBlockCountByApp(): LiveData<Map<String, Int>>
    
    @Query("SELECT contentType, COUNT(*) as count FROM blocked_content_events GROUP BY contentType ORDER BY count DESC")
    fun getBlockCountByContentType(): LiveData<Map<String, Int>>
    
    @Query("SELECT COUNT(*) FROM blocked_content_events WHERE timestamp >= :startOfDay")
    fun getTodayBlockCount(startOfDay: Long): LiveData<Int>
}

/**
 * Data Access Object for AppUsageStats entities
 */
@Dao
interface AppUsageStatsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(stats: AppUsageStats)
    
    @Query("SELECT * FROM app_usage_stats WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getStatsBetweenDates(startDate: Long, endDate: Long): LiveData<List<AppUsageStats>>
    
    @Query("SELECT SUM(timeSpentMs) FROM app_usage_stats WHERE date >= :startOfDay")
    fun getTodayTotalUsageTime(startOfDay: Long): LiveData<Long>
    
    @Query("SELECT SUM(blockedCount) FROM app_usage_stats WHERE date >= :startOfDay")
    fun getTodayTotalBlockedCount(startOfDay: Long): LiveData<Int>
}
