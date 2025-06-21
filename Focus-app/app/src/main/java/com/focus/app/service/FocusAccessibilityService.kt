package com.aryanvbw.focus.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Toast
import com.aryanvbw.focus.data.AppDatabase
import com.aryanvbw.focus.data.BlockedContentEvent
import com.aryanvbw.focus.data.BlockedContentEventDao
import com.aryanvbw.focus.util.AppSettings
import com.aryanvbw.focus.util.ContentDetector
import com.aryanvbw.focus.util.NotificationHelper
import com.aryanvbw.focus.util.BlockingActionHandler
import kotlinx.coroutines.*
import java.net.MalformedURLException
import java.net.URL
import com.aryanvbw.focus.ui.blocker.BlockedPageActivity
import java.util.Date

/**
 * Accessibility service to detect and block distracting content
 * This is the core of the Focus app's functionality
 */
class FocusAccessibilityService : AccessibilityService() {

    private val TAG = "FocusAccessService"
    private lateinit var appSettings: AppSettings
    private lateinit var contentDetector: ContentDetector
    private lateinit var notificationHelper: NotificationHelper
    private lateinit var blockedContentEventDao: BlockedContentEventDao
    private lateinit var blockingActionHandler: BlockingActionHandler
    private val serviceScope = CoroutineScope(Dispatchers.IO)
    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Main + job)
    
    // Event throttling to prevent rapid-fire blocks
    private val lastBlockTime = mutableMapOf<String, Long>()
    private val BLOCK_THROTTLE_MS = 300L // 300ms throttle between blocks for the same package

    companion object {
        private const val TAG = "FocusAccessibilityService"
        
        // Supported browsers for adult content filtering
        private val BROWSER_PACKAGES = setOf(
            "com.android.chrome", // Google Chrome
            "org.mozilla.firefox", // Firefox
            "com.opera.browser", // Opera
            "com.sec.android.app.sbrowser", // Samsung Internet
            "com.duckduckgo.mobile.android" // DuckDuckGo
            // Add other browser packages as needed
        )
        
        // Social media apps with selective blocking
        private const val INSTAGRAM_PACKAGE = "com.instagram.android"
        private const val SNAPCHAT_PACKAGE = "com.snapchat.android"
        private const val YOUTUBE_PACKAGE = "com.google.android.youtube"
        private const val TIKTOK_PACKAGE = "com.zhiliaoapp.musically"
        
        // Content-specific blocking strategy constants
        private const val BLOCK_STRATEGY_REDIRECT = 1 // Redirect to safe section
        private const val BLOCK_STRATEGY_SHOW_PAGE = 2 // Show blocking page (for focus mode)
        private const val BLOCK_STRATEGY_BACK = 3 // Use back button (legacy)
        
        // Instagram UI identifiers (these may change with app updates)
        private val INSTAGRAM_REELS_IDENTIFIERS = setOf(
            "reels_tray", "reels_viewer", "reels_tab", "clips_tab", 
            "com.instagram.android:id/clips_tab", "com.instagram.android:id/clips_viewer",
            "com.instagram.android:id/reels_viewer", "com.instagram.android:id/reels_tray"
        )
        
        // Instagram safe navigation targets
        private val INSTAGRAM_SAFE_TARGETS = setOf(
            "com.instagram.android:id/tab_home",
            "com.instagram.android:id/navigation_home",
            "com.instagram.android:id/tab_search",
            "com.instagram.android:id/navigation_search",
            "com.instagram.android:id/tab_avatar",
            "com.instagram.android:id/navigation_profile"
        )
        
        // Snapchat UI identifiers (these may change with app updates)
        private val SNAPCHAT_STORIES_IDENTIFIERS = setOf(
            "stories_preview", "story_viewer", "spotlight_tab", 
            "com.snapchat.android:id/stories_tab", "com.snapchat.android:id/spotlight_tab",
            "com.snapchat.android:id/stories_container", "com.snapchat.android:id/spotlight_container"
        )
        
        // Snapchat safe navigation targets
        private val SNAPCHAT_SAFE_TARGETS = setOf(
            "com.snapchat.android:id/chat_tab", // Chat tab
            "com.snapchat.android:id/camera_tab", // Camera tab
            "com.snapchat.android:id/tab_bar_camera_option" // Camera option
        )
    }

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "FocusAccessibilityService: onCreate() called - Service starting...")
        
        try {
            // Initialize core components
            appSettings = AppSettings(this)
            contentDetector = ContentDetector(appSettings)
            notificationHelper = NotificationHelper(this)
            blockingActionHandler = BlockingActionHandler(this, this)
            
            // Initialize database components
            val database = AppDatabase.getInstance(this)
            blockedContentEventDao = database.blockedContentEventDao()
            
            Log.i(TAG, "FocusAccessibilityService: All components initialized successfully")
        } catch (e: Exception) {
            Log.e(TAG, "FocusAccessibilityService: Critical error during initialization", e)
            // Don't crash the service, but log the error for debugging
        }
    }
    
    private fun initializeComponents() {
        // Initialize app settings
        if (!::appSettings.isInitialized) {
            appSettings = AppSettings(this)
        }
        
        // Initialize content detector
        if (!::contentDetector.isInitialized) {
            contentDetector = ContentDetector(appSettings)
        }
        
        // Initialize notification helper
        if (!::notificationHelper.isInitialized) {
            notificationHelper = NotificationHelper(this)
        }
        
        // Initialize blocking action handler
        if (!::blockingActionHandler.isInitialized) {
            blockingActionHandler = BlockingActionHandler(this, this)
        }
        
        // Initialize database safely
        if (!::blockedContentEventDao.isInitialized) {
            try {
                val database = AppDatabase.getInstance(this)
                blockedContentEventDao = database.blockedContentEventDao()
            } catch (e: Exception) {
                Log.e(TAG, "Error initializing database: ${e.message}")
                // Continue without database access - app can still function without logging events
            }
        }
        
        Log.d(TAG, "Service initialized successfully")
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.i(TAG, "FocusAccessibilityService: onServiceConnected() - Service is now active and ready")
        
        try {
            // Verify service configuration
            val serviceInfo = serviceInfo
            Log.d(TAG, "Service info - Events: ${serviceInfo?.eventTypes}, Flags: ${serviceInfo?.flags}")
            
            // Show a brief notification that the service is active
            if (::notificationHelper.isInitialized) {
                notificationHelper.showTemporaryNotification(
                    "Focus Active",
                    "Focus is now protecting you from distractions",
                    false
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in onServiceConnected", e)
        }

        // Start the foreground monitoring service
        Log.d(TAG, "Starting FocusMonitorService.")
        val intent = Intent(this, FocusMonitorService::class.java)
        try {
            startService(intent)
            Log.d(TAG, "FocusMonitorService started successfully.")
        } catch (e: Exception) {
            Log.e(TAG, "Error starting FocusMonitorService: ${e.message}")
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        // Enhanced logging for debugging event frequency and performance
        Log.d(TAG, "Event: ${AccessibilityEvent.eventTypeToString(event.eventType)}, Pkg: ${event.packageName}, Class: ${event.className}")
        
        // Get package name and root node
        val packageName = event.packageName?.toString() ?: return
        val rootNode = rootInActiveWindow ?: return
        
        try {
            // Check if all required components are initialized
            if (!::appSettings.isInitialized || !::contentDetector.isInitialized) {
                Log.w(TAG, "Skipping accessibility event - service not fully initialized")
                return
            }
            
            // Event throttling - skip if we recently blocked this package
            val currentTime = System.currentTimeMillis()
            val lastBlock = lastBlockTime[packageName] ?: 0L
            if (currentTime - lastBlock < BLOCK_THROTTLE_MS) {
                Log.v(TAG, "Throttling event for $packageName (${currentTime - lastBlock}ms ago)")
                return
            }
            
            // --- FOCUS MODE ON: Handle full app blocking ---
            if (appSettings.isFocusModeEnabled()) {
                // In Focus Mode, use the user's selected blocking action for blocked apps
                if (appSettings.isAppBlocked(packageName)) {
                    Log.i(TAG, "Focus Mode: Blocking app with user action: $packageName")
                    blockContentWithAction(packageName, "blocked_app")
                    return
                }
                
                // For Instagram and Snapchat in Focus Mode, block reels/stories with user action
                if (packageName == INSTAGRAM_PACKAGE && isInstagramReelsView(rootNode)) {
                    Log.i(TAG, "Focus Mode: Blocking Instagram Reels with user action")
                    blockContentWithAction(packageName, AppSettings.CONTENT_TYPE_REELS)
                    return
                }
                
                if (packageName == SNAPCHAT_PACKAGE && isSnapchatStoriesView(rootNode)) {
                    Log.i(TAG, "Focus Mode: Blocking Snapchat Stories with user action")
                    blockContentWithAction(packageName, AppSettings.CONTENT_TYPE_STORIES)
                    return
                }
                
                if (packageName == YOUTUBE_PACKAGE && isYouTubeShorts(rootNode)) {
                    Log.i(TAG, "Focus Mode: Blocking YouTube Shorts with user action")
                    blockContentWithAction(packageName, AppSettings.CONTENT_TYPE_SHORTS)
                    return
                }
            }
            // --- NORMAL MODE: Content-specific blocking for social apps ---
            else {
                // Check Instagram for Reels
                if (packageName == INSTAGRAM_PACKAGE) {
                    if (isInstagramReelsView(rootNode)) {
                        Log.i(TAG, "Normal Mode: Detected Instagram Reels - redirecting to safe section")
                        val blocked = redirectToSafeAppSection(rootNode, packageName, AppSettings.CONTENT_TYPE_REELS)
                        if (blocked) {
                            lastBlockTime[packageName] = System.currentTimeMillis()
                        }
                        return
                    }
                }
                
                // Check Snapchat for Stories/Spotlight
                if (packageName == SNAPCHAT_PACKAGE) {
                    if (isSnapchatStoriesView(rootNode)) {
                        Log.i(TAG, "Normal Mode: Detected Snapchat Stories/Spotlight - redirecting to safe section")
                        val blocked = redirectToSafeAppSection(rootNode, packageName, AppSettings.CONTENT_TYPE_STORIES)
                        if (blocked) {
                            lastBlockTime[packageName] = System.currentTimeMillis()
                        }
                        return
                    }
                }
                
                // Check YouTube for Shorts
                if (packageName == YOUTUBE_PACKAGE) {
                    if (isYouTubeShorts(rootNode)) {
                        Log.i(TAG, "Normal Mode: Detected YouTube Shorts - redirecting to safe section")
                        val blocked = blockYouTubeShorts(rootNode) // Already implements redirection
                        if (blocked) {
                            lastBlockTime[packageName] = System.currentTimeMillis()
                        }
                        return
                    }
                }
            }
            
            // --- Content Detection for Adult Content ---
            // Check if adult content blocking is enabled and this is a browser
            if (appSettings.isAdultContentBlockingEnabled() && packageName in BROWSER_PACKAGES) {
                val wasBlocked = checkAndBlockAdultUrl(rootNode, packageName)
                if (wasBlocked) {
                    Log.i(TAG, "Adult content blocked in $packageName")
                    return
                }
            }
            
            // --- Standard Content Detection with ContentDetector ---
            if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED ||
                event.eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
                // Process the accessibility tree to detect distracting content
                // Note: Since we're simplifying, we'll just check if it's a monitored app
                if (appSettings.isAppMonitored(packageName)) {
                    Log.d(TAG, "Processing accessibility tree for $packageName")
                    processNode(rootNode, packageName)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error processing accessibility event: ${e.message}")
        } finally {
            // Recycle nodes if needed
            try {
                rootNode.recycle()
            } catch (e: Exception) {
                // Ignore recycling errors
            }
        }
    }
    
    // Expanded lists for better filtering (still not exhaustive)
    private val ADULT_KEYWORDS = setOf(
        "porn", "sex", "xxx", "adult", "hentai", "erotic", "nude", "explicit",
        "lust", "sensual", "aphrodisiac", "bdsm", "fetish"
        // Add more general keywords cautiously
    )
    private val ADULT_DOMAINS = setOf(
        "xnxx.com", "xvideos.com", "pornhub.com", "hamster.com", // Common examples
        "youporn.com", "redtube.com", "tube8.com", "spankbang.com",
        "chaturbate.com", "livejasmin.com"
        // Add specific domains (lowercase) - avoid overly broad entries
    )

    // --- Updated Function Implementation: Check and Block Adult URL ---
    /**
     * Checks and blocks adult content in supported web browsers.
     * Uses back navigation (GLOBAL_ACTION_BACK) for consistency with social media blocking.
     * This provides a non-disruptive user experience compared to launching a full-screen activity.
     * 
     * @param rootNode The accessibility node representing the current screen
     * @param packageName The package name of the browser (e.g., "com.android.chrome")
     * @return True if adult content was detected and blocked, false otherwise
     */
    private fun checkAndBlockAdultUrl(rootNode: AccessibilityNodeInfo, packageName: String): Boolean {
        var urlText: String? = null
        var urlHost: String? = null
        try {
            // --- Find URL Bar based on Browser Package ---
            when (packageName) {
                "com.android.chrome" -> {
                    val urlBarNodes = rootNode.findAccessibilityNodeInfosByViewId("com.android.chrome:id/url_bar")
                    if (urlBarNodes != null && urlBarNodes.isNotEmpty()) {
                        urlText = urlBarNodes[0]?.text?.toString()?.lowercase()
                        Log.d(TAG, "Chrome URL detected: $urlText")
                        // Try to parse the URL to get the host
                        try {
                            if (!urlText.isNullOrBlank()) {
                                val url = URL(urlText) // Requires http:// or https:// prefix
                                urlHost = url.host?.lowercase()
                                Log.d(TAG, "URL Host: $urlHost")
                            }
                        } catch (e: MalformedURLException) {
                            // Might happen if text is not a full URL (e.g., during typing)
                            Log.w(TAG, "Could not parse URL text: $urlText - ${e.message}")
                            // We can still check the raw text against keywords
                        } catch (e: Exception) {
                            Log.e(TAG, "Error parsing URL: $urlText - ${e.message}")
                        }
                    }
                }
                // TODO: Add cases for other browsers
                else -> {
                    Log.w(TAG, "Adult content check not implemented for browser: $packageName")
                }
            }

            // --- Check URL Text and Host against Lists ---
            if (urlText != null) {
                // Check keywords in the full URL text
                for (keyword in ADULT_KEYWORDS) {
                    if (urlText.contains(keyword)) {
                        Log.i(TAG, "Adult keyword '$keyword' found in URL text: $urlText")
                        
                        // Use new blocking action system
                        executeBlockingAction(packageName, "adult_content")
                        
                        // Log the blocked event
                        logBlockedEvent(packageName, "adult_content")
                        return true // Blocked
                    }
                }
                // Check specific domains in the URL host (if host was parsed)
                if (urlHost != null) {
                    for (domain in ADULT_DOMAINS) {
                        if (urlHost.contains(domain)) { // contains checks subdomains too (e.g., m.xnxx.com)
                            Log.i(TAG, "Adult domain '$domain' found in URL host: $urlHost")
                            
                            // Use new blocking action system
                            executeBlockingAction(packageName, "adult_content")
                            
                            // Log the blocked event
                            logBlockedEvent(packageName, "adult_content")
                            return true // Blocked
                        }
                    }
                }
            }

        } catch (e: Exception) {
            Log.e(TAG, "Error during adult URL check for $packageName: ${e.message}")
        } finally {
            // Recycling logic might be needed here if issues arise
        }

        return false // Not blocked
    }
    
    /**
     * Detects if the current Instagram screen is showing Reels content
     */
    private fun isInstagramReelsView(rootNode: AccessibilityNodeInfo): Boolean {
        try {
            // Method 1: Look for specific content descriptions or text
            // Instagram often uses content descriptions for accessibility
            val allNodes = ArrayList<AccessibilityNodeInfo>()
            findAllNodes(rootNode, allNodes)
            
            for (node in allNodes) {
                val contentDesc = node.contentDescription?.toString()?.lowercase() ?: ""
                val text = node.text?.toString()?.lowercase() ?: ""
                
                // Check for specific content descriptions or text that indicate Reels
                if (contentDesc.contains("reel") || text.contains("reel") ||
                    contentDesc.contains("clip") || text.contains("clip")) {
                    Log.d(TAG, "Found Instagram Reels content: $contentDesc or $text")
                    return true
                }
            }
            
            // Method 2: Look for specific view IDs
            for (idPattern in INSTAGRAM_REELS_IDENTIFIERS) {
                val nodes = rootNode.findAccessibilityNodeInfosByViewId(idPattern)
                if (nodes != null && nodes.isNotEmpty()) {
                    Log.d(TAG, "Found Instagram Reels view by ID: $idPattern")
                    return true
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error checking Instagram for Reels: ${e.message}")
        }
        
        return false
    }
    
    /**
     * Detects if the current Snapchat screen is showing Stories or Spotlight content
     */
    private fun isSnapchatStoriesView(rootNode: AccessibilityNodeInfo): Boolean {
        try {
            // Method 1: Look for specific content descriptions or text
            val allNodes = ArrayList<AccessibilityNodeInfo>()
            findAllNodes(rootNode, allNodes)
            
            for (node in allNodes) {
                val contentDesc = node.contentDescription?.toString()?.lowercase() ?: ""
                val text = node.text?.toString()?.lowercase() ?: ""
                
                // Check for specific content descriptions or text that indicate Stories/Spotlight
                if (contentDesc.contains("story") || text.contains("story") ||
                    contentDesc.contains("stories") || text.contains("stories") ||
                    contentDesc.contains("spotlight") || text.contains("spotlight")) {
                    Log.d(TAG, "Found Snapchat Stories/Spotlight content: $contentDesc or $text")
                    return true
                }
            }
            
            // Method 2: Look for specific view IDs
            for (idPattern in SNAPCHAT_STORIES_IDENTIFIERS) {
                val nodes = rootNode.findAccessibilityNodeInfosByViewId(idPattern)
                if (nodes != null && nodes.isNotEmpty()) {
                    Log.d(TAG, "Found Snapchat Stories/Spotlight view by ID: $idPattern")
                    return true
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error checking Snapchat for Stories/Spotlight: ${e.message}")
        }
        
        return false
    }
    
    /**
     * Helper method to find all nodes in the accessibility tree
     */
    private fun findAllNodes(node: AccessibilityNodeInfo?, resultList: ArrayList<AccessibilityNodeInfo>) {
        if (node == null) return
        
        resultList.add(node)
        for (i in 0 until node.childCount) {
            val child = node.getChild(i)
            if (child != null) {
                findAllNodes(child, resultList)
            }
        }
    }
    
    /**
     * Shows the social app block page with appropriate messaging.
     * This is used specifically for Focus Mode when entire apps are being blocked,
     * which is different from content-specific blocking (like adult content or reels)
     * that uses back navigation for a less disruptive experience.
     */
    private fun showSocialAppBlockPage(packageName: String, isFocusMode: Boolean) {
        val intent = Intent(this, BlockedPageActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        
        // Pass information about what's being blocked to customize the message
        intent.putExtra("package_name", packageName)
        intent.putExtra("is_focus_mode", isFocusMode)
        
        when (packageName) {
            INSTAGRAM_PACKAGE -> {
                intent.putExtra("app_name", "Instagram")
                if (!isFocusMode) {
                    intent.putExtra("feature_name", "Reels")
                }
            }
            SNAPCHAT_PACKAGE -> {
                intent.putExtra("app_name", "Snapchat")
                if (!isFocusMode) {
                    intent.putExtra("feature_name", "Stories/Spotlight")
                }
            }
        }
        
        startActivity(intent)
        
        // Note: Removed GLOBAL_ACTION_HOME to prevent closing the entire app
        // The blocking page will handle navigation when user chooses an option
    }

    private fun processNode(rootNode: AccessibilityNodeInfo, packageName: String) {
        try {
            // --- Always block Reels/Shorts --- regardless of focus mode status
            var isReelOrShort = false
            when (packageName) {
                AppSettings.PACKAGE_INSTAGRAM -> {
                    if (isInstagramReels(rootNode)) {
                        Log.d(TAG, "Instagram Reels detected. Blocking.")
                        blockContentWithAction(packageName, AppSettings.CONTENT_TYPE_REELS)
                        isReelOrShort = true // Mark as handled
                    }
                }
                AppSettings.PACKAGE_YOUTUBE -> {
                    if (isYouTubeShorts(rootNode)) {
                        Log.d(TAG, "YouTube Shorts detected. Blocking.")
                        blockContentWithAction(packageName, AppSettings.CONTENT_TYPE_SHORTS)
                        isReelOrShort = true // Mark as handled
                    }
                }
            }

            // If we already handled a reel/short, no need for general detection
            if (isReelOrShort) {
                return
            }

            // --- General Focus Mode Blocking (Content Detection) --- 
            // Only proceed with general content detection if focus mode is ON.
            // App-level blocking is handled earlier in onAccessibilityEvent.
            if (!appSettings.isFocusModeEnabled()) {
                Log.v(TAG, "Skipping general content detection: FocusModeEnabled=false for $packageName")
                return // Don't do general content detection if focus mode is off
            }
            
            Log.d(TAG, "Proceeding with general content detection for $packageName (Focus Mode Active)")
            // Use the general content detector for other apps/content (only if focus mode is on)
            val result = contentDetector.detectDistractingContent(rootNode, packageName)
            
            if (result.detected) {
                // Handle distracting content
                Log.d(TAG, "General distracting content detected: ${result.contentType}")
                blockContentWithAction(packageName, result.contentType)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error processing node: ${e.message}")
        } finally {
            // Optional: Consider if explicit recycling is needed, but often handled automatically.
            // rootNode?.recycle()
        }
    }
    
    // Enhanced detection for Instagram reels
    private fun isInstagramReels(node: AccessibilityNodeInfo): Boolean {
        // More comprehensive checks for Instagram reels
        return (findNodesByText(node, "reels", true).isNotEmpty() ||
               findNodesByText(node, "reel", true).isNotEmpty() ||
               findNodesByText(node, "liked by", true).isNotEmpty() && findNodesByText(node, "others", true).isNotEmpty() ||
               findNodesByText(node, "watching now", true).isNotEmpty() ||
               findNodesByText(node, "Reels and short videos", false).isNotEmpty() ||
               // For You / Following tabs often indicate reels
               (findNodesByText(node, "For You", false).isNotEmpty() && findNodesByText(node, "Following", false).isNotEmpty()) ||
               // URL indicators in content descriptions
               node.findAccessibilityNodeInfosByText("instagram.com/reels").isNotEmpty() ||
               node.findAccessibilityNodeInfosByText("instagram.com/p/").isNotEmpty() && isVerticalScrollingContent(node) ||
               // UI components specific to reels
               node.findAccessibilityNodeInfosByViewId("com.instagram.android:id/reel_viewer_container").isNotEmpty() ||
               node.findAccessibilityNodeInfosByViewId("com.instagram.android:id/clips_viewer").isNotEmpty() ||
               node.findAccessibilityNodeInfosByViewId("com.instagram.android:id/reels_tray").isNotEmpty() ||
               node.findAccessibilityNodeInfosByViewId("com.instagram.android:id/clips_tab").isNotEmpty())
    }
    
    // Enhanced detection for YouTube shorts
    private fun isYouTubeShorts(node: AccessibilityNodeInfo?): Boolean {
        if (node == null) return false
        
        try {
            // More comprehensive checks for YouTube shorts
            return (findNodesByText(node, "shorts", true).isNotEmpty() ||
                   findNodesByText(node, "Short", false).isNotEmpty() ||
                   // Like and subscribe indicators often found in shorts
                   (findNodesByText(node, "Like", false).isNotEmpty() && 
                    findNodesByText(node, "Comments", false).isNotEmpty() && 
                    isVerticalScrollingContent(node)) ||
                   // URL indicators in content descriptions
                   try { node.findAccessibilityNodeInfosByText("/shorts/").isNotEmpty() } catch (e: Exception) { false } ||
                   // UI components specific to shorts
                   try { node.findAccessibilityNodeInfosByViewId("com.google.android.youtube:id/shorts_container").isNotEmpty() } catch (e: Exception) { false } ||
                   try { node.findAccessibilityNodeInfosByViewId("com.google.android.youtube:id/shorts_player").isNotEmpty() } catch (e: Exception) { false } ||
                   try { node.findAccessibilityNodeInfosByViewId("com.google.android.youtube:id/shorts_feed").isNotEmpty() } catch (e: Exception) { false } ||
                   (try { node.findAccessibilityNodeInfosByViewId("com.google.android.youtube:id/player_control_button").isNotEmpty() } catch (e: Exception) { false } && 
                    isVerticalScrollingContent(node)))
        } catch (e: Exception) {
            Log.e(TAG, "Error detecting YouTube shorts: ${e.message}")
            return false
        }
    }
    
    // Helper function to detect vertical scrolling content (common in reels/shorts)
    private fun isVerticalScrollingContent(node: AccessibilityNodeInfo?): Boolean {
        if (node == null) return false
        
        try {
            val scrollable = node.isScrollable
            val bounds = Rect()
            node.getBoundsInScreen(bounds)
            
            // Verify we have valid dimensions to avoid division by zero
            if (bounds.width() <= 0) return false
            
            val heightToWidthRatio = bounds.height().toDouble() / bounds.width().toDouble()
            // It's vertical scrolling content if it's explicitly scrollable or has vertical layout (higher than it is wide)
            return scrollable || heightToWidthRatio > 1.2 // Arbitrary threshold for "tallness"
        } catch (e: Exception) {
            Log.e(TAG, "Error checking vertical scrolling content: ${e.message}")
            return false
        }
    }
    
    // Helper function to find nodes by text
    private fun findNodesByText(node: AccessibilityNodeInfo?, text: String, partial: Boolean): MutableList<AccessibilityNodeInfo> {
        val nodes = mutableListOf<AccessibilityNodeInfo>()
        if (node == null) return nodes
        
        try {
            val list = node.findAccessibilityNodeInfosByText(text) ?: return nodes
            for (i in 0 until list.size) {
                val nodeInfo = list.get(i) ?: continue
                val nodeText = nodeInfo.text?.toString()
                if (nodeText != null && (partial || nodeText.equals(text, ignoreCase = true))) {
                    nodes.add(nodeInfo)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error finding nodes by text: ${e.message}")
        }
        return nodes
    }
    
    // Helper function to find nodes by view ID
    private fun findNodesByViewId(node: AccessibilityNodeInfo?, viewId: String): MutableList<AccessibilityNodeInfo> {
        val nodes = mutableListOf<AccessibilityNodeInfo>()
        if (node == null) return nodes
        
        try {
            // The find method can return null if the node is no longer in the tree
            val foundNodes = node.findAccessibilityNodeInfosByViewId(viewId) ?: return nodes
            nodes.addAll(foundNodes)
        } catch (e: Exception) {
            Log.e(TAG, "Error finding nodes by view ID: ${e.message}")
        }
        return nodes
    }

    /**
     * Redirect to safe app section (for normal mode - tries to keep user in app but away from distracting content)
     */
    private fun redirectToSafeAppSection(rootNode: AccessibilityNodeInfo, packageName: String, contentType: String): Boolean {
        try {
            Log.d(TAG, "Attempting to redirect to safe section in $packageName for $contentType")
            
            when (packageName) {
                INSTAGRAM_PACKAGE -> {
                    return redirectInstagram(rootNode, contentType)
                }
                SNAPCHAT_PACKAGE -> {
                    return redirectSnapchat(rootNode, contentType)
                }
                YOUTUBE_PACKAGE -> {
                    return redirectYouTube(rootNode, contentType)
                }
                else -> {
                    Log.w(TAG, "No safe section redirect available for $packageName")
                    // Fall back to blocking action
                    executeBlockingAction(packageName, contentType)
                    return true
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error redirecting to safe section: ${e.message}")
            return false
        }
    }
    
    /**
     * Block content with user-selected blocking action (for focus mode or when redirection fails)
     */
    private fun blockContentWithAction(packageName: String, contentType: String): Boolean {
        try {
            Log.d(TAG, "Blocking content with user action for $packageName ($contentType)")
            executeBlockingAction(packageName, contentType)
            logBlockedEvent(packageName, contentType)
            return true
        } catch (e: Exception) {
            Log.e(TAG, "Error blocking content with action: ${e.message}")
            return false
        }
    }

    /**
     * Execute blocking action based on user settings
     */
    private fun executeBlockingAction(packageName: String, contentType: String) {
        try {
            val blockingAction = appSettings.getBlockingAction()
            Log.d(TAG, "Executing blocking action: $blockingAction for $packageName ($contentType)")
            
            if (::blockingActionHandler.isInitialized) {
                blockingActionHandler.executeBlockingAction(packageName, blockingAction)
            } else {
                Log.w(TAG, "BlockingActionHandler not initialized, falling back to default action")
                performGlobalAction(GLOBAL_ACTION_BACK)
            }
            
            // Show appropriate user feedback
            val actionMessage = when (blockingAction) {
                AppSettings.BLOCKING_ACTION_CLOSE_PLAYER -> "returned to previous screen"
                AppSettings.BLOCKING_ACTION_CLOSE_APP -> "closed blocked app"
                AppSettings.BLOCKING_ACTION_LOCK_SCREEN -> "locked screen"
                else -> "blocked content"
            }
            
            try {
                Toast.makeText(this, "Focus: $actionMessage", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.e(TAG, "Error showing toast: ${e.message}")
            }
            
            // Show notification if enabled
            if (::notificationHelper.isInitialized && appSettings.showBlockNotifications()) {
                try {
                    notificationHelper.showTemporaryNotification(
                        "Focus: Content Blocked", 
                        "Blocked $contentType - $actionMessage", 
                        false
                    )
                } catch (e: Exception) {
                    Log.e(TAG, "Error showing notification: ${e.message}")
                }
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error executing blocking action: ${e.message}")
            // Fallback to default action
            performGlobalAction(GLOBAL_ACTION_BACK)
        }
    }

    private fun logBlockedEvent(packageName: String, contentType: String) {
        // Skip logging if database initialization failed
        if (!::blockedContentEventDao.isInitialized) {
            Log.w(TAG, "Cannot log blocked event: Database not initialized")
            return
        }
        
        serviceScope.launch {
            try {
                val event = BlockedContentEvent(
                    appPackage = packageName,
                    contentType = contentType,
                    timestamp = Date()
                )
                blockedContentEventDao.insert(event)
                Log.d(TAG, "Logged blocked event: $contentType in $packageName")
            } catch (e: Exception) {
                Log.e(TAG, "Error logging blocked event: ${e.message}")
            }
        }
    }

    override fun onInterrupt() {
        Log.w(TAG, "FocusAccessibilityService: onInterrupt() - Service was interrupted")
        // This is called when the accessibility service is forcibly interrupted
        // We can try to recover or log the issue for debugging
    }

    override fun onDestroy() {
        Log.i(TAG, "FocusAccessibilityService: onDestroy() - Service shutting down")
        super.onDestroy()
        serviceScope.cancel()
        job.cancel()
    }
    
    private fun blockYouTubeShorts(rootNode: AccessibilityNodeInfo): Boolean {
        try {
            Log.d(TAG, "Attempting to block YouTube Shorts")
            
            // Strategy 1: Try intelligent redirection first (like Instagram)
            val redirected = redirectYouTube(rootNode, AppSettings.CONTENT_TYPE_SHORTS)
            
            if (redirected) {
                Log.i(TAG, "Successfully blocked YouTube Shorts using intelligent redirection")
                
                // Show user feedback
                try {
                    Toast.makeText(this, "Focus: Redirected from YouTube Shorts", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Log.e(TAG, "Error showing toast: ${e.message}")
                }
                
                // Show notification if enabled
                if (::notificationHelper.isInitialized && appSettings.showBlockNotifications()) {
                    try {
                        notificationHelper.showTemporaryNotification(
                            "Focus: Content Blocked", 
                            "Redirected from Shorts in YouTube", 
                            false
                        )
                    } catch (e: Exception) {
                        Log.e(TAG, "Error showing notification: ${e.message}")
                    }
                }
                
                // Log the blocked event
                logBlockedEvent("com.google.android.youtube", AppSettings.CONTENT_TYPE_SHORTS)
                
                return true
            }
            
            // Strategy 2: Fallback to simple back action
            Log.d(TAG, "Intelligent redirection failed, falling back to BACK action")
            val success = performGlobalAction(GLOBAL_ACTION_BACK)
            
            if (success) {
                Log.i(TAG, "Successfully blocked YouTube Shorts using BACK action")
                
                // Show user feedback
                try {
                    Toast.makeText(this, "Focus: Blocked YouTube Shorts", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Log.e(TAG, "Error showing toast: ${e.message}")
                }
                
                // Show notification if enabled
                if (::notificationHelper.isInitialized && appSettings.showBlockNotifications()) {
                    try {
                        notificationHelper.showTemporaryNotification(
                            "Focus: Content Blocked", 
                            "Blocked Shorts in YouTube", 
                            false
                        )
                    } catch (e: Exception) {
                        Log.e(TAG, "Error showing notification: ${e.message}")
                    }
                }
                
                // Log the blocked event
                logBlockedEvent("com.google.android.youtube", AppSettings.CONTENT_TYPE_SHORTS)
                
                return true
            } else {
                Log.w(TAG, "BACK action failed for YouTube Shorts")
                return false
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error blocking YouTube Shorts: ${e.message}")
            return false
        }
    }
    
    /**
     * Redirect Instagram from Reels to safe section (Home/Feed)
     */
    private fun redirectInstagram(rootNode: AccessibilityNodeInfo, contentType: String): Boolean {
        try {
            Log.d(TAG, "Attempting to redirect from Instagram $contentType")
            
            // Strategy 1: Try to click on Home tab
            val homeBtns = findNodesByViewId(rootNode, "com.instagram.android:id/tab_home")
            homeBtns.addAll(findNodesByViewId(rootNode, "com.instagram.android:id/navigation_home"))
            homeBtns.addAll(findNodesByText(rootNode, "Home", false))
            for (btn in homeBtns) {
                if (btn.isClickable) {
                    val success = btn.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                    if (success) {
                        Log.d(TAG, "Successfully redirected to Instagram Home")
                        return true
                    }
                }
            }
            
            // Strategy 2: Try to click on Search tab
            val searchBtns = findNodesByViewId(rootNode, "com.instagram.android:id/tab_search")
            searchBtns.addAll(findNodesByViewId(rootNode, "com.instagram.android:id/navigation_search"))
            searchBtns.addAll(findNodesByText(rootNode, "Search", false))
            for (btn in searchBtns) {
                if (btn.isClickable) {
                    val success = btn.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                    if (success) {
                        Log.d(TAG, "Successfully redirected to Instagram Search")
                        return true
                    }
                }
            }
            
            // Strategy 3: Try any safe navigation targets
            for (targetId in INSTAGRAM_SAFE_TARGETS) {
                val targetButtons = findNodesByViewId(rootNode, targetId)
                for (btn in targetButtons) {
                    if (btn.isClickable) {
                        val success = btn.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                        if (success) {
                            Log.d(TAG, "Successfully redirected to Instagram safe section via $targetId")
                            return true
                        }
                    }
                }
            }
            
            Log.d(TAG, "All Instagram redirection strategies failed")
            return false
            
        } catch (e: Exception) {
            Log.e(TAG, "Error redirecting Instagram: ${e.message}")
            return false
        }
    }
    
    /**
     * Redirect Snapchat from Stories/Spotlight to safe section (Chat/Camera)
     */
    private fun redirectSnapchat(rootNode: AccessibilityNodeInfo, contentType: String): Boolean {
        try {
            Log.d(TAG, "Attempting to redirect from Snapchat $contentType")
            
            // Strategy 1: Try to click on Chat tab
            val chatBtns = findNodesByViewId(rootNode, "com.snapchat.android:id/chat_tab")
            chatBtns.addAll(findNodesByText(rootNode, "Chat", false))
            for (btn in chatBtns) {
                if (btn.isClickable) {
                    val success = btn.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                    if (success) {
                        Log.d(TAG, "Successfully redirected to Snapchat Chat")
                        return true
                    }
                }
            }
            
            // Strategy 2: Try to click on Camera tab
            val cameraBtns = findNodesByViewId(rootNode, "com.snapchat.android:id/camera_tab")
            cameraBtns.addAll(findNodesByViewId(rootNode, "com.snapchat.android:id/tab_bar_camera_option"))
            cameraBtns.addAll(findNodesByText(rootNode, "Camera", false))
            for (btn in cameraBtns) {
                if (btn.isClickable) {
                    val success = btn.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                    if (success) {
                        Log.d(TAG, "Successfully redirected to Snapchat Camera")
                        return true
                    }
                }
            }
            
            // Strategy 3: Try any safe navigation targets
            for (targetId in SNAPCHAT_SAFE_TARGETS) {
                val targetButtons = findNodesByViewId(rootNode, targetId)
                for (btn in targetButtons) {
                    if (btn.isClickable) {
                        val success = btn.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                        if (success) {
                            Log.d(TAG, "Successfully redirected to Snapchat safe section via $targetId")
                            return true
                        }
                    }
                }
            }
            
            Log.d(TAG, "All Snapchat redirection strategies failed")
            return false
            
        } catch (e: Exception) {
            Log.e(TAG, "Error redirecting Snapchat: ${e.message}")
            return false
        }
    }

    private fun redirectYouTube(rootNode: AccessibilityNodeInfo, contentType: String): Boolean {
        try {
            Log.d(TAG, "Attempting to redirect from YouTube $contentType")
            
            // YouTube UI identifiers (these may change with app updates)
            val YOUTUBE_SAFE_TARGETS = setOf(
                "com.google.android.youtube:id/tab_home",
                "com.google.android.youtube:id/bottom_tab_home",
                "com.google.android.youtube:id/tab_subscriptions", 
                "com.google.android.youtube:id/bottom_tab_subscriptions",
                "com.google.android.youtube:id/tab_explore",
                "com.google.android.youtube:id/bottom_tab_explore",
                "com.google.android.youtube:id/tab_library",
                "com.google.android.youtube:id/bottom_tab_library"
            )
            
            // Strategy 1: Primary navigation - try to click on the Home tab first
            val homeBtns = findNodesByViewId(rootNode, "com.google.android.youtube:id/tab_home")
            homeBtns.addAll(findNodesByViewId(rootNode, "com.google.android.youtube:id/bottom_tab_home"))
            homeBtns.addAll(findNodesByText(rootNode, "Home", false))
            for (btn in homeBtns) {
                if (btn.isClickable) {
                    val success = btn.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                    if (success) {
                        Log.d(TAG, "Successfully redirected to YouTube Home tab")
                        return true
                    }
                }
            }
            
            // Strategy 2: Try Subscriptions tab
            val subsBtns = findNodesByViewId(rootNode, "com.google.android.youtube:id/tab_subscriptions")
            subsBtns.addAll(findNodesByViewId(rootNode, "com.google.android.youtube:id/bottom_tab_subscriptions"))
            subsBtns.addAll(findNodesByText(rootNode, "Subscriptions", false))
            for (btn in subsBtns) {
                if (btn.isClickable) {
                    val success = btn.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                    if (success) {
                        Log.d(TAG, "Successfully redirected to YouTube Subscriptions tab")
                        return true
                    }
                }
            }
            
            // Strategy 3: Try to find and click on any other safe navigation tab
            for (targetId in YOUTUBE_SAFE_TARGETS) {
                val targetButtons = findNodesByViewId(rootNode, targetId)
                for (btn in targetButtons) {
                    if (btn.isClickable) {
                        val success = btn.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                        if (success) {
                            Log.d(TAG, "Successfully redirected to YouTube safe section via $targetId")
                            return true
                        }
                    }
                }
            }
            
            // Strategy 4: Look for any navigation buttons by analyzing all clickable nodes
            val allNodes = ArrayList<AccessibilityNodeInfo>()
            findAllNodes(rootNode, allNodes)
            
            for (node in allNodes) {
                if (node.isClickable) {
                    val contentDesc = node.contentDescription?.toString()?.lowercase() ?: ""
                    val text = node.text?.toString()?.lowercase() ?: ""
                    
                    // Skip if it's related to shorts or vertical video content
                    if (contentDesc.contains("short") || text.contains("short") ||
                        contentDesc.contains("vertical") || text.contains("vertical")) {
                        continue
                    }
                    
                    // Check if it might be a safe navigation button
                    if (contentDesc.contains("home") || text.contains("home") ||
                        contentDesc.contains("subscription") || text.contains("subscription") ||
                        contentDesc.contains("explore") || text.contains("explore") ||
                        contentDesc.contains("library") || text.contains("library") ||
                        contentDesc.contains("trending") || text.contains("trending")) {
                        
                        val success = node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                        if (success) {
                            Log.d(TAG, "Redirected to YouTube via navigation element: $contentDesc or $text")
                            return true
                        }
                    }
                }
            }
            
            // Strategy 5: Try to close the current view (look for close buttons)
            val closeBtns = findNodesByViewId(rootNode, "com.google.android.youtube:id/close_button")
            closeBtns.addAll(findNodesByViewId(rootNode, "com.google.android.youtube:id/exit_button"))
            closeBtns.addAll(findNodesByText(rootNode, "Close", false))
            closeBtns.addAll(findNodesByText(rootNode, "Exit", false))
            for (btn in closeBtns) {
                if (btn.isClickable) {
                    val success = btn.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                    if (success) {
                        Log.d(TAG, "Successfully closed YouTube Shorts via close button")
                        return true
                    }
                }
            }
            
            // Strategy 6: Failed - return false to trigger fallback
            Log.d(TAG, "All YouTube redirection strategies failed")
            return false
            
        } catch (e: Exception) {
            Log.e(TAG, "Error redirecting YouTube: ${e.message}")
            return false
        }
    }

    private fun findNodesByClassName(node: AccessibilityNodeInfo, className: String, result: MutableList<AccessibilityNodeInfo>) {
        if (node == null) return
        
        if (node.className?.toString() == className) {
            result.add(node)
        }
        
        for (i in 0 until node.childCount) {
            val child = node.getChild(i)
            if (child != null) {
                findNodesByClassName(child, className, result)
            }
        }
    }
}
