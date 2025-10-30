package com.aryanvbw.focus.ui.onboarding

import android.content.Context
import android.widget.ImageView
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.aryanvbw.focus.R

/**
 * Helper class for managing Lottie animations in onboarding screens
 * Provides fallback to static images when Lottie files are not available
 */
object LottieAnimationHelper {
    
    // Lottie animation file names (to be placed in assets/lottie/)
    private const val WELCOME_ANIMATION = "welcome_animation.json"
    private const val MODES_ANIMATION = "modes_animation.json"
    private const val BLOCKING_ANIMATION = "blocking_animation.json"
    private const val PRIVACY_ANIMATION = "privacy_animation.json"
    
    // Fallback drawable resources
    private val fallbackDrawables = mapOf(
        0 to R.drawable.ic_onboarding_welcome,
        1 to R.drawable.ic_onboarding_modes,
        2 to R.drawable.ic_onboarding_blocking,
        3 to R.drawable.ic_onboarding_privacy
    )
    
    private val lottieAnimations = mapOf(
        0 to WELCOME_ANIMATION,
        1 to MODES_ANIMATION,
        2 to BLOCKING_ANIMATION,
        3 to PRIVACY_ANIMATION
    )
    
    /**
     * Sets up animation for the given page position
     * Uses Lottie if available, falls back to static drawable
     */
    fun setupAnimation(
        context: Context,
        imageView: ImageView,
        position: Int,
        shouldReduceAnimations: Boolean = false
    ) {
        val lottieFile = lottieAnimations[position]
        val fallbackDrawable = fallbackDrawables[position]
        
        if (imageView is LottieAnimationView && lottieFile != null && !shouldReduceAnimations) {
            setupLottieAnimation(imageView, lottieFile, fallbackDrawable)
        } else {
            // Use static drawable
            fallbackDrawable?.let { imageView.setImageResource(it) }
        }
    }
    
    /**
     * Sets up Lottie animation with fallback
     */
    private fun setupLottieAnimation(
        lottieView: LottieAnimationView,
        animationFile: String,
        fallbackDrawable: Int?
    ) {
        try {
            // Try to load Lottie animation from assets
            lottieView.setAnimation("lottie/$animationFile")
            lottieView.repeatCount = LottieDrawable.INFINITE
            lottieView.repeatMode = LottieDrawable.RESTART
            
            // Set failure listener to fallback to static image
            lottieView.setFailureListener { throwable ->
                fallbackDrawable?.let { 
                    lottieView.setImageResource(it)
                }
            }
            
            // Start animation
            lottieView.playAnimation()
            
        } catch (e: Exception) {
            // Fallback to static drawable
            fallbackDrawable?.let { 
                lottieView.setImageResource(it)
            }
        }
    }
    
    /**
     * Creates entrance animation for Lottie view
     */
    fun createEntranceAnimation(lottieView: LottieAnimationView) {
        lottieView.alpha = 0f
        lottieView.scaleX = 0.8f
        lottieView.scaleY = 0.8f
        
        lottieView.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(600)
            .setStartDelay(200)
            .setInterpolator(androidx.interpolator.view.animation.FastOutSlowInInterpolator())
            .withEndAction {
                // Start Lottie animation after entrance animation
                if (lottieView.isAnimating.not()) {
                    lottieView.playAnimation()
                }
            }
            .start()
    }
    
    /**
     * Pauses all animations to respect accessibility settings
     */
    fun pauseAnimations(lottieView: LottieAnimationView) {
        if (lottieView.isAnimating) {
            lottieView.pauseAnimation()
        }
    }
    
    /**
     * Resumes animations
     */
    fun resumeAnimations(lottieView: LottieAnimationView) {
        if (!lottieView.isAnimating) {
            lottieView.resumeAnimation()
        }
    }
    
    /**
     * Checks if Lottie animation file exists in assets
     */
    private fun hasLottieFile(context: Context, fileName: String): Boolean {
        return try {
            context.assets.open("lottie/$fileName").use { true }
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Gets recommended Lottie animation properties for onboarding
     */
    fun getRecommendedLottieProperties(): LottieProperties {
        return LottieProperties(
            repeatCount = LottieDrawable.INFINITE,
            repeatMode = LottieDrawable.RESTART,
            speed = 1.0f,
            autoPlay = false // We control when to start
        )
    }
    
    data class LottieProperties(
        val repeatCount: Int,
        val repeatMode: Int,
        val speed: Float,
        val autoPlay: Boolean
    )
}

/**
 * Extension functions for easier Lottie integration
 */
fun LottieAnimationView.applyOnboardingProperties(properties: LottieAnimationHelper.LottieProperties) {
    repeatCount = properties.repeatCount
    repeatMode = properties.repeatMode
    speed = properties.speed
    
    if (properties.autoPlay) {
        playAnimation()
    }
}

/**
 * Extension to safely set Lottie animation with fallback
 */
fun LottieAnimationView.setAnimationSafely(
    animationFile: String,
    fallbackDrawable: Int? = null
) {
    try {
        setAnimation(animationFile)
        setFailureListener { 
            fallbackDrawable?.let { setImageResource(it) }
        }
    } catch (e: Exception) {
        fallbackDrawable?.let { setImageResource(it) }
    }
}
