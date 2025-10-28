package com.aryanvbw.focus.blocking

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.graphics.Rect
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.accessibility.AccessibilityNodeInfo
import com.aryanvbw.focus.detection.model.ScrollDirection
import com.aryanvbw.focus.detection.model.VideoDetectionResult
import com.aryanvbw.focus.util.AppSettings

/**
 * Enhanced scroll blocking handler for video content
 * Provides sophisticated blocking mechanisms for different types of scrolling
 */
class ScrollBlockingHandler(
    private val accessibilityService: AccessibilityService,
    private val settings: AppSettings
) {
    
    companion object {
        private const val TAG = "ScrollBlockingHandler"
        
        // Gesture timing constants
        private const val GESTURE_DURATION_MS = 300L
        private const val COUNTER_SCROLL_DISTANCE = 200f
        private const val RAPID_GESTURE_INTERVAL = 100L
        private const val MAX_GESTURE_ATTEMPTS = 3
        
        // Blocking strategies
        private const val STRATEGY_BACK_NAVIGATION = "back_navigation"
        private const val STRATEGY_COUNTER_SCROLL = "counter_scroll"
        private const val STRATEGY_DISABLE_SCROLL = "disable_scroll"
        private const val STRATEGY_REDIRECT_NAVIGATION = "redirect_navigation"
        private const val STRATEGY_OVERLAY_BLOCK = "overlay_block"
        private const val STRATEGY_RAPID_COUNTER = "rapid_counter"
    }
    
    /**
     * Block scrolling based on the video detection result
     */
    fun blockScrolling(detectionResult: VideoDetectionResult, packageName: String): Boolean {
        if (!detectionResult.detected || !detectionResult.hasScrolling()) {
            return false
        }
        
        val strategy = getBlockingStrategy(detectionResult, packageName)
        
        return when (strategy) {
            STRATEGY_BACK_NAVIGATION -> executeBackNavigation()
            STRATEGY_COUNTER_SCROLL -> executeCounterScroll(detectionResult)
            STRATEGY_DISABLE_SCROLL -> executeScrollDisabling(detectionResult)
            STRATEGY_REDIRECT_NAVIGATION -> executeRedirectNavigation(packageName)
            STRATEGY_OVERLAY_BLOCK -> executeOverlayBlocking(detectionResult)
            STRATEGY_RAPID_COUNTER -> executeRapidCounterGestures(detectionResult)
            else -> executeBackNavigation() // Default fallback
        }
    }
    
    /**
     * Determine the best blocking strategy based on the detection result and app
     */
    private fun getBlockingStrategy(detectionResult: VideoDetectionResult, packageName: String): String {
        return when {
            // Very high confidence vertical video content - use enhanced scroll disabling
            detectionResult.isVerticalVideoContent() && detectionResult.confidence > 0.9f -> {
                STRATEGY_DISABLE_SCROLL
            }
            
            // High confidence vertical video content - use rapid counter-gestures
            detectionResult.isVerticalVideoContent() && detectionResult.confidence > 0.8f -> {
                STRATEGY_RAPID_COUNTER
            }
            
            // Medium confidence vertical content - use standard counter-scroll
            detectionResult.isVerticalVideoContent() && detectionResult.confidence > 0.6f -> {
                STRATEGY_COUNTER_SCROLL
            }
            
            // For apps with known safe navigation targets, use redirect
            packageName in setOf(AppSettings.PACKAGE_INSTAGRAM, AppSettings.PACKAGE_SNAPCHAT) -> {
                STRATEGY_REDIRECT_NAVIGATION
            }
            
            // TikTok and YouTube - use overlay blocking for maximum effectiveness
            packageName in setOf(AppSettings.PACKAGE_TIKTOK, AppSettings.PACKAGE_YOUTUBE) -> {
                STRATEGY_OVERLAY_BLOCK
            }
            
            // For horizontal content (stories), use back navigation
            detectionResult.isHorizontalVideoContent() -> {
                STRATEGY_BACK_NAVIGATION
            }
            
            // Default to back navigation for other cases
            else -> STRATEGY_BACK_NAVIGATION
        }
    }
    
    /**
     * Execute back navigation to exit the current screen
     */
    private fun executeBackNavigation(): Boolean {
        return try {
            val success = accessibilityService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK)
            Log.d(TAG, "Back navigation executed: $success")
            success
        } catch (e: Exception) {
            Log.e(TAG, "Error executing back navigation: ${e.message}")
            false
        }
    }
    
    /**
     * Execute counter-scroll gesture to counteract user scrolling
     */
    private fun executeCounterScroll(detectionResult: VideoDetectionResult): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            Log.w(TAG, "Counter-scroll requires API 24+, falling back to back navigation")
            return executeBackNavigation()
        }
        
        val scrollContainer = detectionResult.getPrimaryScrollContainer() ?: return false
        
        return try {
            val bounds = Rect()
            scrollContainer.getBoundsInScreen(bounds)
            
            val centerX = bounds.centerX().toFloat()
            val centerY = bounds.centerY().toFloat()
            
            val path = Path()
            
            when (detectionResult.scrollDirection) {
                ScrollDirection.VERTICAL -> {
                    // Counter vertical scroll with upward gesture
                    path.moveTo(centerX, centerY + COUNTER_SCROLL_DISTANCE)
                    path.lineTo(centerX, centerY - COUNTER_SCROLL_DISTANCE)
                }
                ScrollDirection.HORIZONTAL -> {
                    // Counter horizontal scroll with leftward gesture
                    path.moveTo(centerX + COUNTER_SCROLL_DISTANCE, centerY)
                    path.lineTo(centerX - COUNTER_SCROLL_DISTANCE, centerY)
                }
                ScrollDirection.BOTH -> {
                    // Counter with diagonal gesture
                    path.moveTo(centerX + COUNTER_SCROLL_DISTANCE, centerY + COUNTER_SCROLL_DISTANCE)
                    path.lineTo(centerX - COUNTER_SCROLL_DISTANCE, centerY - COUNTER_SCROLL_DISTANCE)
                }
                ScrollDirection.NONE -> return false
            }
            
            val gestureBuilder = GestureDescription.Builder()
            val strokeDescription = GestureDescription.StrokeDescription(path, 0, GESTURE_DURATION_MS)
            gestureBuilder.addStroke(strokeDescription)
            
            val success = accessibilityService.dispatchGesture(
                gestureBuilder.build(),
                object : AccessibilityService.GestureResultCallback() {
                    override fun onCompleted(gestureDescription: GestureDescription?) {
                        Log.d(TAG, "Counter-scroll gesture completed")
                    }
                    
                    override fun onCancelled(gestureDescription: GestureDescription?) {
                        Log.w(TAG, "Counter-scroll gesture cancelled")
                    }
                },
                null
            )
            
            Log.d(TAG, "Counter-scroll executed: $success")
            success
        } catch (e: Exception) {
            Log.e(TAG, "Error executing counter-scroll: ${e.message}")
            executeBackNavigation() // Fallback
        }
    }
    
    /**
     * Enhanced scroll disabling with overlay blocking and rapid counter-gestures
     */
    private fun executeScrollDisabling(detectionResult: VideoDetectionResult): Boolean {
        return try {
            // Strategy 1: Try overlay blocking first
            if (executeOverlayBlocking(detectionResult)) {
                Log.d(TAG, "Overlay blocking successful")
                return true
            }
            
            // Strategy 2: Rapid counter-gestures
            if (executeRapidCounterGestures(detectionResult)) {
                Log.d(TAG, "Rapid counter-gestures successful")
                return true
            }
            
            // Strategy 3: Enhanced counter-scroll with multiple attempts
            if (executeEnhancedCounterScroll(detectionResult)) {
                Log.d(TAG, "Enhanced counter-scroll successful")
                return true
            }
            
            // Fallback to back navigation
            Log.d(TAG, "All scroll disabling strategies failed, using back navigation")
            executeBackNavigation()
        } catch (e: Exception) {
            Log.e(TAG, "Error in scroll disabling: ${e.message}")
            executeBackNavigation()
        }
    }
    
    /**
     * Execute redirect navigation to safe areas of the app
     */
    private fun executeRedirectNavigation(packageName: String): Boolean {
        return when (packageName) {
            AppSettings.PACKAGE_INSTAGRAM -> redirectInstagramToSafeArea()
            AppSettings.PACKAGE_SNAPCHAT -> redirectSnapchatToSafeArea()
            AppSettings.PACKAGE_YOUTUBE -> redirectYouTubeToSafeArea()
            else -> executeBackNavigation()
        }
    }
    
    /**
     * Redirect Instagram to home feed or profile
     */
    private fun redirectInstagramToSafeArea(): Boolean {
        return try {
            val rootNode = accessibilityService.rootInActiveWindow ?: return false
            
            // Try to find and click home tab
            val homeTargets = setOf(
                "com.instagram.android:id/tab_home",
                "com.instagram.android:id/navigation_home"
            )
            
            for (targetId in homeTargets) {
                val homeNodes = rootNode.findAccessibilityNodeInfosByViewId(targetId)
                if (homeNodes.isNotEmpty()) {
                    val homeNode = homeNodes.first()
                    if (homeNode.isClickable) {
                        val success = homeNode.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                        Log.d(TAG, "Instagram redirect to home: $success")
                        return success
                    }
                }
            }
            
            // Fallback to back navigation
            executeBackNavigation()
        } catch (e: Exception) {
            Log.e(TAG, "Error redirecting Instagram: ${e.message}")
            executeBackNavigation()
        }
    }
    
    /**
     * Redirect Snapchat to camera or chat
     */
    private fun redirectSnapchatToSafeArea(): Boolean {
        return try {
            val rootNode = accessibilityService.rootInActiveWindow ?: return false
            
            // Try to find and click camera tab
            val cameraTargets = setOf(
                "com.snapchat.android:id/camera_tab",
                "com.snapchat.android:id/tab_bar_camera_option"
            )
            
            for (targetId in cameraTargets) {
                val cameraNodes = rootNode.findAccessibilityNodeInfosByViewId(targetId)
                if (cameraNodes.isNotEmpty()) {
                    val cameraNode = cameraNodes.first()
                    if (cameraNode.isClickable) {
                        val success = cameraNode.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                        Log.d(TAG, "Snapchat redirect to camera: $success")
                        return success
                    }
                }
            }
            
            // Fallback to back navigation
            executeBackNavigation()
        } catch (e: Exception) {
            Log.e(TAG, "Error redirecting Snapchat: ${e.message}")
            executeBackNavigation()
        }
    }
    
    /**
     * Redirect YouTube to home or subscriptions
     */
    private fun redirectYouTubeToSafeArea(): Boolean {
        return try {
            val rootNode = accessibilityService.rootInActiveWindow ?: return false
            
            // Try to find and click home tab
            val homeTargets = setOf(
                "com.google.android.youtube:id/tab_home",
                "com.google.android.youtube:id/navigation_home"
            )
            
            for (targetId in homeTargets) {
                val homeNodes = rootNode.findAccessibilityNodeInfosByViewId(targetId)
                if (homeNodes.isNotEmpty()) {
                    val homeNode = homeNodes.first()
                    if (homeNode.isClickable) {
                        val success = homeNode.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                        Log.d(TAG, "YouTube redirect to home: $success")
                        return success
                    }
                }
            }
            
            // Fallback to back navigation
            executeBackNavigation()
        } catch (e: Exception) {
            Log.e(TAG, "Error redirecting YouTube: ${e.message}")
            executeBackNavigation()
        }
    }
    
    /**
     * Check if scroll blocking is enabled for the given package
     */
    fun isScrollBlockingEnabled(packageName: String): Boolean {
        return when (packageName) {
            AppSettings.PACKAGE_INSTAGRAM -> settings.isInstagramReelsBlocked()
            AppSettings.PACKAGE_YOUTUBE -> settings.isYouTubeShortsBlocked()
            AppSettings.PACKAGE_TIKTOK -> settings.isTikTokBlocked()
            AppSettings.PACKAGE_FACEBOOK -> settings.isFacebookReelsBlocked()
            AppSettings.PACKAGE_SNAPCHAT -> settings.isSnapchatStoriesBlocked()
            else -> false
        }
    }
    
    /**
     * Execute overlay blocking using TYPE_ACCESSIBILITY_OVERLAY
     */
    private fun executeOverlayBlocking(detectionResult: VideoDetectionResult): Boolean {
        return try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                Log.w(TAG, "Overlay blocking requires API 24+")
                return false
            }
            
            val scrollContainer = detectionResult.getPrimaryScrollContainer() ?: return false
            val bounds = Rect()
            scrollContainer.getBoundsInScreen(bounds)
            
            // Create transparent overlay that blocks touch events
            val overlayManager = OverlayBlockingManager(accessibilityService)
            overlayManager.createScrollBlockingOverlay(bounds, detectionResult.scrollDirection)
            
            // Schedule overlay removal after a short duration
            Handler(Looper.getMainLooper()).postDelayed({
                overlayManager.removeOverlay()
            }, GESTURE_DURATION_MS * 2)
            
            Log.d(TAG, "Overlay blocking executed")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error in overlay blocking: ${e.message}")
            false
        }
    }
    
    /**
     * Execute rapid counter-gestures to overwhelm user scroll attempts
     */
    private fun executeRapidCounterGestures(detectionResult: VideoDetectionResult): Boolean {
        return try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                Log.w(TAG, "Rapid gestures require API 24+")
                return false
            }
            
            val scrollContainer = detectionResult.getPrimaryScrollContainer() ?: return false
            val bounds = Rect()
            scrollContainer.getBoundsInScreen(bounds)
            
            val centerX = bounds.centerX().toFloat()
            val centerY = bounds.centerY().toFloat()
            
            // Execute multiple rapid counter-gestures
            for (i in 0 until MAX_GESTURE_ATTEMPTS) {
                val path = Path()
                
                when (detectionResult.scrollDirection) {
                    ScrollDirection.VERTICAL -> {
                        // Multiple upward gestures with slight variations
                        val offset = (i * 50f)
                        path.moveTo(centerX + offset, centerY + COUNTER_SCROLL_DISTANCE)
                        path.lineTo(centerX + offset, centerY - COUNTER_SCROLL_DISTANCE)
                    }
                    ScrollDirection.HORIZONTAL -> {
                        // Multiple leftward gestures
                        val offset = (i * 50f)
                        path.moveTo(centerX + COUNTER_SCROLL_DISTANCE, centerY + offset)
                        path.lineTo(centerX - COUNTER_SCROLL_DISTANCE, centerY + offset)
                    }
                    else -> return false
                }
                
                val gestureBuilder = GestureDescription.Builder()
                val strokeDescription = GestureDescription.StrokeDescription(
                    path, 
                    i * RAPID_GESTURE_INTERVAL, 
                    GESTURE_DURATION_MS / 2
                )
                gestureBuilder.addStroke(strokeDescription)
                
                accessibilityService.dispatchGesture(
                    gestureBuilder.build(),
                    null,
                    null
                )
            }
            
            Log.d(TAG, "Rapid counter-gestures executed: $MAX_GESTURE_ATTEMPTS gestures")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error in rapid counter-gestures: ${e.message}")
            false
        }
    }
    
    /**
     * Enhanced counter-scroll with improved timing and multiple attempts
     */
    private fun executeEnhancedCounterScroll(detectionResult: VideoDetectionResult): Boolean {
        return try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                Log.w(TAG, "Enhanced counter-scroll requires API 24+")
                return false
            }
            
            val scrollContainer = detectionResult.getPrimaryScrollContainer() ?: return false
            val bounds = Rect()
            scrollContainer.getBoundsInScreen(bounds)
            
            val centerX = bounds.centerX().toFloat()
            val centerY = bounds.centerY().toFloat()
            
            // Create a more complex gesture path
            val path = Path()
            
            when (detectionResult.scrollDirection) {
                ScrollDirection.VERTICAL -> {
                    // Create a curved counter-scroll gesture
                    path.moveTo(centerX - 100f, centerY + COUNTER_SCROLL_DISTANCE)
                    path.quadTo(centerX, centerY, centerX + 100f, centerY - COUNTER_SCROLL_DISTANCE)
                }
                ScrollDirection.HORIZONTAL -> {
                    // Create a curved horizontal counter-gesture
                    path.moveTo(centerX + COUNTER_SCROLL_DISTANCE, centerY - 100f)
                    path.quadTo(centerX, centerY, centerX - COUNTER_SCROLL_DISTANCE, centerY + 100f)
                }
                ScrollDirection.BOTH -> {
                    // Complex diagonal gesture
                    path.moveTo(centerX + COUNTER_SCROLL_DISTANCE, centerY + COUNTER_SCROLL_DISTANCE)
                    path.quadTo(centerX, centerY, centerX - COUNTER_SCROLL_DISTANCE, centerY - COUNTER_SCROLL_DISTANCE)
                }
                ScrollDirection.NONE -> return false
            }
            
            val gestureBuilder = GestureDescription.Builder()
            val strokeDescription = GestureDescription.StrokeDescription(path, 0, GESTURE_DURATION_MS)
            gestureBuilder.addStroke(strokeDescription)
            
            val success = accessibilityService.dispatchGesture(
                gestureBuilder.build(),
                object : AccessibilityService.GestureResultCallback() {
                    override fun onCompleted(gestureDescription: GestureDescription?) {
                        Log.d(TAG, "Enhanced counter-scroll completed")
                    }
                    
                    override fun onCancelled(gestureDescription: GestureDescription?) {
                        Log.w(TAG, "Enhanced counter-scroll cancelled, trying backup gesture")
                        // Execute immediate backup gesture
                        executeBackupCounterGesture(detectionResult)
                    }
                },
                null
            )
            
            Log.d(TAG, "Enhanced counter-scroll executed: $success")
            success
        } catch (e: Exception) {
            Log.e(TAG, "Error in enhanced counter-scroll: ${e.message}")
            false
        }
    }
    
    /**
     * Execute backup counter-gesture when primary gesture is cancelled
     */
    private fun executeBackupCounterGesture(detectionResult: VideoDetectionResult) {
        try {
            val scrollContainer = detectionResult.getPrimaryScrollContainer() ?: return
            val bounds = Rect()
            scrollContainer.getBoundsInScreen(bounds)
            
            val centerX = bounds.centerX().toFloat()
            val centerY = bounds.centerY().toFloat()
            
            val path = Path()
            
            // Simple, fast counter-gesture
            when (detectionResult.scrollDirection) {
                ScrollDirection.VERTICAL -> {
                    path.moveTo(centerX, centerY + 50f)
                    path.lineTo(centerX, centerY - 50f)
                }
                ScrollDirection.HORIZONTAL -> {
                    path.moveTo(centerX + 50f, centerY)
                    path.lineTo(centerX - 50f, centerY)
                }
                else -> return
            }
            
            val gestureBuilder = GestureDescription.Builder()
            val strokeDescription = GestureDescription.StrokeDescription(path, 0, 100L)
            gestureBuilder.addStroke(strokeDescription)
            
            accessibilityService.dispatchGesture(gestureBuilder.build(), null, null)
            Log.d(TAG, "Backup counter-gesture executed")
        } catch (e: Exception) {
            Log.e(TAG, "Error in backup counter-gesture: ${e.message}")
        }
    }
    
    /**
     * Get blocking effectiveness metrics
     */
    fun getBlockingMetrics(): ScrollBlockingMetrics {
        // This would be implemented to track blocking success rates
        return ScrollBlockingMetrics()
    }
}

/**
 * Data class for tracking scroll blocking metrics
 */
data class ScrollBlockingMetrics(
    val totalAttempts: Int = 0,
    val successfulBlocks: Int = 0,
    val failedBlocks: Int = 0,
    val averageResponseTime: Long = 0L
) {
    val successRate: Float
        get() = if (totalAttempts > 0) successfulBlocks.toFloat() / totalAttempts else 0f
}