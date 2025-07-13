package com.aryanvbw.focus.service

import android.app.Service
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import com.aryanvbw.focus.data.AppDatabase
import com.aryanvbw.focus.data.AppUsageEvent
import com.aryanvbw.focus.data.AppLimit
import com.aryanvbw.focus.util.AppSettings
import com.aryanvbw.focus.util.NotificationHelper
import kotlinx.coroutines.*
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Service to track app usage analytics in Normal Mode
 * Monitors app usage patterns, enforces limits, and provides insights
 */
class UsageAnalyticsService : Service() {
    
    companion object {
        private const val TAG = "UsageAnalyticsService"
        private const val USAGE_CHECK_INTERVAL = 60 * 1000L // 1 minute
        private const val NOTIFICATION_ID_LIMIT_WARNING = 1001
        private const val NOTIFICATION_ID_LIMIT_EXCEEDED = 1002
    }
    
    private lateinit var appSettings: AppSettings
    private lateinit var database: AppDatabase
    private lateinit var notificationHelper: NotificationHelper
    private lateinit var usageStatsManager: UsageStatsManager
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    private val handler = Handler(Looper.getMainLooper())
    private var usageCheckRunnable: Runnable? = null
    
    private val currentSessions = mutableMapOf<String, Long>() // packageName -> startTime
    private val todayUsage = mutableMapOf<String, Long>() // packageName -> totalUsageMs
    
    override fun onCreate() {
        super.onCreate()
        appSettings = AppSettings(this)
        database = AppDatabase.getInstance(this)
        notificationHelper = NotificationHelper(this)
        usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        
        Log.d(TAG, "UsageAnalyticsService created")
        startUsageTracking()
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "UsageAnalyticsService started")
        return START_STICKY
    }
    
    override fun onBind(intent: Intent?): IBinder? = null
    
    override fun onDestroy() {
        super.onDestroy()
        stopUsageTracking()
        serviceScope.cancel()
        Log.d(TAG, "UsageAnalyticsService destroyed")
    }
    
    private fun startUsageTracking() {
        usageCheckRunnable = object : Runnable {
            override fun run() {
                checkAppUsage()
                handler.postDelayed(this, USAGE_CHECK_INTERVAL)
            }
        }
        handler.post(usageCheckRunnable!!)
    }
    
    private fun stopUsageTracking() {
        usageCheckRunnable?.let { handler.removeCallbacks(it) }
    }
    
    private fun checkAppUsage() {
        serviceScope.launch {
            try {
                val endTime = System.currentTimeMillis()
                val startTime = endTime - TimeUnit.DAYS.toMillis(1) // Last 24 hours
                
                val usageStats = usageStatsManager.queryUsageStats(
                    UsageStatsManager.INTERVAL_DAILY,
                    startTime,
                    endTime
                )
                
                processUsageStats(usageStats)
                checkLimitsAndNotify()
                
            } catch (e: Exception) {
                Log.e(TAG, "Error checking app usage", e)
            }
        }
    }
    
    private suspend fun processUsageStats(usageStats: List<UsageStats>) {
        val packageManager = packageManager
        val today = getTodayStartTime()
        
        for (stat in usageStats) {
            if (stat.totalTimeInForeground > 0) {
                try {
                    val appInfo = packageManager.getApplicationInfo(stat.packageName, 0)
                    val appName = packageManager.getApplicationLabel(appInfo).toString()
                    val category = getAppCategory(stat.packageName)
                    
                    // Update today's usage tracking
                    val todayUsageTime = getTodayUsageTime(stat, today)
                    if (todayUsageTime > 0) {
                        todayUsage[stat.packageName] = todayUsageTime
                        
                        // Save usage event to database
                        val usageEvent = AppUsageEvent(
                            packageName = stat.packageName,
                            appName = appName,
                            category = category,
                            startTime = Date(stat.firstTimeStamp),
                            endTime = Date(stat.lastTimeStamp),
                            usageDurationMs = todayUsageTime,
                            timestamp = Date()
                        )
                        
                        database.appUsageEventDao().insertUsageEvent(usageEvent)
                    }
                    
                } catch (e: PackageManager.NameNotFoundException) {
                    // App might be uninstalled
                    continue
                }
            }
        }
    }
    
    private suspend fun checkLimitsAndNotify() {
        val enabledLimits = database.appLimitDao().getEnabledLimits().value ?: return
        
        for (limit in enabledLimits) {
            val usageMs = todayUsage[limit.packageName] ?: 0L
            val usageMinutes = TimeUnit.MILLISECONDS.toMinutes(usageMs)
            val limitMinutes = limit.dailyLimitMinutes.toLong()
            
            when {
                usageMinutes >= limitMinutes -> {
                    // Limit exceeded
                    showLimitExceededNotification(limit, usageMinutes)
                }
                usageMinutes >= (limitMinutes * 0.8) -> {
                    // 80% warning
                    showLimitWarningNotification(limit, usageMinutes, limitMinutes)
                }
            }
        }
    }
    
    private fun showLimitWarningNotification(limit: AppLimit, usedMinutes: Long, limitMinutes: Long) {
        val remainingMinutes = (limitMinutes - usedMinutes).toInt()
        val notification = notificationHelper.createLimitWarningNotification(
            appName = limit.appName,
            usageMinutes = usedMinutes.toInt(),
            limitMinutes = limitMinutes.toInt(),
            remainingMinutes = remainingMinutes
        )
        
        notificationHelper.showNotification(notification, NOTIFICATION_ID_LIMIT_WARNING)
    }
    
    private fun showLimitExceededNotification(limit: AppLimit, usedMinutes: Long) {
        val exceededMinutes = (usedMinutes - limit.dailyLimitMinutes).toInt()
        val notification = notificationHelper.createLimitExceededNotification(
            appName = limit.appName,
            usageMinutes = usedMinutes.toInt(),
            limitMinutes = limit.dailyLimitMinutes,
            exceededMinutes = exceededMinutes
        )
        
        notificationHelper.showNotification(notification, NOTIFICATION_ID_LIMIT_EXCEEDED)
    }
    
    private fun getTodayStartTime(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
    
    private fun getTodayUsageTime(stat: UsageStats, todayStart: Long): Long {
        return if (stat.lastTimeStamp >= todayStart) {
            val actualStart = maxOf(stat.firstTimeStamp, todayStart)
            stat.totalTimeInForeground - (actualStart - stat.firstTimeStamp).coerceAtLeast(0)
        } else {
            0L
        }
    }
    
    private fun getAppCategory(packageName: String): String {
        return when {
            packageName.contains("social") || 
            packageName.contains("instagram") || 
            packageName.contains("facebook") || 
            packageName.contains("twitter") || 
            packageName.contains("snapchat") || 
            packageName.contains("tiktok") -> "social"
            
            packageName.contains("youtube") || 
            packageName.contains("netflix") || 
            packageName.contains("spotify") -> "entertainment"
            
            packageName.contains("gmail") || 
            packageName.contains("office") || 
            packageName.contains("docs") -> "productivity"
            
            packageName.contains("game") -> "games"
            
            else -> "other"
        }
    }
}
