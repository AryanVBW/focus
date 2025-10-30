package com.aryanvbw.focus.ui.onboarding

import android.animation.*
import android.content.Context
import android.view.View
import android.view.animation.*
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import androidx.interpolator.view.animation.FastOutSlowInInterpolator

/**
 * Enhanced animation utilities for onboarding screens
 * Following Material Design motion guidelines
 */
object AnimationUtils {
    
    // Material Design motion durations
    const val DURATION_SHORT = 200L
    const val DURATION_MEDIUM = 300L
    const val DURATION_LONG = 500L
    const val DURATION_EXTRA_LONG = 700L
    
    // Spring animation constants
    const val SPRING_DAMPING_RATIO = SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY
    const val SPRING_STIFFNESS = SpringForce.STIFFNESS_LOW
    
    /**
     * Creates a smooth fade-in animation with slide up effect
     */
    fun createFadeInSlideUpAnimation(
        view: View,
        duration: Long = DURATION_MEDIUM,
        startDelay: Long = 0L,
        onEnd: (() -> Unit)? = null
    ): AnimatorSet {
        view.alpha = 0f
        view.translationY = 50f
        
        val fadeIn = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f)
        val slideUp = ObjectAnimator.ofFloat(view, "translationY", 50f, 0f)
        
        return AnimatorSet().apply {
            playTogether(fadeIn, slideUp)
            this.duration = duration
            this.startDelay = startDelay
            interpolator = FastOutSlowInInterpolator()
            onEnd?.let { doOnEnd { it() } }
        }
    }
    
    /**
     * Creates a spring-based scale animation for button interactions
     */
    fun createSpringScaleAnimation(view: View, scale: Float = 0.95f): SpringAnimation {
        return SpringAnimation(view, DynamicAnimation.SCALE_X).apply {
            spring = SpringForce().apply {
                finalPosition = scale
                dampingRatio = SPRING_DAMPING_RATIO
                stiffness = SPRING_STIFFNESS
            }
        }
    }
    
    /**
     * Creates a ripple-like scale animation for touch feedback
     */
    fun createRippleScaleAnimation(
        view: View,
        onAnimationEnd: (() -> Unit)? = null
    ): AnimatorSet {
        val scaleDown = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.95f).apply {
            duration = 100L
        }
        val scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.95f).apply {
            duration = 100L
        }
        val scaleUp = ObjectAnimator.ofFloat(view, "scaleX", 0.95f, 1f).apply {
            duration = 150L
        }
        val scaleUpY = ObjectAnimator.ofFloat(view, "scaleY", 0.95f, 1f).apply {
            duration = 150L
        }
        
        return AnimatorSet().apply {
            play(scaleDown).with(scaleDownY)
            play(scaleUp).with(scaleUpY).after(scaleDown)
            interpolator = FastOutSlowInInterpolator()
            onAnimationEnd?.let { doOnEnd { it() } }
        }
    }
    
    /**
     * Creates a staggered entrance animation for multiple views
     */
    fun createStaggeredEntranceAnimation(
        views: List<View>,
        staggerDelay: Long = 100L,
        onAllAnimationsEnd: (() -> Unit)? = null
    ): AnimatorSet {
        val animators = mutableListOf<Animator>()
        
        views.forEachIndexed { index, view ->
            val animation = createFadeInSlideUpAnimation(
                view = view,
                duration = DURATION_MEDIUM,
                startDelay = index * staggerDelay
            )
            animators.add(animation)
        }
        
        return AnimatorSet().apply {
            playTogether(animators)
            onAllAnimationsEnd?.let { doOnEnd { it() } }
        }
    }
    
    /**
     * Creates a smooth page transition animation
     */
    fun createPageTransitionAnimation(
        outgoingView: View,
        incomingView: View,
        direction: TransitionDirection = TransitionDirection.FORWARD,
        onTransitionComplete: (() -> Unit)? = null
    ): AnimatorSet {
        val translationDistance = outgoingView.width.toFloat()
        
        // Outgoing view animation
        val outgoingTranslation = when (direction) {
            TransitionDirection.FORWARD -> -translationDistance
            TransitionDirection.BACKWARD -> translationDistance
        }
        
        val outgoingAnim = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(outgoingView, "translationX", 0f, outgoingTranslation),
                ObjectAnimator.ofFloat(outgoingView, "alpha", 1f, 0f)
            )
            duration = DURATION_MEDIUM
        }
        
        // Incoming view animation
        val incomingStartTranslation = when (direction) {
            TransitionDirection.FORWARD -> translationDistance
            TransitionDirection.BACKWARD -> -translationDistance
        }
        
        incomingView.translationX = incomingStartTranslation
        incomingView.alpha = 0f
        
        val incomingAnim = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(incomingView, "translationX", incomingStartTranslation, 0f),
                ObjectAnimator.ofFloat(incomingView, "alpha", 0f, 1f)
            )
            duration = DURATION_MEDIUM
            startDelay = 50L // Slight overlap for smoother transition
        }
        
        return AnimatorSet().apply {
            playTogether(outgoingAnim, incomingAnim)
            interpolator = FastOutSlowInInterpolator()
            onTransitionComplete?.let { doOnEnd { it() } }
        }
    }
    
    /**
     * Creates a breathing animation for illustrations
     */
    fun createBreathingAnimation(view: View): AnimatorSet {
        val scaleUp = ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.05f).apply {
            duration = 2000L
            repeatMode = ObjectAnimator.REVERSE
            repeatCount = ObjectAnimator.INFINITE
        }
        val scaleUpY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.05f).apply {
            duration = 2000L
            repeatMode = ObjectAnimator.REVERSE
            repeatCount = ObjectAnimator.INFINITE
        }
        
        return AnimatorSet().apply {
            playTogether(scaleUp, scaleUpY)
            interpolator = AccelerateDecelerateInterpolator()
        }
    }
    
    /**
     * Creates a progress animation for the progress indicator
     */
    fun createProgressAnimation(
        view: View,
        fromProgress: Int,
        toProgress: Int,
        duration: Long = DURATION_LONG
    ): ValueAnimator {
        return ValueAnimator.ofInt(fromProgress, toProgress).apply {
            this.duration = duration
            interpolator = FastOutSlowInInterpolator()
            addUpdateListener { animator ->
                val progress = animator.animatedValue as Int
                if (view is android.widget.ProgressBar) {
                    view.progress = progress
                }
            }
        }
    }
    
    /**
     * Checks if animations should be reduced based on system settings
     */
    fun shouldReduceAnimations(context: Context): Boolean {
        val resolver = context.contentResolver
        return android.provider.Settings.Global.getFloat(
            resolver,
            android.provider.Settings.Global.ANIMATOR_DURATION_SCALE,
            1f
        ) == 0f
    }
    
    enum class TransitionDirection {
        FORWARD, BACKWARD
    }
}
