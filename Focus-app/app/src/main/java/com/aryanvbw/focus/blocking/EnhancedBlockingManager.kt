package com.aryanvbw.focus.blocking

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.Context
import android.content.Intent
import android.graphics.Path
import android.graphics.PixelFormat
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.FrameLayout
import com.aryanvbw.focus.ui.blocker.FocusDisruptActivity
import com.aryanvbw.focus.util.AppSettings
import kotlin.random.Random

/**
 * Enhanced blocking manager that implements multiple blocking strategies
 * for short-form video apps as requested in the requirements
 */
class EnhancedBlockingManager(
    private val context: Context,
    private val accessibilityService: AccessibilityService,
    private val appSettings: AppSettings
) {
    
    companion object {
        private const val TAG = "EnhancedBlockingManager"
        private const val OVERLAY_ALPHA = 0.01f
        private const val GESTURE_DURATION = 100L
        private const val COUNTER_SCROLL_DISTANCE = 500f
        private const val DISRUPTION_COOLDOWN = 3000L // 3 seconds
        
        // Blocking strategies as required
        enum class BlockingStrategy {
            OVERLAY_BLOCKER,
            UI_TRAVERSAL_CONTROL,
            GESTURE_INJECTION,
            DISRUPTION_ACTIVITY
        }
        
        // Target app packages for short-form video content
        private val SHORT_FORM_VIDEO_PACKAGES = setOf(
            AppSettings.PACKAGE_INSTAGRAM,
            AppSettings.PACKAGE_YOUTUBE,
            AppSettings.PACKAGE_TIKTOK,
            AppSettings.PACKAGE_SNAPCHAT,
            AppSettings.PACKAGE_FACEBOOK
        )
    }
    
    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private val handler = Handler(Looper.getMainLooper())
    private var currentOverlay: View? = null
    private var lastDisruptionTime = 0L
    
    /**
     * Main blocking method that determines and executes the appropriate blocking strategy
     */
    fun executeBlocking(
        packageName: String,
        rootNode: AccessibilityNodeInfo?,
        contentType: String = "unknown"
    ) {
        if (!isShortFormVideoApp(packageName)) {
            Log.d(TAG, "Package $packageName not in short-form video blocklist")
            return
        }
        
        Log.i(TAG, "Executing enhanced blocking for $packageName with content type: $contentType")
        
        // Determine blocking strategy based on app settings and content type
        val strategies = determineBlockingStrategies(packageName, contentType)
        
        // Execute strategies in order of priority
        strategies.forEach { strategy ->
            when (strategy) {
                BlockingStrategy.OVERLAY_BLOCKER -> executeOverlayBlocking()
                BlockingStrategy.UI_TRAVERSAL_CONTROL -> executeUITraversalControl(rootNode)
                BlockingStrategy.GESTURE_INJECTION -> executeGestureInjection(packageName)
                BlockingStrategy.DISRUPTION_ACTIVITY -> executeDisruptionActivity(packageName, contentType)
            }
        }
    }
    
    /**
     * Strategy 1: Overlay Blocker
     * Creates a fullscreen transparent overlay that intercepts touch events
     */
    private fun executeOverlayBlocking() {
        try {
            removeCurrentOverlay()
            
            val overlay = FrameLayout(context).apply {
                setBackgroundColor(android.graphics.Color.argb((255 * OVERLAY_ALPHA).toInt(), 0, 0, 0))
                setOnTouchListener { _, event ->
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            Log.d(TAG, "Overlay blocked touch event at (${event.x}, ${event.y})")
                            // Provide haptic feedback
                            performHapticFeedback()
                            true // Consume the event
                        }
                        else -> true
                    }
                }
            }
            
            val params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT
            ).apply {
                gravity = Gravity.TOP or Gravity.START
            }
            
            windowManager.addView(overlay, params)
            currentOverlay = overlay
            
            Log.i(TAG, "Overlay blocking activated")
            
            // Auto-remove overlay after a delay
            handler.postDelayed({
                removeCurrentOverlay()
            }, 5000L)
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create overlay blocker", e)
        }
    }
    
    /**
     * Strategy 2: UI Traversal and Element Control
     * Traverses the accessibility tree and disables scrollable/media nodes
     */
    private fun executeUITraversalControl(rootNode: AccessibilityNodeInfo?) {
        rootNode?.let { root ->
            Log.i(TAG, "Executing UI traversal control")
            traverseAndControlNodes(root)
        }
    }
    
    private fun traverseAndControlNodes(node: AccessibilityNodeInfo) {
        try {
            // Check if this node is a scrollable container (common in short-form video apps)
            if (node.isScrollable) {
                Log.d(TAG, "Found scrollable node: ${node.className}")
                
                // Attempt to disable scrolling by performing counter-actions
                if (node.actionList.contains(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_FORWARD)) {
                    node.performAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD)
                }
                if (node.actionList.contains(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_BACKWARD)) {
                    node.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD)
                }
            }
            
            // Look for video-related nodes
            val className = node.className?.toString() ?: ""
            val contentDesc = node.contentDescription?.toString() ?: ""
            val text = node.text?.toString() ?: ""
            
            if (isVideoRelatedNode(className, contentDesc, text)) {
                Log.d(TAG, "Found video-related node: $className")
                
                // Try to collapse or minimize video nodes
                if (node.actionList.contains(AccessibilityNodeInfo.AccessibilityAction.ACTION_COLLAPSE)) {
                    node.performAction(AccessibilityNodeInfo.ACTION_COLLAPSE)
                }
                
                // Try to click away from video content
                if (node.isClickable) {
                    // Don't click on video nodes to avoid engagement
                    Log.d(TAG, "Avoiding click on video node")
                }
            }
            
            // Recursively traverse child nodes
            for (i in 0 until node.childCount) {
                node.getChild(i)?.let { child ->
                    traverseAndControlNodes(child)
                    child.recycle()
                }
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error in UI traversal control", e)
        }
    }
    
    /**
     * Strategy 3: Gesture Injection
     * Uses dispatchGesture to disrupt user interaction
     */
    private fun executeGestureInjection(packageName: String) {
        Log.i(TAG, "Executing gesture injection for $packageName")
        
        val gestureType = Random.nextInt(3)
        
        when (gestureType) {
            0 -> injectBackGesture()
            1 -> injectDisruptiveSwipe()
            2 -> injectCounterScroll()
        }
    }
    
    private fun injectBackGesture() {
        try {
            accessibilityService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK)
            Log.d(TAG, "Injected back gesture")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to inject back gesture", e)
        }
    }
    
    private fun injectDisruptiveSwipe() {
        try {
            val displayMetrics = context.resources.displayMetrics
            val screenWidth = displayMetrics.widthPixels.toFloat()
            val screenHeight = displayMetrics.heightPixels.toFloat()
            
            // Create a random disruptive swipe
            val startX = screenWidth * Random.nextFloat()
            val startY = screenHeight * Random.nextFloat()
            val endX = screenWidth * Random.nextFloat()
            val endY = screenHeight * Random.nextFloat()
            
            val path = Path().apply {
                moveTo(startX, startY)
                lineTo(endX, endY)
            }
            
            val gesture = GestureDescription.Builder()
                .addStroke(GestureDescription.StrokeDescription(path, 0, GESTURE_DURATION))
                .build()
            
            accessibilityService.dispatchGesture(gesture, null, null)
            Log.d(TAG, "Injected disruptive swipe from ($startX, $startY) to ($endX, $endY)")
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to inject disruptive swipe", e)
        }
    }
    
    private fun injectCounterScroll() {
        try {
            val displayMetrics = context.resources.displayMetrics
            val screenWidth = displayMetrics.widthPixels.toFloat()
            val screenHeight = displayMetrics.heightPixels.toFloat()
            
            // Counter-scroll upward to disrupt downward scrolling
            val startX = screenWidth / 2
            val startY = screenHeight * 0.7f
            val endY = startY - COUNTER_SCROLL_DISTANCE
            
            val path = Path().apply {
                moveTo(startX, startY)
                lineTo(startX, endY)
            }
            
            val gesture = GestureDescription.Builder()
                .addStroke(GestureDescription.StrokeDescription(path, 0, GESTURE_DURATION))
                .build()
            
            accessibilityService.dispatchGesture(gesture, null, null)
            Log.d(TAG, "Injected counter-scroll gesture")
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to inject counter-scroll", e)
        }
    }
    
    /**
     * Strategy 4: Launch Disruption Activity
     * Launches a full-screen focus activity with motivational messaging
     */
    private fun executeDisruptionActivity(packageName: String, contentType: String) {
        val currentTime = System.currentTimeMillis()
        
        // Prevent spam launching of disruption activity
        if (currentTime - lastDisruptionTime < DISRUPTION_COOLDOWN) {
            Log.d(TAG, "Disruption activity on cooldown")
            return
        }
        
        try {
            val intent = Intent(context, FocusDisruptActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT
                putExtra(FocusDisruptActivity.EXTRA_PACKAGE_NAME, packageName)
                putExtra(FocusDisruptActivity.EXTRA_CONTENT_TYPE, contentType)
                putExtra(FocusDisruptActivity.EXTRA_APP_NAME, getAppName(packageName))
            }
            
            Log.d(TAG, "Starting FocusDisruptActivity with intent flags: ${intent.flags}")
            Log.d(TAG, "Package: $packageName, Content: $contentType, App: ${getAppName(packageName)}")
            
            context.startActivity(intent)
            lastDisruptionTime = currentTime
            
            Log.i(TAG, "Launched disruption activity for $packageName")
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to launch disruption activity", e)
        }
    }
    
    // Helper methods
    
    private fun isShortFormVideoApp(packageName: String): Boolean {
        return SHORT_FORM_VIDEO_PACKAGES.contains(packageName)
    }
    
    private fun determineBlockingStrategies(packageName: String, contentType: String): List<BlockingStrategy> {
        val strategies = mutableListOf<BlockingStrategy>()
        
        // Always use disruption activity as primary strategy
        strategies.add(BlockingStrategy.DISRUPTION_ACTIVITY)
        
        // Add additional strategies based on app settings
        if (appSettings.isOverlayBlockingEnabled()) {
            strategies.add(BlockingStrategy.OVERLAY_BLOCKER)
        }
        
        if (appSettings.isUITraversalEnabled()) {
            strategies.add(BlockingStrategy.UI_TRAVERSAL_CONTROL)
        }
        
        if (appSettings.isGestureInjectionEnabled()) {
            strategies.add(BlockingStrategy.GESTURE_INJECTION)
        }
        
        return strategies
    }
    
    private fun isVideoRelatedNode(className: String, contentDesc: String, text: String): Boolean {
        val videoKeywords = setOf(
            "video", "reel", "short", "story", "spotlight", "player", "media",
            "VideoView", "MediaPlayer", "ExoPlayer", "RecyclerView"
        )
        
        return videoKeywords.any { keyword ->
            className.contains(keyword, ignoreCase = true) ||
            contentDesc.contains(keyword, ignoreCase = true) ||
            text.contains(keyword, ignoreCase = true)
        }
    }
    
    private fun getAppName(packageName: String): String {
        return when (packageName) {
            AppSettings.PACKAGE_INSTAGRAM -> "Instagram"
            AppSettings.PACKAGE_YOUTUBE -> "YouTube"
            AppSettings.PACKAGE_TIKTOK -> "TikTok"
            AppSettings.PACKAGE_SNAPCHAT -> "Snapchat"
            AppSettings.PACKAGE_FACEBOOK -> "Facebook"
            else -> "App"
        }
    }
    
    private fun performHapticFeedback() {
        // Provide subtle haptic feedback when blocking occurs
        try {
            val view = currentOverlay
            view?.performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to perform haptic feedback", e)
        }
    }
    
    private fun removeCurrentOverlay() {
        currentOverlay?.let { overlay ->
            try {
                windowManager.removeView(overlay)
                Log.d(TAG, "Removed overlay blocker")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to remove overlay", e)
            } finally {
                currentOverlay = null
            }
        }
    }
    
    /**
     * Clean up resources
     */
    fun cleanup() {
        removeCurrentOverlay()
        handler.removeCallbacksAndMessages(null)
    }
}