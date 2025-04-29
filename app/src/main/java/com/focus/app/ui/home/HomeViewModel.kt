package com.focus.app.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.focus.app.FocusApplication
import androidx.lifecycle.viewModelScope
import com.focus.app.data.AppDatabase
import com.focus.app.data.BlockedContentEventDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private lateinit var blockedContentEventDao: BlockedContentEventDao
    
    private val _todayBlockedCount = MutableLiveData<Int>()
    val todayBlockedCount: LiveData<Int> = _todayBlockedCount
    
    private val _totalTimeBlocked = MutableLiveData<Int>()
    val totalTimeBlocked: LiveData<Int> = _totalTimeBlocked

    init {
        val database = AppDatabase.getInstance(application)
        blockedContentEventDao = database.blockedContentEventDao()
        
        loadTodayStats()
    }
    
    fun loadTodayStats() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Get today's date
                val calendar = Calendar.getInstance()
                val startTime = calendar.apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                }.time
                
                // Get today's blocked content count
                val contentCount = blockedContentEventDao.getBlockCountSince(startTime)
                _todayBlockedCount.postValue(contentCount)
                
                // Calculate estimated time saved (assume average of 2 minutes per blocked content)
                _totalTimeBlocked.postValue(contentCount * 2)
            } catch (e: Exception) {
                // Handle exceptions
                _todayBlockedCount.postValue(0)
                _totalTimeBlocked.postValue(0)
            }
        }
    }
}
