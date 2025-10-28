package com.aryanvbw.focus.data

import android.content.Context
import androidx.lifecycle.LiveData
import com.aryanvbw.focus.util.AppSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

/**
 * Repository to manage app data and business logic
 */
class FocusRepository(context: Context) {
    
    private val database = AppDatabase.getInstance(context)
    private val appSettings = AppSettings(context)
    
    // DAOs
    private val blockedContentEventDao = database.blockedContentEventDao()
    private val appUsageEventDao = database.appUsageEventDao()
    private val appLimitDao = database.appLimitDao()
    private val notificationEventDao = database.notificationEventDao()
    private val appModeDao = database.appModeDao()
    
    // ===== App Mode Management =====
    
    suspend fun getCurrentMode(): AppMode? {
        return withContext(Dispatchers.IO) {
            appModeDao.getCurrentMode()
        }
    }
    
    fun getCurrentModeLive(): LiveData<AppMode?> {
        return appModeDao.getCurrentModeLive()
    }
    
    suspend fun setAppMode(mode: String) {
        withContext(Dispatchers.IO) {
            val currentMode = appModeDao.getCurrentMode()
            if (currentMode != null) {
                appModeDao.updateCurrentMode(mode, Date())
            } else {
                appModeDao.insertOrUpdateMode(
                    AppMode(
                        id = "current_mode",
                        mode = mode,
                        isActive = true,
                        createdAt = Date(),
                        updatedAt = Date()
                    )
                )
            }
            
            // Update settings as well for backward compatibility
            appSettings.getPreferences().edit()
                .putBoolean(AppSettings.KEY_FOCUS_MODE, mode == "focus")
                .apply()
        }
    }
    
    // ===== Usage Analytics =====
    
    fun getAllUsageEvents(): LiveData<List<AppUsageEvent>> {
        return appUsageEventDao.getAllUsageEvents()
    }
    
    fun getTodayUsageEvents(): LiveData<List<AppUsageEvent>> {
        return appUsageEventDao.getTodayUsageEvents()
    }
    
    fun getTodayUsageSummary(): LiveData<List<AppUsageSummary>> {
        return appUsageEventDao.getTodayUsageSummary()
    }
    
    fun getWeeklyUsageSummary(): LiveData<List<AppUsageSummary>> {
        return appUsageEventDao.getWeeklyUsageSummary()
    }
    
    suspend fun insertUsageEvent(event: AppUsageEvent) {
        withContext(Dispatchers.IO) {
            appUsageEventDao.insertUsageEvent(event)
        }
    }
    
    // ===== App Limits =====
    
    fun getAllLimits(): LiveData<List<AppLimit>> {
        return appLimitDao.getAllLimits()
    }
    
    fun getEnabledLimits(): LiveData<List<AppLimit>> {
        return appLimitDao.getEnabledLimits()
    }
    
    suspend fun getLimitForApp(packageName: String): AppLimit? {
        return withContext(Dispatchers.IO) {
            appLimitDao.getLimitForApp(packageName)
        }
    }
    
    suspend fun setAppLimit(packageName: String, appName: String, limitMinutes: Int) {
        withContext(Dispatchers.IO) {
            val limit = AppLimit(
                packageName = packageName,
                appName = appName,
                dailyLimitMinutes = limitMinutes,
                isEnabled = true,
                createdAt = Date(),
                updatedAt = Date()
            )
            appLimitDao.insertOrUpdateLimit(limit)
        }
    }
    
    suspend fun removeAppLimit(packageName: String) {
        withContext(Dispatchers.IO) {
            appLimitDao.deleteLimitByPackage(packageName)
        }
    }
    
    suspend fun toggleLimitEnabled(packageName: String, enabled: Boolean) {
        withContext(Dispatchers.IO) {
            val currentLimit = appLimitDao.getLimitForApp(packageName)
            currentLimit?.let { limit ->
                val updatedLimit = limit.copy(
                    isEnabled = enabled,
                    updatedAt = Date()
                )
                appLimitDao.updateLimit(updatedLimit)
            }
        }
    }
    
    // ===== Notification Events =====
    
