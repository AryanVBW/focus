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
        const val KEY_NORMAL_MODE = "normal_mode"
        const val KEY_CURRENT_APP_MODE = "current_app_mode" // "normal" or "focus"
        const val KEY_SHOW_BLOCK_NOTIFICATIONS = "show_block_notifications"
        
        // Normal Mode features
        const val KEY_USAGE_ANALYTICS_ENABLED = "usage_analytics_enabled"
        const val KEY_SMART_LIMITS_ENABLED = "smart_limits_enabled"
        const val KEY_USAGE_INSIGHTS_ENABLED = "usage_insights_enabled"
        
        // Focus Mode features
        const val KEY_CONTENT_BLOCKING_ENABLED = "content_blocking_enabled"
        const val KEY_CONTENT_FILTERING_ENABLED = "content_filtering_enabled"
        const val KEY_NOTIFICATION_CONTROL_ENABLED = "notification_control_enabled"
        
        // Timer and break settings
        const val KEY_TIMER_LENGTH = "timer_length"
        const val KEY_SHORT_BREAK_LENGTH = "short_break_length"
        const val KEY_LONG_BREAK_LENGTH = "long_break_length"
        const val KEY_MOTIVATION_QUOTES_ENABLED = "motivation_quotes_enabled"
        const val KEY_BREAK_REMINDERS_ENABLED = "break_reminders_enabled"
        
        // App mode values
        const val APP_MODE_NORMAL = "normal"
        const val APP_MODE_FOCUS = "focus"
        
        // Blocking action preferences
        const val KEY_BLOCKING_ACTION = "blocking_action"
        const val BLOCKING_ACTION_CLOSE_PLAYER = "close_player"
        const val BLOCKING_ACTION_CLOSE_APP = "close_app"
        const val BLOCKING_ACTION_LOCK_SCREEN = "lock_screen"
        
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
        const val KEY_CONTENT_SPOTLIGHT = "content_spotlight"
        
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
        const val CONTENT_TYPE_SPOTLIGHT = "spotlight"
        
        const val KEY_BLOCKED_APPS = "blocked_apps_set"
        const val KEY_BLOCK_ADULT_CONTENT = "block_adult_content"
        
        // Onboarding preferences
        const val KEY_FIRST_TIME_USER = "first_time_user"
        const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
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
     * Get current app mode (normal or focus)
     */
    fun getCurrentAppMode(): String {
        return prefs.getString(KEY_CURRENT_APP_MODE, APP_MODE_NORMAL) ?: APP_MODE_NORMAL
    }
    
    /**
     * Set current app mode
     */
    fun setCurrentAppMode(mode: String) {
        prefs.edit().putString(KEY_CURRENT_APP_MODE, mode).apply()
        // Update focus mode flag for backward compatibility
        prefs.edit().putBoolean(KEY_FOCUS_MODE, mode == APP_MODE_FOCUS).apply()
    }
    
    /**
     * Check if Normal Mode features are enabled
     */
    fun isUsageAnalyticsEnabled(): Boolean {
        return prefs.getBoolean(KEY_USAGE_ANALYTICS_ENABLED, true)
    }
    
    fun isSmartLimitsEnabled(): Boolean {
        return prefs.getBoolean(KEY_SMART_LIMITS_ENABLED, true)
    }
    
    fun isUsageInsightsEnabled(): Boolean {
        return prefs.getBoolean(KEY_USAGE_INSIGHTS_ENABLED, true)
    }
    
    /**
     * Check if Focus Mode features are enabled  
     */
    fun isContentBlockingEnabled(): Boolean {
        return prefs.getBoolean(KEY_CONTENT_BLOCKING_ENABLED, true)
    }
    
    fun isContentFilteringEnabled(): Boolean {
        return prefs.getBoolean(KEY_CONTENT_FILTERING_ENABLED, true)
    }
    
    fun isNotificationControlEnabled(): Boolean {
        return prefs.getBoolean(KEY_NOTIFICATION_CONTROL_ENABLED, true)
    }
    
    /**
     * Enable/disable Normal Mode features
     */
    fun setUsageAnalyticsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_USAGE_ANALYTICS_ENABLED, enabled).apply()
    }
    
    fun setSmartLimitsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_SMART_LIMITS_ENABLED, enabled).apply()
    }
    
    fun setUsageInsightsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_USAGE_INSIGHTS_ENABLED, enabled).apply()
    }
    
    /**
     * Enable/disable Focus Mode features
     */
    fun setContentBlockingEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_CONTENT_BLOCKING_ENABLED, enabled).apply()
    }
    
    fun setContentFilteringEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_CONTENT_FILTERING_ENABLED, enabled).apply()
    }
    
    fun setNotificationControlEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_NOTIFICATION_CONTROL_ENABLED, enabled).apply()
    }
    
    /**
     * Check if a specific app should be monitored
     */
    fun isAppMonitored(packageName: String): Boolean {
        val currentMode = getCurrentAppMode()
        
        // In focus mode, monitor all known apps for blocking
        if (currentMode == APP_MODE_FOCUS && isContentBlockingEnabled()) {
            return isKnownPackage(packageName)
        }
        
        // In normal mode, monitor based on individual settings
        if (currentMode == APP_MODE_NORMAL && isUsageAnalyticsEnabled()) {
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
        
        return false
    }
    
    /**
     * Check if a content type should be blocked
     */
    fun shouldBlockContentType(contentType: String): Boolean {
        val currentMode = getCurrentAppMode()
        
        // In focus mode, block all content types if content blocking is enabled
        if (currentMode == APP_MODE_FOCUS && isContentBlockingEnabled()) {
            return true
        }
        
        // In normal mode, content is not blocked but tracked for analytics
        if (currentMode == APP_MODE_NORMAL) {
            return false
        }
        
        // Legacy fallback
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
            CONTENT_TYPE_SPOTLIGHT -> 
                prefs.getBoolean(KEY_CONTENT_SPOTLIGHT, true)
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
    
    /**
     * Get the blocking action preference
     */
    fun getBlockingAction(): String {
        return prefs.getString(KEY_BLOCKING_ACTION, BLOCKING_ACTION_CLOSE_PLAYER) ?: BLOCKING_ACTION_CLOSE_PLAYER
    }
    
    /**
     * Set the blocking action preference
     */
    fun setBlockingAction(action: String) {
        prefs.edit().putString(KEY_BLOCKING_ACTION, action).apply()
    }
    
    /**
     * Enhanced detection system methods
     */
    
    /**
     * Check if enhanced video detection is enabled
     */
    fun isEnhancedVideoDetectionEnabled(): Boolean {
        return prefs.getBoolean("enhanced_video_detection_enabled", true)
    }
    
    /**
     * Check if scroll blocking is enabled for a specific app
     */
    fun isScrollBlockingEnabled(packageName: String): Boolean {
        val currentMode = getCurrentAppMode()
        if (currentMode == APP_MODE_FOCUS && isContentBlockingEnabled()) {
            return prefs.getBoolean("scroll_blocking_${packageName}", true)
        }
        return false
    }
    
    /**
     * Check if content should be blocked for a specific app and content type
     */
    fun shouldBlockContent(packageName: String, contentType: String): Boolean {
        val currentMode = getCurrentAppMode()
        
        // In focus mode, block based on content blocking settings
        if (currentMode == APP_MODE_FOCUS && isContentBlockingEnabled()) {
            return shouldBlockContentType(contentType) && isAppMonitored(packageName)
        }
        
        // In normal mode, don't block content
        return false
    }
    
    /**
     * Get minimum confidence threshold for video detection
     */
    fun getVideoDetectionConfidenceThreshold(): Float {
        return prefs.getFloat("video_detection_confidence_threshold", 0.6f)
    }
    
    /**
     * Check if counter-scrolling is enabled
     */
    fun isCounterScrollingEnabled(): Boolean {
        return prefs.getBoolean("counter_scrolling_enabled", true)
    }
    
    /**
     * Check if app redirect blocking is enabled
     */
    fun isAppRedirectEnabled(): Boolean {
        return prefs.getBoolean("app_redirect_enabled", true)
    }
    
    /**
     * App-specific content blocking methods
     */
    fun isInstagramReelsBlocked(): Boolean {
        return prefs.getBoolean("instagram_reels_blocked", true)
    }
    
    fun isInstagramStoriesBlocked(): Boolean {
        return prefs.getBoolean("instagram_stories_blocked", true)
    }
    
    fun isInstagramExploreBlocked(): Boolean {
        return prefs.getBoolean("instagram_explore_blocked", true)
    }
    
    fun isYouTubeShortsBlocked(): Boolean {
        return prefs.getBoolean("youtube_shorts_blocked", true)
    }
    
    fun isYouTubeExploreBlocked(): Boolean {
        return prefs.getBoolean("youtube_explore_blocked", true)
    }
    
    fun isSnapchatStoriesBlocked(): Boolean {
        return prefs.getBoolean("snapchat_stories_blocked", true)
    }
    
    fun isSnapchatDiscoverBlocked(): Boolean {
        return prefs.getBoolean("snapchat_discover_blocked", true)
    }
    
    fun isTikTokBlocked(): Boolean {
        return prefs.getBoolean("tiktok_blocked", true)
    }
    
    fun isFacebookReelsBlocked(): Boolean {
        return prefs.getBoolean("facebook_reels_blocked", true)
    }
    
    fun isFacebookStoriesBlocked(): Boolean {
        return prefs.getBoolean("facebook_stories_blocked", true)
    }
    
    fun isTwitterExploreBlocked(): Boolean {
        return prefs.getBoolean("twitter_explore_blocked", true)
    }
    
    /**
     * Timer and break length settings
     */
    fun getTimerLength(): Int {
        return prefs.getInt(KEY_TIMER_LENGTH, 25) // Default 25 minutes
    }
    
    fun setTimerLength(minutes: Int) {
        prefs.edit().putInt(KEY_TIMER_LENGTH, minutes).apply()
    }
    
    fun getShortBreakLength(): Int {
        return prefs.getInt(KEY_SHORT_BREAK_LENGTH, 5) // Default 5 minutes
    }
    
    fun setShortBreakLength(minutes: Int) {
        prefs.edit().putInt(KEY_SHORT_BREAK_LENGTH, minutes).apply()
    }
    
    fun getLongBreakLength(): Int {
        return prefs.getInt(KEY_LONG_BREAK_LENGTH, 15) // Default 15 minutes
    }
    
    fun setLongBreakLength(minutes: Int) {
        prefs.edit().putInt(KEY_LONG_BREAK_LENGTH, minutes).apply()
    }
    
    /**
     * Notification settings
     */
    fun getMotivationQuotesEnabled(): Boolean {
        return prefs.getBoolean(KEY_MOTIVATION_QUOTES_ENABLED, true)
    }
    
    fun setMotivationQuotesEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_MOTIVATION_QUOTES_ENABLED, enabled).apply()
    }
    
    fun getBreakRemindersEnabled(): Boolean {
        return prefs.getBoolean(KEY_BREAK_REMINDERS_ENABLED, true)
    }
    
    fun setBreakRemindersEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_BREAK_REMINDERS_ENABLED, enabled).apply()
    }
    
    /**
     * User session management
     */
    fun clearUserSession() {
        // Clear user-specific data but keep app settings
        prefs.edit()
            .remove("user_logged_in")
            .remove("user_id")
            .remove("user_email")
            .remove("user_name")
            .apply()
    }
    
    /**
     * Enhanced Blocking Manager Settings
     * These settings control the new blocking mechanisms as requested
     */
    
    // Enhanced blocking strategy preferences
    fun isOverlayBlockingEnabled(): Boolean {
        return prefs.getBoolean("overlay_blocking_enabled", true)
    }
    
    fun setOverlayBlockingEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("overlay_blocking_enabled", enabled).apply()
    }
    
    fun isUITraversalEnabled(): Boolean {
        return prefs.getBoolean("ui_traversal_enabled", true)
    }
    
    fun setUITraversalEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("ui_traversal_enabled", enabled).apply()
    }
    
    fun isGestureInjectionEnabled(): Boolean {
        return prefs.getBoolean("gesture_injection_enabled", true)
    }
    
    fun setGestureInjectionEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("gesture_injection_enabled", enabled).apply()
    }
    
    fun isDisruptionActivityEnabled(): Boolean {
        return prefs.getBoolean("disruption_activity_enabled", true)
    }
    
    fun setDisruptionActivityEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("disruption_activity_enabled", enabled).apply()
    }
    
    /**
     * Temporary unblock functionality
     */
    fun isTemporaryUnblockEnabled(): Boolean {
        return prefs.getBoolean("temporary_unblock_enabled", true)
    }
    
    fun setTemporaryUnblockEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("temporary_unblock_enabled", enabled).apply()
    }
    
    fun getTemporaryUnblockDuration(): Long {
        return prefs.getLong("temporary_unblock_duration", 5 * 60 * 1000L) // Default 5 minutes
    }
    
    fun setTemporaryUnblockDuration(durationMs: Long) {
        prefs.edit().putLong("temporary_unblock_duration", durationMs).apply()
    }
    
    fun setTemporaryUnblock(packageName: String, durationMs: Long) {
        val expiryTime = System.currentTimeMillis() + durationMs
        prefs.edit().putLong("temp_unblock_${packageName}", expiryTime).apply()
    }
    
    fun isTemporarilyUnblocked(packageName: String): Boolean {
        val expiryTime = prefs.getLong("temp_unblock_${packageName}", 0L)
        val isUnblocked = System.currentTimeMillis() < expiryTime
        
        // Clean up expired entries
        if (!isUnblocked && expiryTime > 0) {
            prefs.edit().remove("temp_unblock_${packageName}").apply()
        }
        
        return isUnblocked
    }
    
    /**
     * Disruption activity settings
     */
    fun getDisruptionCooldownTime(): Long {
        return prefs.getLong("disruption_cooldown_time", 0L) // Default: no auto-dismiss
    }
    
    fun setDisruptionCooldownTime(timeMs: Long) {
        prefs.edit().putLong("disruption_cooldown_time", timeMs).apply()
    }
    
    /**
     * Custom blocklist management
     */
    fun getCustomBlockedApps(): Set<String> {
        return prefs.getStringSet("custom_blocked_apps", emptySet()) ?: emptySet()
    }
    
    fun addCustomBlockedApp(packageName: String) {
        val currentApps = getCustomBlockedApps().toMutableSet()
        currentApps.add(packageName)
        prefs.edit().putStringSet("custom_blocked_apps", currentApps).apply()
    }
    
    fun removeCustomBlockedApp(packageName: String) {
        val currentApps = getCustomBlockedApps().toMutableSet()
        currentApps.remove(packageName)
        prefs.edit().putStringSet("custom_blocked_apps", currentApps).apply()
    }
    
    fun isCustomBlockedApp(packageName: String): Boolean {
        return getCustomBlockedApps().contains(packageName)
    }
    
    /**
     * Enhanced blocking detection settings
     */
    fun getBlockingAggressiveness(): Int {
        return prefs.getInt("blocking_aggressiveness", 2) // 1=Low, 2=Medium, 3=High
    }
    
    fun setBlockingAggressiveness(level: Int) {
        prefs.edit().putInt("blocking_aggressiveness", level).apply()
    }
    
    fun isHapticFeedbackEnabled(): Boolean {
        return prefs.getBoolean("haptic_feedback_enabled", true)
    }
    
    fun setHapticFeedbackEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("haptic_feedback_enabled", enabled).apply()
    }
    
    /**
     * Onboarding management
     */
    fun isFirstTimeUser(): Boolean {
        return prefs.getBoolean(KEY_FIRST_TIME_USER, true)
    }
    
    fun setFirstTimeUser(isFirstTime: Boolean) {
        prefs.edit().putBoolean(KEY_FIRST_TIME_USER, isFirstTime).apply()
    }
    
    fun isOnboardingCompleted(): Boolean {
        return prefs.getBoolean(KEY_ONBOARDING_COMPLETED, false)
    }
    
    fun setOnboardingCompleted(completed: Boolean) {
        prefs.edit().putBoolean(KEY_ONBOARDING_COMPLETED, completed).apply()
    }
    
    /**
     * Migration and initialization functions
     */
    
    /**
     * Ensure the blocking action is set to close_player by default for better UX.
     * This prevents the entire app from closing when short videos are detected.
     * Should be called on app startup.
     */
    fun ensurePlayerCloseDefault() {
        val currentAction = getBlockingAction()
        val migrationDone = prefs.getBoolean("migration_blocking_action_v1", false)
        
        if (!migrationDone) {
            // First time migration - force set to close_player
            if (currentAction != BLOCKING_ACTION_CLOSE_PLAYER) {
                android.util.Log.i("AppSettings", "Migration: Changing blocking action from '$currentAction' to 'close_player' for better UX")
                setBlockingAction(BLOCKING_ACTION_CLOSE_PLAYER)
            }
            prefs.edit().putBoolean("migration_blocking_action_v1", true).apply()
            android.util.Log.i("AppSettings", "Migration complete: Blocking action set to close_player")
        }
    }
    
    /**
     * Get the effective blocking action for a specific content type.
     * For short video content, always returns CLOSE_PLAYER regardless of user setting.
     */
    fun getEffectiveBlockingAction(contentType: String): String {
        // For short video content, always use close_player to avoid closing entire app
        if (isShortVideoContent(contentType)) {
            return BLOCKING_ACTION_CLOSE_PLAYER
        }
        // For other content, respect user's preference
        return getBlockingAction()
    }
    
    /**
     * Check if content type is short video content (reels, shorts, stories)
     */
    private fun isShortVideoContent(contentType: String): Boolean {
        return contentType in listOf(
            CONTENT_TYPE_REELS,
            CONTENT_TYPE_SHORTS,
            CONTENT_TYPE_STORIES,
            CONTENT_TYPE_SPOTLIGHT
        )
    }
}
