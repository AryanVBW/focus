package com.aryanvbw.focus.ui.analytics

import android.app.Application
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class UsageAnalyticsViewModel(application: Application) : AndroidViewModel(application) {

    private val usageStatsManager = application.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
    private val packageManager = application.packageManager

    private val _appUsageList = MutableLiveData<List<AppUsageData>>()
    val appUsageList: LiveData<List<AppUsageData>> = _appUsageList

    private val _weeklyData = MutableLiveData<List<DailyUsageData>>()
    val weeklyData: LiveData<List<DailyUsageData>> = _weeklyData

    private val _categoryData = MutableLiveData<List<CategoryUsageData>>()
    val categoryData: LiveData<List<CategoryUsageData>> = _categoryData

    private val _hourlyData = MutableLiveData<List<HourlyUsageData>>()
    val hourlyData: LiveData<List<HourlyUsageData>> = _hourlyData

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun loadUsageData(period: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.postValue(true)
            
            try {
                val (startTime, endTime) = getTimeRange(period)
                val usageStats = usageStatsManager.queryUsageStats(
                    UsageStatsManager.INTERVAL_DAILY,
                    startTime,
                    endTime
                )

                val appUsageList = processUsageStats(usageStats)
                val weeklyData = generateWeeklyData(period)
                val categoryData = generateCategoryData(appUsageList)
                val hourlyData = generateHourlyData(period)

                _appUsageList.postValue(appUsageList)
                _weeklyData.postValue(weeklyData)
                _categoryData.postValue(categoryData)
                _hourlyData.postValue(hourlyData)
                
            } catch (e: Exception) {
                // Handle error - post empty data or show error message
                _appUsageList.postValue(emptyList())
                _weeklyData.postValue(emptyList())
                _categoryData.postValue(emptyList())
                _hourlyData.postValue(emptyList())
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    private fun getTimeRange(period: String): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        val endTime = calendar.timeInMillis
        
        when (period) {
            "Today" -> {
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
            }
            "This Week" -> {
                calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
            }
            "This Month" -> {
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
            }
            else -> { // Last 7 days
                calendar.add(Calendar.DAY_OF_YEAR, -7)
            }
        }
        
        return Pair(calendar.timeInMillis, endTime)
    }

    private fun processUsageStats(usageStats: List<UsageStats>): List<AppUsageData> {
        val appUsageMap = mutableMapOf<String, AppUsageData>()

        for (stat in usageStats) {
            if (stat.totalTimeInForeground > 0) {
                val packageName = stat.packageName
                
                try {
                    val appInfo = packageManager.getApplicationInfo(packageName, 0)
                    val appName = packageManager.getApplicationLabel(appInfo).toString()
                    val appIcon = packageManager.getApplicationIcon(appInfo)

                    val existingData = appUsageMap[packageName]
                    if (existingData != null) {
                        appUsageMap[packageName] = existingData.copy(
                            usageTimeMinutes = existingData.usageTimeMinutes + (stat.totalTimeInForeground / 60000),
                            openCount = existingData.openCount + 1,
                            lastUsed = maxOf(existingData.lastUsed, stat.lastTimeUsed)
                        )
                    } else {
                        appUsageMap[packageName] = AppUsageData(
                            packageName = packageName,
                            appName = appName,
                            appIcon = appIcon,
                            usageTimeMinutes = stat.totalTimeInForeground / 60000,
                            openCount = 1,
                            lastUsed = stat.lastTimeUsed,
                            category = getAppCategory(packageName)
                        )
                    }
                } catch (e: PackageManager.NameNotFoundException) {
                    // App not found, skip
                }
            }
        }

        return appUsageMap.values.sortedByDescending { it.usageTimeMinutes }
    }

    private fun generateWeeklyData(period: String): List<DailyUsageData> {
        // Generate sample weekly data - in real implementation, this would aggregate usage stats by day
        val weeklyData = mutableListOf<DailyUsageData>()
        val calendar = Calendar.getInstance()
        
        for (i in 6 downTo 0) {
            calendar.add(Calendar.DAY_OF_YEAR, -1)
            val dayName = when (calendar.get(Calendar.DAY_OF_WEEK)) {
                Calendar.SUNDAY -> "Sun"
                Calendar.MONDAY -> "Mon"
                Calendar.TUESDAY -> "Tue"
                Calendar.WEDNESDAY -> "Wed"
                Calendar.THURSDAY -> "Thu"
                Calendar.FRIDAY -> "Fri"
                Calendar.SATURDAY -> "Sat"
                else -> "Unknown"
            }
            
            // Generate sample data - replace with actual usage calculation
            val totalMinutes = (60..300).random().toLong()
            weeklyData.add(DailyUsageData(dayName, totalMinutes))
        }
        
        return weeklyData.asReversed()
    }

    private fun generateCategoryData(appUsageList: List<AppUsageData>): List<CategoryUsageData> {
        val categoryMap = appUsageList.groupBy { it.category }
        val totalUsage = appUsageList.sumOf { it.usageTimeMinutes }.toFloat()
        
        return categoryMap.map { (category, apps) ->
            val categoryUsage = apps.sumOf { it.usageTimeMinutes }
            val percentage = if (totalUsage > 0) (categoryUsage / totalUsage) * 100 else 0f
            CategoryUsageData(category, percentage)
        }.sortedByDescending { it.percentage }
    }

    private fun generateHourlyData(period: String): List<HourlyUsageData> {
        // Generate sample hourly data - in real implementation, this would aggregate usage by hour
        val hourlyData = mutableListOf<HourlyUsageData>()
        
        for (hour in 0..23) {
            val usageMinutes = when (hour) {
                in 0..6 -> (0..10).random().toLong() // Low usage during sleep
                in 7..9 -> (20..60).random().toLong() // Morning usage
                in 10..12 -> (30..90).random().toLong() // Mid-morning
                in 13..17 -> (40..120).random().toLong() // Afternoon peak
                in 18..22 -> (50..150).random().toLong() // Evening peak
                else -> (5..30).random().toLong() // Late night
            }
            hourlyData.add(HourlyUsageData(hour, usageMinutes))
        }
        
        return hourlyData
    }

    private fun getAppCategory(packageName: String): String {
        return when {
            packageName.contains("instagram") || packageName.contains("facebook") || 
            packageName.contains("twitter") || packageName.contains("snapchat") ||
            packageName.contains("tiktok") || packageName.contains("linkedin") -> "Social"
            
            packageName.contains("youtube") || packageName.contains("netflix") || 
            packageName.contains("spotify") || packageName.contains("music") ||
            packageName.contains("video") || packageName.contains("media") -> "Entertainment"
            
            packageName.contains("chrome") || packageName.contains("firefox") || 
            packageName.contains("browser") || packageName.contains("edge") -> "Browser"
            
            packageName.contains("game") || packageName.contains("play") ||
            packageName.contains("puzzle") || packageName.contains("casino") -> "Games"
            
            packageName.contains("whatsapp") || packageName.contains("telegram") || 
            packageName.contains("messenger") || packageName.contains("discord") ||
            packageName.contains("slack") || packageName.contains("teams") -> "Communication"
            
            packageName.contains("camera") || packageName.contains("photo") || 
            packageName.contains("gallery") || packageName.contains("editing") -> "Photography"
            
            packageName.contains("maps") || packageName.contains("navigation") || 
            packageName.contains("uber") || packageName.contains("transport") -> "Navigation"
            
            packageName.contains("shopping") || packageName.contains("amazon") || 
            packageName.contains("ebay") || packageName.contains("store") -> "Shopping"
            
            else -> "Other"
        }
    }
}

// Data classes for different usage statistics
data class AppUsageData(
    val packageName: String,
    val appName: String,
    val appIcon: android.graphics.drawable.Drawable,
    val usageTimeMinutes: Long,
    val openCount: Int,
    val lastUsed: Long,
    val category: String
)

data class DailyUsageData(
    val dayName: String,
    val totalMinutes: Long
)

data class CategoryUsageData(
    val categoryName: String,
    val percentage: Float
)

data class HourlyUsageData(
    val hour: Int,
    val usageMinutes: Long
)