    fun getAllNotificationEvents(): LiveData<List<NotificationEvent>> {
        return notificationEventDao.getAllNotificationEvents()
    }
    
    fun getTodayNotificationEvents(): LiveData<List<NotificationEvent>> {
        return notificationEventDao.getTodayNotificationEvents()
    }
    
    fun getTodayBlockedNotifications(): LiveData<List<NotificationEvent>> {
        return notificationEventDao.getTodayBlockedNotifications()
    }
    
    fun getTodayBlockedNotificationCount(): LiveData<Int> {
        return notificationEventDao.getTodayBlockedNotificationCount()
    }
    
    suspend fun insertNotificationEvent(event: NotificationEvent) {
        withContext(Dispatchers.IO) {
            notificationEventDao.insertNotificationEvent(event)
        }
    }
    
    // ===== Blocked Content Events =====
    
    fun getTodayBlockedCount(): LiveData<Int> {
        return blockedContentEventDao.getTodayBlockedCount()
    }
    
    fun getEstimatedTimeSavedMinutes(): LiveData<Int> {
        return blockedContentEventDao.getEstimatedTimeSavedMinutes()
    }
    
    suspend fun insertBlockedContentEvent(event: BlockedContentEvent) {
        withContext(Dispatchers.IO) {
            blockedContentEventDao.insert(event)
        }
    }
    
    // ===== Data Cleanup =====
    
    suspend fun cleanupOldData() {
        withContext(Dispatchers.IO) {
            val thirtyDaysAgo = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_MONTH, -30)
            }.time
            
            appUsageEventDao.deleteOldEvents(thirtyDaysAgo)
            notificationEventDao.deleteOldEvents(thirtyDaysAgo)
        }
    }
    
    // ===== Insights and Analytics =====
    
    suspend fun getUsageInsights(): UsageInsights {
        return withContext(Dispatchers.IO) {
            val todaySummary = appUsageEventDao.getTodayUsageSummary().value ?: emptyList()
            val weeklySummary = appUsageEventDao.getWeeklyUsageSummary().value ?: emptyList()
            val todayBlocked = blockedContentEventDao.getTodayBlockedCount().value ?: 0
            val todayBlockedNotifications = notificationEventDao.getTodayBlockedNotificationCount().value ?: 0
            
            val totalTodayUsage = todaySummary.sumOf { it.totalUsage }
            val totalWeeklyUsage = weeklySummary.sumOf { it.totalUsage }
            val avgDailyUsage = totalWeeklyUsage / 7
            
            val mostUsedApp = todaySummary.maxByOrNull { it.totalUsage }
            val productivityScore = calculateProductivityScore(todaySummary, todayBlocked)
            
            UsageInsights(
                totalTodayUsageMs = totalTodayUsage,
                totalWeeklyUsageMs = totalWeeklyUsage,
                avgDailyUsageMs = avgDailyUsage,
                mostUsedApp = mostUsedApp,
                todayBlockedCount = todayBlocked,
                todayBlockedNotifications = todayBlockedNotifications,
                productivityScore = productivityScore
            )
        }
    }
    
    private fun calculateProductivityScore(todaySummary: List<AppUsageSummary>, blockedCount: Int): Int {
        if (todaySummary.isEmpty()) return 100
        
        val totalUsage = todaySummary.sumOf { it.totalUsage }
        val distractingUsage = todaySummary
            .filter { it.packageName.contains("social") || it.packageName.contains("game") }
            .sumOf { it.totalUsage }
        
        val distractingRatio = if (totalUsage > 0) (distractingUsage.toDouble() / totalUsage) else 0.0
        val baseScore = ((1.0 - distractingRatio) * 100).toInt()
        
        // Bonus for blocking distracting content
        val blockingBonus = (blockedCount * 2).coerceAtMost(20)
        
        return (baseScore + blockingBonus).coerceIn(0, 100)
    }
}

/**
 * Data class for usage insights
 */
data class UsageInsights(
    val totalTodayUsageMs: Long,
    val totalWeeklyUsageMs: Long,
    val avgDailyUsageMs: Long,
    val mostUsedApp: AppUsageSummary?,
    val todayBlockedCount: Int,
    val todayBlockedNotifications: Int,
    val productivityScore: Int
)
