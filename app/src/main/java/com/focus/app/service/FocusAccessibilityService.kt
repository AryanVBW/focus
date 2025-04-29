package com.focus.app.service

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
import com.focus.app.data.AppDatabase
import com.focus.app.data.BlockedContentEvent
import com.focus.app.data.BlockedContentEventDao
import com.focus.app.util.AppSettings
import com.focus.app.util.ContentDetector
import com.focus.app.util.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
    private val serviceScope = CoroutineScope(Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        try {
            Log.d(TAG, "Service created")
            initializeComponents()
        } catch (e: Exception) {
            Log.e(TAG, "Error during service creation: ${e.message}")
            // Schedule a retry after a short delay to allow system to stabilize
            Handler(Looper.getMainLooper()).postDelayed({
                try {
                    Log.d(TAG, "Retrying service initialization")
                    initializeComponents()
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to initialize service on retry: ${e.message}")
                    // At this point we can't do much more than notify the user
                    Toast.makeText(this, "Focus service couldn't start properly. Please try disabling and re-enabling the service.", Toast.LENGTH_LONG).show()
                }
            }, 1000) // Wait 1 second before retry
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
        Log.d(TAG, "Service connected")
        // Add log to check notificationHelper state
        if (::notificationHelper.isInitialized) {
            Log.d(TAG, "NotificationHelper is initialized. Showing notification.")
            notificationHelper.showServiceRunningNotification()
        } else {
            Log.w(TAG, "NotificationHelper NOT initialized when trying to show notification.")
            // Consider initializing it here as a fallback, though it should be done in onCreate
            // initializeComponents() // Or specifically initialize notificationHelper if possible
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
        // Add log at the beginning of the event handler
        Log.d(TAG, "onAccessibilityEvent received: type=${AccessibilityEvent.eventTypeToString(event.eventType)}, pkg=${event.packageName}, class=${event.className}")
        try {
            // Check if all required components are initialized
            if (!::appSettings.isInitialized || !::contentDetector.isInitialized) {
                Log.w(TAG, "Skipping accessibility event - service not fully initialized (appSettings: ${::appSettings.isInitialized}, contentDetector: ${::contentDetector.isInitialized})")
                return
            }
            
            // Only process if service is enabled
            if (!appSettings.isFocusModeEnabled()) {
                return
            }

            // Get package name of the app that generated the event
            val packageName = event.packageName?.toString() ?: return
            
            // Check if this app is configured to be monitored
            if (!appSettings.isAppMonitored(packageName)) {
                return
            }

            // Only process relevant event types to reduce overhead and potential crashes
            when (event.eventType) {
                AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED,
                AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED -> {
                    // These events are most relevant for content detection
                    val rootNode = event.source ?: rootInActiveWindow
                    if (rootNode != null) {
                        try {
                            processNode(rootNode, packageName)
                        } catch (e: Exception) {
                            Log.e(TAG, "Error processing node: ${e.message}")
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error processing accessibility event: ${e.message}")
        }
    }

    private fun processNode(rootNode: AccessibilityNodeInfo, packageName: String) {
        try {
            // Special handling for Instagram reels and YouTube shorts which are difficult to detect
            when (packageName) {
                AppSettings.PACKAGE_INSTAGRAM -> {
                    // Check for reels/short-form content indicators
                    if (isInstagramReels(rootNode)) {
                        handleDistractingContent(rootNode, packageName, AppSettings.CONTENT_TYPE_REELS)
                        return
                    }
                }
                AppSettings.PACKAGE_YOUTUBE -> {
                    // Check for shorts
                    if (isYouTubeShorts(rootNode)) {
                        handleDistractingContent(rootNode, packageName, AppSettings.CONTENT_TYPE_SHORTS)
                        return
                    }
                }
            }
            
            // Use the general content detector for other apps/content
            val result = contentDetector.detectDistractingContent(rootNode, packageName)
            
            if (result.detected) {
                // Handle distracting content
                handleDistractingContent(rootNode, packageName, result.contentType)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error processing node: ${e.message}")
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

    private fun handleDistractingContent(rootNode: AccessibilityNodeInfo?, packageName: String, contentType: String) {
        if (rootNode == null) {
            Log.e(TAG, "Cannot handle distracting content: null root node")
            return
        }
        
        // Ensure app settings are initialized
        if (!::appSettings.isInitialized) {
            Log.e(TAG, "Cannot handle distracting content: app settings not initialized")
            return
        }
        
        try {
            // Block the content based on settings
            if (appSettings.shouldBlockContentType(contentType)) {
                // More aggressive blocking strategy
                val blocked = when (packageName) {
                    AppSettings.PACKAGE_INSTAGRAM -> {
                        when (contentType) {
                            AppSettings.CONTENT_TYPE_REELS -> blockInstagramReels(rootNode)
                            AppSettings.CONTENT_TYPE_STORIES -> true.also { 
                                try { performGlobalAction(GLOBAL_ACTION_BACK) } catch (e: Exception) { 
                                    Log.e(TAG, "Error performing back action: ${e.message}") 
                                }
                            }
                            else -> true.also { 
                                try { performGlobalAction(GLOBAL_ACTION_BACK) } catch (e: Exception) { 
                                    Log.e(TAG, "Error performing back action: ${e.message}") 
                                }
                            }
                        }
                    }
                    AppSettings.PACKAGE_YOUTUBE -> {
                        when (contentType) {
                            AppSettings.CONTENT_TYPE_SHORTS -> blockYouTubeShorts(rootNode)
                            else -> true.also { 
                                try { performGlobalAction(GLOBAL_ACTION_BACK) } catch (e: Exception) { 
                                    Log.e(TAG, "Error performing back action: ${e.message}") 
                                }
                            }
                        }
                    }
                    else -> {
                        // Default blocking action for other apps
                        try { performGlobalAction(GLOBAL_ACTION_BACK) } catch (e: Exception) { 
                            Log.e(TAG, "Error performing back action: ${e.message}") 
                        }
                        true
                    }
                }
            
                if (blocked) {
                    try {
                        // Show a toast message immediately for better feedback
                        Toast.makeText(this, "Blocked distracting $contentType content", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        Log.e(TAG, "Error showing toast: ${e.message}")
                        // Continue execution even if toast fails
                    }
                    
                    // Show notification if helper is initialized
                    if (::notificationHelper.isInitialized) {
                        try {
                            notificationHelper.showContentBlockedNotification(packageName, contentType)
                        } catch (e: Exception) {
                            Log.e(TAG, "Error showing notification: ${e.message}")
                            // Continue execution even if notification fails
                        }
                    }
                    
                    // Log the event, only if we've successfully initialized the database
                    try {
                        logBlockedEvent(packageName, contentType)
                    } catch (e: Exception) {
                        Log.e(TAG, "Error logging blocked event: ${e.message}")
                        // Continue execution even if logging fails
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error handling distracting content: ${e.message}")
            // Try a fallback action if the handling failed
            try {
                performGlobalAction(GLOBAL_ACTION_BACK)
            } catch (e2: Exception) {
                Log.e(TAG, "Fallback action also failed: ${e2.message}")
            }
        }
    }
    
    // Specialized method to block Instagram reels with enhanced strategies
    private fun blockInstagramReels(rootNode: AccessibilityNodeInfo): Boolean {
        try {
            Log.d(TAG, "Attempting to block Instagram reels with enhanced strategies...")
            
            // Create a notification to inform the user (but only if setting enabled)
            if (appSettings.showBlockNotifications()) {
                notificationHelper.showTemporaryNotification(
                    "Focus Mode", 
                    "Distracting content (Instagram Reels) blocked", 
                    false
                )
            }

            // Strategy 1: Close button approach - look for close or exit buttons first
            val closeBtns = findNodesByViewId(rootNode, "com.instagram.android:id/close_button")
            closeBtns.addAll(findNodesByViewId(rootNode, "com.instagram.android:id/exit_button"))
            closeBtns.addAll(findNodesByText(rootNode, "Close", false))
            closeBtns.addAll(findNodesByText(rootNode, "Exit", false))
            if (closeBtns.isNotEmpty()) {
                for (btn in closeBtns) {
                    if (btn.isClickable) {
                        btn.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                        Log.d(TAG, "Clicked close/exit button on Instagram reels")
                        return true
                    }
                }
            }
            
            // Strategy 2: Try to navigate back using native back button
            val backBtns = findNodesByViewId(rootNode, "com.instagram.android:id/action_bar_button_back")
            backBtns.addAll(findNodesByText(rootNode, "Back", false))
            backBtns.addAll(findNodesByViewId(rootNode, "com.instagram.android:id/navigation_bar_item_icon_view"))
            if (backBtns.isNotEmpty()) {
                for (btn in backBtns) {
                    if (btn.isClickable) {
                        btn.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                        Log.d(TAG, "Clicked back button on Instagram reels")
                        return true
                    }
                }
            }
            
            // Strategy 3: Tab navigation approach - try to switch to other tabs
            // First priority: Home tab
            val homeBtns = findNodesByViewId(rootNode, "com.instagram.android:id/tab_home")
            homeBtns.addAll(findNodesByViewId(rootNode, "com.instagram.android:id/navigation_home"))
            homeBtns.addAll(findNodesByText(rootNode, "Home", false))
            if (homeBtns.isNotEmpty()) {
                for (btn in homeBtns) {
                    if (btn.isClickable) {
                        btn.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                        Log.d(TAG, "Clicked home button to exit Instagram reels")
                        return true
                    }
                }
            }
            
            // Search tab
            val searchBtns = findNodesByViewId(rootNode, "com.instagram.android:id/tab_search")
            searchBtns.addAll(findNodesByViewId(rootNode, "com.instagram.android:id/navigation_search"))
            searchBtns.addAll(findNodesByText(rootNode, "Search", false))
            if (searchBtns.isNotEmpty()) {
                for (btn in searchBtns) {
                    if (btn.isClickable) {
                        btn.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                        Log.d(TAG, "Clicked search button to exit Instagram reels")
                        return true
                    }
                }
            }
            
            // Profile tab
            val profileBtns = findNodesByViewId(rootNode, "com.instagram.android:id/tab_avatar")
            profileBtns.addAll(findNodesByViewId(rootNode, "com.instagram.android:id/navigation_profile"))
            profileBtns.addAll(findNodesByText(rootNode, "Profile", false))
            if (profileBtns.isNotEmpty()) {
                for (btn in profileBtns) {
                    if (btn.isClickable) {
                        btn.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                        Log.d(TAG, "Clicked profile button to exit Instagram reels")
                        return true
                    }
                }
            }
            
            // Strategy 4: If we're in a WebView with Reels, try to navigate to main Instagram URL
            // Find WebView with navigation options
            val webViews = mutableListOf<AccessibilityNodeInfo>()
            findNodesByClassName(rootNode, "android.webkit.WebView", webViews)
            if (webViews.isNotEmpty()) {
                // Try to find address bar to navigate away
                for (webView in webViews) {
                    val addressBars = findNodesByViewId(webView, "com.android.chrome:id/url_bar")
                    addressBars.addAll(findNodesByViewId(webView, "com.android.browser:id/url"))
                    
                    if (addressBars.isNotEmpty()) {
                        // Try to navigate to Instagram main page
                        for (addressBar in addressBars) {
                            if (addressBar.isEditable) {
                                // Clear current URL
                                addressBar.performAction(AccessibilityNodeInfo.ACTION_FOCUS)
                                addressBar.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT)
                                
                                // Set new URL
                                val arguments = Bundle()
                                arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, "https://www.instagram.com/")
                                addressBar.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)
                                
                                // Find go/enter button
                                val goBtns = findNodesByViewId(webView, "com.android.chrome:id/url_action_button")
                                for (goBtn in goBtns) {
                                    if (goBtn.isClickable) {
                                        goBtn.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                                        Log.d(TAG, "Navigated away from Instagram reels in WebView")
                                        return true
                                    }
                                }
                                
                                // Try keyboard enter as fallback
                                performGlobalAction(GLOBAL_ACTION_BACK) // Hide keyboard
                                return true
                            }
                        }
                    }
                }
            }
            
            // Strategy 5: Fallback to global back button
            performGlobalAction(GLOBAL_ACTION_BACK)
            Log.d(TAG, "Used global back action to exit Instagram reels")
            return true
        } catch (e: Exception) {
            Log.e(TAG, "Error blocking Instagram reels: ${e.message}")
            // Fallback to basic blocking
            performGlobalAction(GLOBAL_ACTION_BACK)
            return true
        }
    }
    
    /**
     * Find nodes by class name - helper method for navigation
     */
    private fun findNodesByClassName(node: AccessibilityNodeInfo?, className: String, results: MutableList<AccessibilityNodeInfo>) {
        if (node == null) return

        try {
            if (node.className?.toString() == className) {
                results.add(node)
            }

            // Safely get child count, can throw exceptions if node has been recycled
            val childCount = try { node.childCount } catch (e: Exception) { 0 }

            for (i in 0 until childCount) {
                try {
                    val child = node.getChild(i)
                    if (child != null) {
                        findNodesByClassName(child, className, results)
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error accessing child node: ${e.message}")
                    // Skip this child and continue with others
                    continue
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in findNodesByClassName: ${e.message}")
            // Method failed, but we don't want to crash
        }
    }
    
    // Specialized method to block YouTube shorts with enhanced strategies
    private fun blockYouTubeShorts(rootNode: AccessibilityNodeInfo?): Boolean {
        if (rootNode == null) {
            Log.e(TAG, "Cannot block YouTube shorts: null root node")
            return false
        }
        
        try {
            Log.d(TAG, "Attempting to block YouTube shorts with enhanced strategies...")
            
            // Create a notification to inform the user (but only if setting enabled)
            if (::appSettings.isInitialized && appSettings.showBlockNotifications() && 
                ::notificationHelper.isInitialized) {
                try {
                    notificationHelper.showTemporaryNotification(
                        "Focus Mode", 
                        "Distracting content (YouTube Shorts) blocked", 
                        false
                    )
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to show notification: ${e.message}")
                    // Continue execution, notification failure is not critical
                }
            }
            
            // Strategy 1: Close button approach - look for various close or exit buttons first
            val closeBtns = findNodesByViewId(rootNode, "com.google.android.youtube:id/close_button")
            closeBtns.addAll(findNodesByViewId(rootNode, "com.google.android.youtube:id/player_close_button"))
            closeBtns.addAll(findNodesByViewId(rootNode, "com.google.android.youtube:id/player_control_close_button"))
            closeBtns.addAll(findNodesByText(rootNode, "Close", false))
            closeBtns.addAll(findNodesByText(rootNode, "Exit", false))
            closeBtns.addAll(findNodesByViewId(rootNode, "dismiss_button"))
            if (closeBtns.isNotEmpty()) {
                for (btn in closeBtns) {
                    if (btn.isClickable) {
                        btn.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                        Log.d(TAG, "Clicked close/exit button on YouTube shorts")
                        return true
                    }
                }
            }
            
            // Strategy 2: Tab navigation approach - try to switch to other tabs
            // First priority: Home tab
            val homeTabs = findNodesByViewId(rootNode, "com.google.android.youtube:id/home_button")
            homeTabs.addAll(findNodesByViewId(rootNode, "com.google.android.youtube:id/navigation_home"))
            homeTabs.addAll(findNodesByText(rootNode, "Home", false))
            if (homeTabs.isNotEmpty()) {
                for (tab in homeTabs) {
                    if (tab.isClickable) {
                        tab.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                        Log.d(TAG, "Switched to Home tab from YouTube shorts")
                        return true
                    }
                }
            }

            // Subscriptions tab
            val subsTabs = findNodesByViewId(rootNode, "com.google.android.youtube:id/subscriptions_button")
            subsTabs.addAll(findNodesByViewId(rootNode, "com.google.android.youtube:id/navigation_subscriptions"))
            subsTabs.addAll(findNodesByText(rootNode, "Subscriptions", false))
            if (subsTabs.isNotEmpty()) {
                for (tab in subsTabs) {
                    if (tab.isClickable) {
                        tab.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                        Log.d(TAG, "Switched to Subscriptions tab from YouTube shorts")
                        return true
                    }
                }
            }
            
            // Library tab
            val libraryTabs = findNodesByViewId(rootNode, "com.google.android.youtube:id/library_button")
            libraryTabs.addAll(findNodesByViewId(rootNode, "com.google.android.youtube:id/navigation_library"))
            libraryTabs.addAll(findNodesByText(rootNode, "Library", false))
            if (libraryTabs.isNotEmpty()) {
                for (tab in libraryTabs) {
                    if (tab.isClickable) {
                        tab.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                        Log.d(TAG, "Switched to Library tab from YouTube shorts")
                        return true
                    }
                }
            }
            
            // Strategy 3: Try to click on alternative siblings if we're in the shorts tab
            val shortsTabs = findNodesByText(rootNode, "Shorts", false)
            shortsTabs.addAll(findNodesByViewId(rootNode, "com.google.android.youtube:id/shorts_button"))
            shortsTabs.addAll(findNodesByViewId(rootNode, "com.google.android.youtube:id/navigation_shorts"))
            if (shortsTabs.isNotEmpty()) {
                // If we're already on shorts tab, try to find something else to click
                for (tab in shortsTabs) {
                    // Get parent to find sibling tabs
                    val parent = tab.parent
                    if (parent != null) {
                        for (i in 0 until parent.childCount) {
                            val child = parent.getChild(i)
                            if (child != null && child != tab && child.isClickable) {
                                child.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                                Log.d(TAG, "Clicked alternative tab to exit YouTube shorts")
                                return true
                            }
                        }
                    }
                }
            }
            
            // Strategy 4: If we're in a WebView with Shorts, try to navigate to main YouTube URL
            val webViews = mutableListOf<AccessibilityNodeInfo>()
            findNodesByClassName(rootNode, "android.webkit.WebView", webViews)
            if (webViews.isNotEmpty()) {
                // Try to find address bar to navigate away
                for (webView in webViews) {
                    val addressBars = findNodesByViewId(webView, "com.android.chrome:id/url_bar")
                    addressBars.addAll(findNodesByViewId(webView, "com.android.browser:id/url"))
                    
                    if (addressBars.isNotEmpty()) {
                        // Try to navigate to YouTube main page
                        for (addressBar in addressBars) {
                            if (addressBar.isEditable) {
                                // Clear current URL
                                addressBar.performAction(AccessibilityNodeInfo.ACTION_FOCUS)
                                addressBar.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT)
                                
                                // Set new URL
                                val arguments = Bundle()
                                arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, "https://www.youtube.com/")
                                addressBar.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)
                                
                                // Find go/enter button
                                val goBtns = findNodesByViewId(webView, "com.android.chrome:id/url_action_button")
                                for (goBtn in goBtns) {
                                    if (goBtn.isClickable) {
                                        goBtn.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                                        Log.d(TAG, "Navigated away from YouTube shorts in WebView")
                                        return true
                                    }
                                }
                                
                                // Try keyboard enter as fallback
                                performGlobalAction(GLOBAL_ACTION_BACK) // Hide keyboard
                                return true
                            }
                        }
                    }
                }
            }
            
            // Strategy 5: Check for swipe up/down controls (common in YouTube shorts)
            val videoPlayerViews = findNodesByViewId(rootNode, "com.google.android.youtube:id/player_view")
            videoPlayerViews.addAll(findNodesByViewId(rootNode, "com.google.android.youtube:id/shorts_player"))
            if (videoPlayerViews.isNotEmpty()) {
                for (playerView in videoPlayerViews) {
                    if (playerView.isScrollable) {
                        // Try to scroll to exit the video
                        // Scroll down first (most shorts will exit this way)
                        val scrollDownSuccessful = playerView.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD)
                        if (scrollDownSuccessful) {
                            Log.d(TAG, "Scrolled down to exit YouTube shorts")
                            return true
                        }
                        
                        // Try scrolling up if down didn't work
                        val scrollUpSuccessful = playerView.performAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD)
                        if (scrollUpSuccessful) {
                            Log.d(TAG, "Scrolled up to exit YouTube shorts")
                            return true
                        }
                    }
                }
            }
            
            // Strategy 6: Try to find and click any navigation buttons
            val navigationBtns = findNodesByViewId(rootNode, "com.google.android.youtube:id/navigation_bar_item_icon_view")
            if (navigationBtns.isNotEmpty()) {
                // Get the first navigation button that isn't shorts
                for (btn in navigationBtns) {
                    if (btn.isClickable && 
                        btn.contentDescription?.toString()?.contains("shorts", ignoreCase = true) != true) {
                        btn.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                        Log.d(TAG, "Clicked navigation button to exit YouTube shorts")
                        return true
                    }
                }
            }
            
            // Strategy 7: Fallback to global back button
            performGlobalAction(GLOBAL_ACTION_BACK)
            Log.d(TAG, "Used global back action to exit YouTube shorts")
            return true
        } catch (e: Exception) {
            Log.e(TAG, "Error blocking YouTube shorts: ${e.message}")
            // Fallback to basic blocking
            performGlobalAction(GLOBAL_ACTION_BACK)
            return true
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
        Log.d(TAG, "Service interrupted")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Service destroyed")
        
        // Stop the foreground service
        val intent = Intent(this, FocusMonitorService::class.java)
        stopService(intent)
    }
}
