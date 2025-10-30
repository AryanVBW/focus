package com.aryanvbw.focus.ui.onboarding

import android.view.View
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.abs

/**
 * Enhanced page transformer for smooth onboarding transitions
 * Implements Material Design motion guidelines with depth and parallax effects
 */
class OnboardingPageTransformer : ViewPager2.PageTransformer {
    
    companion object {
        private const val MIN_SCALE = 0.85f
        private const val MIN_ALPHA = 0.5f
        private const val PARALLAX_COEFFICIENT = 0.3f
    }
    
    override fun transformPage(view: View, position: Float) {
        val pageWidth = view.width
        val pageHeight = view.height
        
        when {
            position < -1 -> { // [-Infinity,-1)
                // This page is way off-screen to the left
                view.alpha = 0f
            }
            position <= 1 -> { // [-1,1]
                // Apply transformations for visible pages
                applyParallaxEffect(view, position)
                applyScaleEffect(view, position)
                applyFadeEffect(view, position)
                applyDepthEffect(view, position, pageWidth)
            }
            else -> { // (1,+Infinity]
                // This page is way off-screen to the right
                view.alpha = 0f
            }
        }
    }
    
    private fun applyParallaxEffect(view: View, position: Float) {
        // Find illustration and text elements for parallax
        val illustration = view.findViewById<View>(com.aryanvbw.focus.R.id.ivIllustration)
        val title = view.findViewById<View>(com.aryanvbw.focus.R.id.tvTitle)
        val subtitle = view.findViewById<View>(com.aryanvbw.focus.R.id.tvSubtitle)
        val description = view.findViewById<View>(com.aryanvbw.focus.R.id.tvDescription)
        
        // Apply different parallax speeds for depth
        illustration?.translationX = -position * view.width * PARALLAX_COEFFICIENT * 0.5f
        title?.translationX = -position * view.width * PARALLAX_COEFFICIENT * 0.8f
        subtitle?.translationX = -position * view.width * PARALLAX_COEFFICIENT * 0.9f
        description?.translationX = -position * view.width * PARALLAX_COEFFICIENT
    }
    
    private fun applyScaleEffect(view: View, position: Float) {
        // Scale the page based on its position
        val scaleFactor = MIN_SCALE + (1 - MIN_SCALE) * (1 - abs(position))
        view.scaleX = scaleFactor
        view.scaleY = scaleFactor
    }
    
    private fun applyFadeEffect(view: View, position: Float) {
        // Fade the page based on its position
        view.alpha = MIN_ALPHA + (1 - MIN_ALPHA) * (1 - abs(position))
    }
    
    private fun applyDepthEffect(view: View, position: Float, pageWidth: Int) {
        // Create depth effect with translation
        if (position < 0) {
            // Page is moving out to the left
            view.translationX = position * pageWidth * 0.25f
        } else if (position > 0) {
            // Page is moving out to the right
            view.translationX = position * pageWidth * 0.25f
        } else {
            // Page is centered
            view.translationX = 0f
        }
    }
}

/**
 * Alternative transformer for a more subtle cube effect
 */
class OnboardingCubeTransformer : ViewPager2.PageTransformer {
    
    override fun transformPage(view: View, position: Float) {
        view.apply {
            cameraDistance = 20000f
            
            when {
                position < -1 -> {
                    alpha = 0f
                }
                position <= 0 -> {
                    alpha = 1f
                    pivotX = width.toFloat()
                    rotationY = 90 * abs(position)
                }
                position <= 1 -> {
                    alpha = 1f
                    pivotX = 0f
                    rotationY = -90 * abs(position)
                }
                else -> {
                    alpha = 0f
                }
            }
        }
    }
}

/**
 * Zoom out page transformer for a modern feel
 */
class OnboardingZoomOutTransformer : ViewPager2.PageTransformer {
    
    companion object {
        private const val MIN_SCALE = 0.85f
        private const val MIN_ALPHA = 0.5f
    }
    
    override fun transformPage(view: View, position: Float) {
        val pageWidth = view.width
        val pageHeight = view.height
        
        when {
            position < -1 -> {
                view.alpha = 0f
            }
            position <= 1 -> {
                val scaleFactor = MIN_SCALE.coerceAtLeast(1 - abs(position))
                val vertMargin = pageHeight * (1 - scaleFactor) / 2
                val horzMargin = pageWidth * (1 - scaleFactor) / 2
                
                if (position < 0) {
                    view.translationX = horzMargin - vertMargin / 2
                } else {
                    view.translationX = -horzMargin + vertMargin / 2
                }
                
                view.scaleX = scaleFactor
                view.scaleY = scaleFactor
                
                view.alpha = MIN_ALPHA + (scaleFactor - MIN_SCALE) / (1 - MIN_SCALE) * (1 - MIN_ALPHA)
            }
            else -> {
                view.alpha = 0f
            }
        }
    }
}
