package com.aryanvbw.focus.util

import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import java.util.*

/**
 * Utility class for managing app usage statistics
 */
class AppUsageManager(private val context: Context) {
    
    private val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
    
    /**
     * Get today's usage statistics for all apps
     */
    fun getTodayUsageStats(): Map<String, UsageStats> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startTime = calendar.timeInMillis
        val endTime = System.currentTimeMillis()
        
        val usageStats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            startTime,
            endTime
        )
        
        return usageStats.associateBy { it.packageName }
    }
    
    /**
     * Get usage statistics for a specific time range
     */
    fun getUsageStats(startTime: Long, endTime: Long): Map<String, UsageStats> {
        val usageStats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            startTime,
            endTime
        )
        
        return usageStats.associateBy { it.packageName }
    }
    
    /**
     * Get usage time for a specific app today (in minutes)
     */
    fun getTodayUsageForApp(packageName: String): Int {
        val todayStats = getTodayUsageStats()
        val appStats = todayStats[packageName]
        return if (appStats != null) {
            (appStats.totalTimeInForeground / (1000 * 60)).toInt()
        } else {
            0
        }
    }
    
    /**
     * Check if usage stats permission is granted
     */
    fun hasUsageStatsPermission(): Boolean {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MINUTE, -1)
        val stats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            calendar.timeInMillis,
            System.currentTimeMillis()
        )
        return stats.isNotEmpty()
    }
}