package com.aryanvbw.focus.util

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

/**
 * Handles app settings and user preferences
 */
class AppSettings(private val context: Context) {

    private val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    fun getPreferences(): SharedPreferences {
        return prefs
    }

    companion object {
        // App monitoring preferences
        const val KEY_SERVICE_ENABLED = "service_enabled"
        const val KEY_FOCUS_MODE = "focus_mode"
        const val KEY_SHOW_BLOCK_NOTIFICATIONS = "show_block_notifications"
        
        // Apps to monitor
        const val KEY_APP_INSTAGRAM = "app_instagram"
        const val KEY_APP_YOUTUBE = "app_youtube"
        const val KEY_APP_SNAPCHAT = "app_snapchat"
        const val KEY_APP_TIKTOK = "app_tiktok"
        const val KEY_APP_FACEBOOK = "app_facebook"
        const val KEY_APP_TWITTER = "app_twitter"
        
        // Content types to block
        const val KEY_CONTENT_REELS = "content_reels"
        const val KEY_CONTENT_SHORTS = "content_shorts"
        const val KEY_CONTENT_EXPLORE = "content_explore"
        
        // Package names of popular apps
        const val PACKAGE_INSTAGRAM = "com.instagram.android"
        const val PACKAGE_YOUTUBE = "com.google.android.youtube"
        const val PACKAGE_SNAPCHAT = "com.snapchat.android"
        const val PACKAGE_TIKTOK = "com.zhiliaoapp.musically"
        const val PACKAGE_FACEBOOK = "com.facebook.katana"
        const val PACKAGE_TWITTER = "com.twitter.android"
        
        // Content type identifiers
        const val CONTENT_TYPE_REELS = "reels"
        const val CONTENT_TYPE_STORIES = "stories"
        const val CONTENT_TYPE_SHORTS = "shorts"
        const val CONTENT_TYPE_EXPLORE = "explore"
        
        const val KEY_BLOCKED_APPS = "blocked_apps_set"
        const val KEY_BLOCK_ADULT_CONTENT = "block_adult_content"
    }
    
    /**
     * Check if the accessibility service is enabled
     */
    fun isServiceEnabled(): Boolean {
        return prefs.getBoolean(KEY_SERVICE_ENABLED, false)
    }
    
    /**
     * Set the service enabled state
     */
    fun setServiceEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_SERVICE_ENABLED, enabled).apply()
    }
    
    /**
     * Check if focus mode is enabled (blocks all distracting content)
     */
    fun isFocusModeEnabled(): Boolean {
        return prefs.getBoolean(KEY_FOCUS_MODE, false)
    }
    
    /**
     * Check if a specific app should be monitored
     */
    fun isAppMonitored(packageName: String): Boolean {
        // If focus mode is on, monitor all known apps
        if (isFocusModeEnabled()) {
            return isKnownPackage(packageName)
        }
        
        return when (packageName) {
            PACKAGE_INSTAGRAM -> prefs.getBoolean(KEY_APP_INSTAGRAM, true)
            PACKAGE_YOUTUBE -> prefs.getBoolean(KEY_APP_YOUTUBE, true)
            PACKAGE_SNAPCHAT -> prefs.getBoolean(KEY_APP_SNAPCHAT, true)
            PACKAGE_TIKTOK -> prefs.getBoolean(KEY_APP_TIKTOK, true)
            PACKAGE_FACEBOOK -> prefs.getBoolean(KEY_APP_FACEBOOK, true)
            PACKAGE_TWITTER -> prefs.getBoolean(KEY_APP_TWITTER, true)
            else -> false
        }
    }
    
    /**
     * Check if a content type should be blocked
     */
    fun shouldBlockContentType(contentType: String): Boolean {
        // If focus mode is on, block all content types
        if (isFocusModeEnabled()) {
            return true
        }
        
        return when (contentType) {
            CONTENT_TYPE_REELS, CONTENT_TYPE_STORIES -> 
                prefs.getBoolean(KEY_CONTENT_REELS, true)
            CONTENT_TYPE_SHORTS -> 
                prefs.getBoolean(KEY_CONTENT_SHORTS, true)
            CONTENT_TYPE_EXPLORE -> 
                prefs.getBoolean(KEY_CONTENT_EXPLORE, true)
            else -> false
        }
    }
    
    /**
     * Check if notifications should be shown when content is blocked
     */
    fun showBlockNotifications(): Boolean {
        return prefs.getBoolean(KEY_SHOW_BLOCK_NOTIFICATIONS, true)
    }
    
    /**
     * Check if this is a known package that the app can monitor
     */
    private fun isKnownPackage(packageName: String): Boolean {
        return packageName == PACKAGE_INSTAGRAM ||
               packageName == PACKAGE_YOUTUBE ||
               packageName == PACKAGE_SNAPCHAT ||
               packageName == PACKAGE_TIKTOK ||
               packageName == PACKAGE_FACEBOOK ||
               packageName == PACKAGE_TWITTER
    }
    
    /**
     * Get the set of blocked app packages
     */
    fun getBlockedApps(): Set<String> {
        return prefs.getStringSet(KEY_BLOCKED_APPS, emptySet()) ?: emptySet()
    }

    /**
     * Set an app as blocked or unblocked
     */
    fun setAppBlocked(packageName: String, isBlocked: Boolean) {
        val currentBlockedApps = getBlockedApps().toMutableSet()
        if (isBlocked) {
            currentBlockedApps.add(packageName)
        } else {
            currentBlockedApps.remove(packageName)
        }
        prefs.edit().putStringSet(KEY_BLOCKED_APPS, currentBlockedApps).apply()
    }

    // --- Adult Content Blocking --- 
    fun isAdultContentBlockingEnabled(): Boolean {
        return prefs.getBoolean(KEY_BLOCK_ADULT_CONTENT, false) // Default to false
    }
    
    /**
     * Check if a specific app package is in the blocked apps list
     */
    fun isAppBlocked(packageName: String): Boolean {
        return getBlockedApps().contains(packageName)
    }
}
