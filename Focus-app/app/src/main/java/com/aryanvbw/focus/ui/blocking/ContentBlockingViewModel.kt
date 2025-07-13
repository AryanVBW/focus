package com.aryanvbw.focus.ui.blocking

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.aryanvbw.focus.data.repository.BlockingRepository
import com.aryanvbw.focus.data.model.BlockedContent
import com.aryanvbw.focus.data.model.BlockingStats
import com.aryanvbw.focus.data.model.CategorySettings

class ContentBlockingViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository = BlockingRepository.getInstance(application)
    
    private val _blockedContent = MutableLiveData<List<BlockedContent>>()
    val blockedContent: LiveData<List<BlockedContent>> = _blockedContent
    
    private val _blockingStats = MutableLiveData<BlockingStats>()
    val blockingStats: LiveData<BlockingStats> = _blockingStats
    
    private val _categorySettings = MutableLiveData<CategorySettings>()
    val categorySettings: LiveData<CategorySettings> = _categorySettings
    
    fun loadBlockedContent() {
        // Simulate loading data - replace with actual repository call
        val sampleBlockedContent = listOf(
            BlockedContent(
                id = 1,
                type = "app",
                identifier = "com.instagram.android",
                displayName = "Instagram",
                isEnabled = true
            ),
            BlockedContent(
                id = 2,
                type = "website",
                identifier = "facebook.com",
                displayName = "Facebook",
                isEnabled = true
            ),
            BlockedContent(
                id = 3,
                type = "app",
                identifier = "com.tiktok.android",
                displayName = "TikTok",
                isEnabled = true
            ),
            BlockedContent(
                id = 4,
                type = "website",
                identifier = "youtube.com",
                displayName = "YouTube",
                isEnabled = true
            ),
            BlockedContent(
                id = 5,
                type = "website",
                identifier = "reddit.com",
                displayName = "Reddit",
                isEnabled = false
            )
        )
        _blockedContent.value = sampleBlockedContent
    }
    
    fun loadBlockingStats() {
        // Simulate loading stats - replace with actual repository call
        val stats = BlockingStats(
            blockedAppsCount = 8,
            blockedWebsitesCount = 15,
            focusTimeMinutes = 247, // 4h 7m
            blocksToday = 23
        )
        _blockingStats.value = stats
    }
    
    fun setMasterBlockingEnabled(enabled: Boolean) {
        val currentSettings = _categorySettings.value ?: CategorySettings()
        _categorySettings.value = currentSettings.copy(masterEnabled = enabled)
        // Save to repository
        repository.updateMasterBlocking(enabled)
    }
    
    fun setCategoryBlockingEnabled(category: String, enabled: Boolean) {
        val currentSettings = _categorySettings.value ?: CategorySettings()
        val updatedSettings = when (category) {
            "social_media" -> currentSettings.copy(socialMediaEnabled = enabled)
            "entertainment" -> currentSettings.copy(entertainmentEnabled = enabled)
            "news_media" -> currentSettings.copy(newsMediaEnabled = enabled)
            "adult_content" -> currentSettings.copy(adultContentEnabled = enabled)
            else -> currentSettings
        }
        _categorySettings.value = updatedSettings
        // Save to repository
        repository.updateCategoryBlocking(category, enabled)
    }
    
    fun addBlockedContent(type: String, identifier: String, displayName: String) {
        val currentList = _blockedContent.value?.toMutableList() ?: mutableListOf()
        val newItem = BlockedContent(
            id = (currentList.maxOfOrNull { it.id } ?: 0) + 1,
            type = type,
            identifier = identifier,
            displayName = displayName,
            isEnabled = true
        )
        currentList.add(newItem)
        _blockedContent.value = currentList
        
        // Save to repository
        repository.addBlockedContent(newItem)
        
        // Update stats
        loadBlockingStats()
    }
    
    fun removeBlockedContent(blockedContent: BlockedContent) {
        val currentList = _blockedContent.value?.toMutableList() ?: return
        currentList.remove(blockedContent)
        _blockedContent.value = currentList
        
        // Remove from repository
        repository.removeBlockedContent(blockedContent.id)
        
        // Update stats
        loadBlockingStats()
    }
    
    fun toggleBlockedContent(blockedContent: BlockedContent) {
        val currentList = _blockedContent.value?.toMutableList() ?: return
        val index = currentList.indexOf(blockedContent)
        if (index != -1) {
            currentList[index] = blockedContent.copy(isEnabled = !blockedContent.isEnabled)
            _blockedContent.value = currentList
            
            // Update in repository
            repository.updateBlockedContent(currentList[index])
        }
    }
    
    init {
        // Load initial category settings
        val initialSettings = CategorySettings(
            masterEnabled = true,
            socialMediaEnabled = true,
            entertainmentEnabled = false,
            newsMediaEnabled = false,
            adultContentEnabled = true
        )
        _categorySettings.value = initialSettings
    }
}
