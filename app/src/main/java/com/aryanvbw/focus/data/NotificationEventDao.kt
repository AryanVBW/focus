package com.aryanvbw.focus.data

import androidx.room.*
import androidx.lifecycle.LiveData
import java.util.Date

/**
 * DAO for notification events
 */
@Dao
interface NotificationEventDao {
    
    @Query("SELECT * FROM notification_events ORDER BY timestamp DESC")
    fun getAllNotificationEvents(): LiveData<List<NotificationEvent>>
    
    @Query("SELECT * FROM notification_events WHERE DATE(timestamp/1000, 'unixepoch') = DATE('now') ORDER BY timestamp DESC")
    fun getTodayNotificationEvents(): LiveData<List<NotificationEvent>>
    
    @Query("SELECT * FROM notification_events WHERE isBlocked = 1 AND DATE(timestamp/1000, 'unixepoch') = DATE('now') ORDER BY timestamp DESC")
    fun getTodayBlockedNotifications(): LiveData<List<NotificationEvent>>
    
    @Query("SELECT COUNT(*) FROM notification_events WHERE isBlocked = 1 AND DATE(timestamp/1000, 'unixepoch') = DATE('now')")
    fun getTodayBlockedNotificationCount(): LiveData<Int>
    
    @Insert
    suspend fun insertNotificationEvent(event: NotificationEvent)
    
    @Update
    suspend fun updateNotificationEvent(event: NotificationEvent)
    
    @Delete
    suspend fun deleteNotificationEvent(event: NotificationEvent)
    
    @Query("DELETE FROM notification_events WHERE timestamp < :cutoffDate")
    suspend fun deleteOldEvents(cutoffDate: Date)
}
