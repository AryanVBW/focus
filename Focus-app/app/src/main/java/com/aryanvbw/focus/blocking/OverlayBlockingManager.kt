package com.aryanvbw.focus.blocking

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.Rect
import android.os.Build
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import com.aryanvbw.focus.detection.model.ScrollDirection

/**
 * Manages overlay windows for blocking touch events and scroll gestures
 * Uses TYPE_ACCESSIBILITY_OVERLAY for trusted overlay windows that can block touches
 */
class OverlayBlockingManager(private val accessibilityService: AccessibilityService) {
    
    companion object {
        private const val TAG = "OverlayBlockingManager"
        private const val OVERLAY_ALPHA = 0.01f // Nearly transparent but still blocks touches
        private const val PULSE_ANIMATION_DURATION = 200L
    }
    
    private var windowManager: WindowManager? = null
    private var overlayView: View? = null
    private var isOverlayActive = false
    
    init {
        windowManager = accessibilityService.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }
    
    /**
     * Create a scroll blocking overlay that covers the specified area
     */
    fun createScrollBlockingOverlay(bounds: Rect, scrollDirection: ScrollDirection): Boolean {
        return try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                Log.w(TAG, "Overlay blocking requires API 24+")
                return false
            }
            
            if (isOverlayActive) {
                Log.d(TAG, "Overlay already active, removing previous overlay")
                removeOverlay()
            }
            
            val overlayLayout = createOverlayLayout(bounds, scrollDirection)
            val layoutParams = createOverlayLayoutParams(bounds)
            
            windowManager?.addView(overlayLayout, layoutParams)
            overlayView = overlayLayout
            isOverlayActive = true
            
            Log.d(TAG, "Scroll blocking overlay created successfully")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error creating overlay: ${e.message}")
            false
        }
    }
    
    /**
     * Create the overlay layout with touch blocking capabilities
     */
    private fun createOverlayLayout(bounds: Rect, scrollDirection: ScrollDirection): FrameLayout {
        val layout = object : FrameLayout(accessibilityService) {
            override fun onTouchEvent(event: MotionEvent?): Boolean {
                // Block all touch events
                when (event?.action) {
                    MotionEvent.ACTION_DOWN -> {
                        Log.d(TAG, "Touch blocked by overlay")
                        // Provide visual feedback
                        showTouchBlockedFeedback()
                    }
                    MotionEvent.ACTION_MOVE -> {
                        // Block scroll gestures
                        Log.d(TAG, "Scroll gesture blocked by overlay")
                        return true
                    }
                }
                return true // Consume all touch events
            }
            
            override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
                // Intercept and block all touch events
                return onTouchEvent(ev)
            }
        }
        
        // Set layout properties
        layout.setBackgroundColor(Color.TRANSPARENT)
        layout.alpha = OVERLAY_ALPHA
        
        // Add visual indicator for debugging (optional)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layout.tooltipText = "Scroll blocking active"
        }
        
        return layout
    }
    
    /**
     * Create layout parameters for the overlay window
     */
    private fun createOverlayLayoutParams(bounds: Rect): WindowManager.LayoutParams {
        val layoutParams = WindowManager.LayoutParams(
            bounds.width(),
            bounds.height(),
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        )
        
        // Position the overlay exactly over the scroll area
        layoutParams.gravity = Gravity.TOP or Gravity.START
        layoutParams.x = bounds.left
        layoutParams.y = bounds.top
        
        // Set window title for debugging
        layoutParams.setTitle("Focus Scroll Blocker")
        
        return layoutParams
    }
    
    /**
     * Show visual feedback when touch is blocked
     */
    private fun showTouchBlockedFeedback() {
        overlayView?.let { view ->
            // Brief visual pulse to indicate blocking
            view.animate()
                .alpha(0.3f)
                .setDuration(PULSE_ANIMATION_DURATION)
                .withEndAction {
                    view.animate()
                        .alpha(OVERLAY_ALPHA)
                        .setDuration(PULSE_ANIMATION_DURATION)
                        .start()
                }
                .start()
        }
    }
    
    /**
     * Create a full-screen overlay for maximum blocking effectiveness
     */
    fun createFullScreenBlockingOverlay(): Boolean {
        return try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                Log.w(TAG, "Full screen overlay requires API 24+")
                return false
            }
            
            if (isOverlayActive) {
                removeOverlay()
            }
            
            val overlayLayout = createFullScreenLayout()
            val layoutParams = createFullScreenLayoutParams()
            
            windowManager?.addView(overlayLayout, layoutParams)
            overlayView = overlayLayout
            isOverlayActive = true
            
            Log.d(TAG, "Full screen blocking overlay created")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error creating full screen overlay: ${e.message}")
            false
        }
    }
    
    /**
     * Create full screen layout
     */
    private fun createFullScreenLayout(): FrameLayout {
        val layout = object : FrameLayout(accessibilityService) {
            override fun onTouchEvent(event: MotionEvent?): Boolean {
                Log.d(TAG, "Full screen touch blocked")
                showTouchBlockedFeedback()
                return true
            }
            
            override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
                return onTouchEvent(ev)
            }
        }
        
        layout.setBackgroundColor(Color.TRANSPARENT)
        layout.alpha = OVERLAY_ALPHA
        
        return layout
    }
    
    /**
     * Create full screen layout parameters
     */
    private fun createFullScreenLayoutParams(): WindowManager.LayoutParams {
        return WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            setTitle("Focus Full Screen Blocker")
        }
    }
    
    /**
     * Remove the active overlay
     */
    fun removeOverlay() {
        try {
            overlayView?.let { view ->
                windowManager?.removeView(view)
                overlayView = null
                isOverlayActive = false
                Log.d(TAG, "Overlay removed successfully")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error removing overlay: ${e.message}")
            // Reset state even if removal failed
            overlayView = null
            isOverlayActive = false
        }
    }
    
    /**
     * Check if overlay is currently active
     */
    fun isActive(): Boolean = isOverlayActive
    
    /**
     * Create a temporary overlay that auto-removes after specified duration
     */
    fun createTemporaryOverlay(bounds: Rect, scrollDirection: ScrollDirection, durationMs: Long): Boolean {
        val success = createScrollBlockingOverlay(bounds, scrollDirection)
        if (success) {
            // Schedule automatic removal
            overlayView?.postDelayed({
                removeOverlay()
            }, durationMs)
        }
        return success
    }
    
    /**
     * Update overlay position and size
     */
    fun updateOverlay(newBounds: Rect) {
        try {
            overlayView?.let { view ->
                val layoutParams = view.layoutParams as WindowManager.LayoutParams
                layoutParams.width = newBounds.width()
                layoutParams.height = newBounds.height()
                layoutParams.x = newBounds.left
                layoutParams.y = newBounds.top
                
                windowManager?.updateViewLayout(view, layoutParams)
                Log.d(TAG, "Overlay updated to new bounds")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error updating overlay: ${e.message}")
        }
    }
    
    /**
     * Cleanup resources
     */
    fun cleanup() {
        removeOverlay()
        windowManager = null
    }
}