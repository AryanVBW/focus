package com.aryanvbw.focus.ui.block

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BlockViewModel : ViewModel() {

    private val _socialMediaBlocked = MutableLiveData<Boolean>().apply {
        value = true
    }
    val socialMediaBlocked: LiveData<Boolean> = _socialMediaBlocked

    private val _newsBlocked = MutableLiveData<Boolean>().apply {
        value = true
    }
    val newsBlocked: LiveData<Boolean> = _newsBlocked

    private val _entertainmentBlocked = MutableLiveData<Boolean>().apply {
        value = true
    }
    val entertainmentBlocked: LiveData<Boolean> = _entertainmentBlocked

    private val _shoppingBlocked = MutableLiveData<Boolean>().apply {
        value = true
    }
    val shoppingBlocked: LiveData<Boolean> = _shoppingBlocked

    private val _scheduledBlockingEnabled = MutableLiveData<Boolean>().apply {
        value = true
    }
    val scheduledBlockingEnabled: LiveData<Boolean> = _scheduledBlockingEnabled

    private val _blockedAttempts = MutableLiveData<Int>().apply {
        value = 12
    }
    val blockedAttempts: LiveData<Int> = _blockedAttempts

    private val _blockingMessage = MutableLiveData<String>().apply {
        value = ""
    }
    val blockingMessage: LiveData<String> = _blockingMessage

    private val blockedUrls = mutableSetOf<String>()

    fun toggleSocialMedia(isBlocked: Boolean) {
        _socialMediaBlocked.value = isBlocked
        if (isBlocked) {
            addSocialMediaUrls()
        } else {
            removeSocialMediaUrls()
        }
        updateBlockingMessage("Social Media", isBlocked)
    }

    fun toggleNews(isBlocked: Boolean) {
        _newsBlocked.value = isBlocked
        if (isBlocked) {
            addNewsUrls()
        } else {
            removeNewsUrls()
        }
        updateBlockingMessage("News", isBlocked)
    }

    fun toggleEntertainment(isBlocked: Boolean) {
        _entertainmentBlocked.value = isBlocked
        if (isBlocked) {
            addEntertainmentUrls()
        } else {
            removeEntertainmentUrls()
        }
        updateBlockingMessage("Entertainment", isBlocked)
    }

    fun toggleShopping(isBlocked: Boolean) {
        _shoppingBlocked.value = isBlocked
        if (isBlocked) {
            addShoppingUrls()
        } else {
            removeShoppingUrls()
        }
        updateBlockingMessage("Shopping", isBlocked)
    }

    fun toggleScheduledBlocking(isEnabled: Boolean) {
        _scheduledBlockingEnabled.value = isEnabled
        _blockingMessage.value = if (isEnabled) {
            "Scheduled blocking enabled"
        } else {
            "Scheduled blocking disabled"
        }
    }

    fun blockNow(customUrl: String) {
        if (customUrl.isNotEmpty()) {
            blockedUrls.add(customUrl)
            _blockingMessage.value = "Custom URL blocked: $customUrl"
        } else {
            // Block all enabled categories
            val categories = mutableListOf<String>()
            if (_socialMediaBlocked.value == true) categories.add("Social Media")
            if (_newsBlocked.value == true) categories.add("News")
            if (_entertainmentBlocked.value == true) categories.add("Entertainment")
            if (_shoppingBlocked.value == true) categories.add("Shopping")
            
            if (categories.isNotEmpty()) {
                _blockingMessage.value = "Blocking activated for: ${categories.joinToString(", ")}"
                incrementBlockedAttempts()
            } else {
                _blockingMessage.value = "No categories selected for blocking"
            }
        }
    }

    private fun addSocialMediaUrls() {
        blockedUrls.addAll(listOf(
            "facebook.com", "instagram.com", "twitter.com", "tiktok.com",
            "snapchat.com", "linkedin.com", "reddit.com"
        ))
    }

    private fun removeSocialMediaUrls() {
        blockedUrls.removeAll(listOf(
            "facebook.com", "instagram.com", "twitter.com", "tiktok.com",
            "snapchat.com", "linkedin.com", "reddit.com"
        ))
    }

    private fun addNewsUrls() {
        blockedUrls.addAll(listOf(
            "cnn.com", "bbc.com", "nytimes.com", "reuters.com",
            "theguardian.com", "washingtonpost.com"
        ))
    }

    private fun removeNewsUrls() {
        blockedUrls.removeAll(listOf(
            "cnn.com", "bbc.com", "nytimes.com", "reuters.com",
            "theguardian.com", "washingtonpost.com"
        ))
    }

    private fun addEntertainmentUrls() {
        blockedUrls.addAll(listOf(
            "youtube.com", "netflix.com", "hulu.com", "twitch.tv",
            "spotify.com", "disney.com"
        ))
    }

    private fun removeEntertainmentUrls() {
        blockedUrls.removeAll(listOf(
            "youtube.com", "netflix.com", "hulu.com", "twitch.tv",
            "spotify.com", "disney.com"
        ))
    }

    private fun addShoppingUrls() {
        blockedUrls.addAll(listOf(
            "amazon.com", "ebay.com", "walmart.com", "target.com",
            "bestbuy.com", "etsy.com"
        ))
    }

    private fun removeShoppingUrls() {
        blockedUrls.removeAll(listOf(
            "amazon.com", "ebay.com", "walmart.com", "target.com",
            "bestbuy.com", "etsy.com"
        ))
    }

    private fun updateBlockingMessage(category: String, isBlocked: Boolean) {
        _blockingMessage.value = if (isBlocked) {
            "$category blocking enabled"
        } else {
            "$category blocking disabled"
        }
    }

    private fun incrementBlockedAttempts() {
        val current = _blockedAttempts.value ?: 0
        _blockedAttempts.value = current + 1
    }

    fun getBlockedUrls(): Set<String> {
        return blockedUrls.toSet()
    }
}