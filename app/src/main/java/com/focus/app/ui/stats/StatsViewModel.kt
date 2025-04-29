package com.focus.app.ui.stats

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.focus.app.data.AppDatabase
import com.focus.app.data.AppStatCount
import com.focus.app.data.BlockedContentEventDao
import com.focus.app.data.ContentTypeStatCount
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

class StatsViewModel(application: Application) : AndroidViewModel(application) {

    private lateinit var blockedContentEventDao: BlockedContentEventDao
    
    private val _selectedDate = MutableLiveData<Date>()
    val selectedDate: LiveData<Date> = _selectedDate
    
    private val _blockedContentCount = MutableLiveData<Int>()
    val blockedContentCount: LiveData<Int> = _blockedContentCount
    
    private val _estimatedTimeSaved = MutableLiveData<Int>()
    val estimatedTimeSaved: LiveData<Int> = _estimatedTimeSaved
    
    private val _appStats = MutableLiveData<Map<String, Int>>()
    val appStats: LiveData<Map<String, Int>> = _appStats
    
    private val _contentTypeStats = MutableLiveData<Map<String, Int>>()
    val contentTypeStats: LiveData<Map<String, Int>> = _contentTypeStats
    
    private val calendar = Calendar.getInstance()

    init {
        val database = AppDatabase.getInstance(application)
        blockedContentEventDao = database.blockedContentEventDao()
        
        // Set current date as default
        _selectedDate.value = calendar.time
        
        // Load stats for current date
        loadStatsForSelectedDate()
    }
    
    fun loadNextDay() {
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        
        // Don't go beyond today
        val today = Calendar.getInstance()
        if (calendar.after(today)) {
            calendar.time = today.time
        }
        
        _selectedDate.value = calendar.time
        loadStatsForSelectedDate()
    }
    
    fun loadPreviousDay() {
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        _selectedDate.value = calendar.time
        loadStatsForSelectedDate()
    }
    
    private fun loadStatsForSelectedDate() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Get start and end dates for the selected day
                val startDate = getStartOfDay(calendar.time)
                val endDate = getEndOfDay(calendar.time)
                
                // Get block count for the selected day
                val blockCount = blockedContentEventDao.getBlockCountForPeriod(startDate, endDate)
                _blockedContentCount.postValue(blockCount)
                
                // Estimate time saved (assume 2 minutes per block)
                _estimatedTimeSaved.postValue(blockCount * 2)
                
                // Get real app stats from database
                val appStatsList = blockedContentEventDao.getAppStatsForPeriod(startDate, endDate)
                // Convert to map and readable app names
                val formattedAppStats = appStatsList.associate { getAppName(it.appPackage) to it.count }
                _appStats.postValue(formattedAppStats)
                
                // Get real content type stats from database
                val contentTypeStatsList = blockedContentEventDao.getContentTypeStatsForPeriod(startDate, endDate)
                // Convert to map
                val contentTypeStatsMap = contentTypeStatsList.associate { it.contentType to it.count }
                _contentTypeStats.postValue(contentTypeStatsMap)
            } catch (e: Exception) {
                // Log the exception and handle errors
                android.util.Log.e("StatsViewModel", "Error loading stats: ${e.message}")
                e.printStackTrace()
                
                // Set default values for UI
                _blockedContentCount.postValue(0)
                _estimatedTimeSaved.postValue(0)
                _appStats.postValue(emptyMap())
                _contentTypeStats.postValue(emptyMap())
            }
        }
    }
    
    private fun getStartOfDay(date: Date): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.time
    }
    
    private fun getEndOfDay(date: Date): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        return calendar.time
    }
    
    private fun getAppName(packageName: String): String {
        return when (packageName) {
            "com.instagram.android" -> "Instagram"
            "com.google.android.youtube" -> "YouTube"
            "com.snapchat.android" -> "Snapchat"
            "com.zhiliaoapp.musically" -> "TikTok"
            "com.facebook.katana" -> "Facebook"
            "com.twitter.android" -> "Twitter"
            else -> packageName
        }
    }
}
