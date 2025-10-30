package com.aryanvbw.focus.ui.onboarding

import android.content.Context
import android.os.Build
import android.provider.Settings
import android.view.View
import android.view.ViewTreeObserver
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView

/**
 * Performance optimization utilities for onboarding screens
 * Ensures smooth 60fps animations and respects accessibility settings
 */
object PerformanceOptimizer {
    
    /**
     * Checks if the device supports hardware acceleration
     */
    fun isHardwareAccelerated(view: View): Boolean {
        return view.isHardwareAccelerated
    }
    
    /**
     * Optimizes view for smooth animations
     */
    fun optimizeViewForAnimations(view: View) {
        // Enable hardware acceleration if not already enabled
        if (!view.isHardwareAccelerated) {
            view.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        }
        
        // Optimize for animations
        ViewCompat.setHasTransientState(view, true)
    }
    
    /**
     * Cleans up view optimizations after animations
     */
    fun cleanupViewOptimizations(view: View) {
        ViewCompat.setHasTransientState(view, false)
        
        // Reset layer type to default if we changed it
        if (view.layerType == View.LAYER_TYPE_HARDWARE) {
            view.setLayerType(View.LAYER_TYPE_NONE, null)
        }
    }
    
    /**
     * Checks system animation settings and returns appropriate duration multiplier
     */
    fun getAnimationDurationMultiplier(context: Context): Float {
        return try {
            Settings.Global.getFloat(
                context.contentResolver,
                Settings.Global.ANIMATOR_DURATION_SCALE,
                1.0f
            )
        } catch (e: Exception) {
            1.0f
        }
    }
    
    /**
     * Gets optimized animation duration based on system settings
     */
    fun getOptimizedDuration(context: Context, baseDuration: Long): Long {
        val multiplier = getAnimationDurationMultiplier(context)
        return if (multiplier == 0f) {
            0L // Animations disabled
        } else {
            (baseDuration * multiplier).toLong()
        }
    }
    
    /**
     * Checks if device is low-end and should use reduced animations
     */
    fun isLowEndDevice(): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.N || 
               Runtime.getRuntime().maxMemory() < 128 * 1024 * 1024 // Less than 128MB heap
    }
    
    /**
     * Optimizes RecyclerView for smooth scrolling
     */
    fun optimizeRecyclerView(recyclerView: RecyclerView) {
        recyclerView.apply {
            // Enable item animator optimizations
            itemAnimator?.changeDuration = 0
            itemAnimator?.moveDuration = 0
            
            // Optimize drawing cache
            setItemViewCacheSize(4)
            setHasFixedSize(true)
            
            // Enable nested scrolling
            isNestedScrollingEnabled = true
            
            // Optimize for performance
            setHasFixedSize(true)
        }
    }
    
    /**
     * Preloads views to reduce layout time
     */
    fun preloadViews(context: Context, layoutIds: List<Int>) {
        // This would typically be done in a background thread
        // For now, we'll just ensure the layouts are inflated once
        layoutIds.forEach { layoutId ->
            try {
                android.view.LayoutInflater.from(context).inflate(layoutId, null, false)
            } catch (e: Exception) {
                // Ignore inflation errors
            }
        }
    }
    
    /**
     * Monitors frame rate and logs performance issues
     */
    fun monitorFrameRate(view: View, callback: (fps: Float) -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            var frameCount = 0
            var startTime = System.currentTimeMillis()
            
            val frameCallback = object : ViewTreeObserver.OnDrawListener {
                override fun onDraw() {
                    frameCount++
                    val currentTime = System.currentTimeMillis()
                    val elapsed = currentTime - startTime
                    
                    if (elapsed >= 1000) { // Calculate FPS every second
                        val fps = frameCount * 1000f / elapsed
                        callback(fps)
                        
                        frameCount = 0
                        startTime = currentTime
                    }
                }
            }
            
            view.viewTreeObserver.addOnDrawListener(frameCallback)
        }
    }
    
    /**
     * Applies memory optimizations
     */
    fun applyMemoryOptimizations(context: Context) {
        // Suggest garbage collection (not guaranteed)
        System.gc()
        
        // Clear any unnecessary caches
        // This would be app-specific
    }
    
    /**
     * Checks if the device supports advanced animations
     */
    fun supportsAdvancedAnimations(context: Context): Boolean {
        return !isLowEndDevice() && 
               getAnimationDurationMultiplier(context) > 0f &&
               Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
    }
    
    /**
     * Gets recommended animation configuration based on device capabilities
     */
    fun getRecommendedAnimationConfig(context: Context): AnimationConfig {
        return when {
            !supportsAdvancedAnimations(context) -> AnimationConfig.MINIMAL
            isLowEndDevice() -> AnimationConfig.REDUCED
            else -> AnimationConfig.FULL
        }
    }
    
    /**
     * Optimizes window for smooth animations
     */
    fun optimizeWindow(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Use new window insets API
            ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }
    }
    
    enum class AnimationConfig {
        MINIMAL,    // No animations or very basic ones
        REDUCED,    // Simple animations only
        FULL        // All animations enabled
    }
}

/**
 * Extension functions for easier performance optimization
 */
fun View.optimizeForAnimations() {
    PerformanceOptimizer.optimizeViewForAnimations(this)
}

fun View.cleanupAnimationOptimizations() {
    PerformanceOptimizer.cleanupViewOptimizations(this)
}

fun RecyclerView.optimizePerformance() {
    PerformanceOptimizer.optimizeRecyclerView(this)
}

/**
 * Performance monitoring callback
 */
interface PerformanceCallback {
    fun onFrameRateChanged(fps: Float)
    fun onPerformanceIssue(issue: String)
}
