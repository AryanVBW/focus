package com.aryanvbw.focus.ui.onboarding

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.aryanvbw.focus.R
import com.aryanvbw.focus.databinding.ActivityOnboardingBinding
import com.aryanvbw.focus.ui.MainActivity
import com.aryanvbw.focus.util.AppSettings
import com.google.android.material.tabs.TabLayoutMediator

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var settings: AppSettings
    private lateinit var onboardingAdapter: OnboardingPagerAdapter
    
    private val onboardingPages = listOf(
        OnboardingPage(
            title = "Welcome to Focus",
            subtitle = "Take back control of your time",
            description = "Join thousands who've reclaimed their digital wellness. Focus helps you build healthier technology habits without compromising productivity.",
            imageRes = R.drawable.ic_onboarding_welcome,
            backgroundColor = R.color.onboarding_bg_1
        ),
        OnboardingPage(
            title = "Two Powerful Modes",
            subtitle = "Monitor & Focus for every situation",
            description = "Monitor Mode: Track habits and gain insights.\nFocus Mode: Block distractions when you need deep work. Switch seamlessly between both.",
            imageRes = R.drawable.ic_onboarding_modes,
            backgroundColor = R.color.onboarding_bg_2
        ),
        OnboardingPage(
            title = "Smart Content Blocking",
            subtitle = "AI-powered distraction detection",
            description = "Automatically detects and blocks addictive content like reels, shorts, and infinite scroll feeds across all your favorite apps.",
            imageRes = R.drawable.ic_onboarding_blocking,
            backgroundColor = R.color.onboarding_bg_3
        ),
        OnboardingPage(
            title = "Privacy First",
            subtitle = "100% local, 0% tracking",
            description = "Everything happens on your device. No data collection, no cloud storage, no privacy compromises. Your digital habits stay yours.",
            imageRes = R.drawable.ic_onboarding_privacy,
            backgroundColor = R.color.onboarding_bg_4
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        settings = AppSettings(this)

        // Apply performance optimizations
        applyPerformanceOptimizations()

        setupViewPager()
        setupClickListeners()
        setupInitialState()
    }

    private fun applyPerformanceOptimizations() {
        // Optimize window for smooth animations
        PerformanceOptimizer.optimizeWindow(binding.root)

        // Optimize ViewPager2 for performance
        binding.viewPager.optimizePerformance()

        // Apply memory optimizations
        PerformanceOptimizer.applyMemoryOptimizations(this)

        // Monitor performance in debug builds
        if (BuildConfig.DEBUG) {
            PerformanceOptimizer.monitorFrameRate(binding.root) { fps ->
                if (fps < 50) {
                    android.util.Log.w("OnboardingPerformance", "Low FPS detected: $fps")
                }
            }
        }
    }
    
    private fun setupViewPager() {
        onboardingAdapter = OnboardingPagerAdapter(onboardingPages)
        binding.viewPager.adapter = onboardingAdapter

        // Get animation configuration based on device capabilities
        val animationConfig = PerformanceOptimizer.getRecommendedAnimationConfig(this)
        val shouldReduceAnimations = AnimationUtils.shouldReduceAnimations(this)

        // Apply page transformer based on performance capabilities
        when (animationConfig) {
            PerformanceOptimizer.AnimationConfig.FULL -> {
                if (!shouldReduceAnimations) {
                    binding.viewPager.setPageTransformer(OnboardingPageTransformer())
                }
            }
            PerformanceOptimizer.AnimationConfig.REDUCED -> {
                binding.viewPager.setPageTransformer(OnboardingZoomOutTransformer())
            }
            PerformanceOptimizer.AnimationConfig.MINIMAL -> {
                // No page transformer for minimal config
            }
        }

        // Optimize ViewPager2 settings
        binding.viewPager.offscreenPageLimit = 1 // Reduce memory usage
        binding.viewPager.isUserInputEnabled = true

        // Setup page indicator
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { _, _ -> }.attach()

        // Page change listener for smooth transitions
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateUIForPage(position)
                if (animationConfig != PerformanceOptimizer.AnimationConfig.MINIMAL) {
                    animatePageTransition()
                }
            }
        })
    }
    
    private fun setupClickListeners() {
        // Enhanced button interactions with ripple animations
        binding.btnNext.setOnClickListener { view ->
            AnimationUtils.createRippleScaleAnimation(view) {
                navigateToNextPage()
            }.start()
        }

        binding.btnSkip.setOnClickListener { view ->
            AnimationUtils.createRippleScaleAnimation(view) {
                showSkipConfirmation()
            }.start()
        }

        binding.btnGetStarted.setOnClickListener { view ->
            AnimationUtils.createRippleScaleAnimation(view) {
                completeOnboarding()
            }.start()
        }

        // Add swipe gesture support for better UX
        setupSwipeGestures()
    }

    private fun navigateToNextPage() {
        val currentItem = binding.viewPager.currentItem
        if (currentItem < onboardingPages.size - 1) {
            binding.viewPager.currentItem = currentItem + 1
        } else {
            completeOnboarding()
        }
    }

    private fun showSkipConfirmation() {
        // Show a subtle confirmation for skip action
        com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
            .setTitle("Skip Onboarding?")
            .setMessage("You can always view this introduction later in Settings.")
            .setPositiveButton("Skip") { _, _ ->
                completeOnboarding()
            }
            .setNegativeButton("Continue") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun setupSwipeGestures() {
        // ViewPager2 already handles swipe gestures, but we can add custom behavior
        binding.viewPager.isUserInputEnabled = true

        // Optional: Add custom gesture handling for enhanced UX
        // This could include haptic feedback, sound effects, etc.
    }
    
    private fun setupInitialState() {
        updateUIForPage(0)
        
        // Initial entrance animation
        binding.root.alpha = 0f
        binding.root.animate()
            .alpha(1f)
            .setDuration(800)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .start()
    }
    
    private fun updateUIForPage(position: Int) {
        val isLastPage = position == onboardingPages.size - 1

        // Update button visibility with smooth transitions
        if (isLastPage) {
            animateButtonVisibility(binding.btnNext, false)
            animateButtonVisibility(binding.btnSkip, false)
            animateButtonVisibility(binding.btnGetStarted, true)
        } else {
            animateButtonVisibility(binding.btnNext, true)
            animateButtonVisibility(binding.btnSkip, true)
            animateButtonVisibility(binding.btnGetStarted, false)
        }

        // Update background color with smooth transition
        val backgroundColor = ContextCompat.getColor(this, onboardingPages[position].backgroundColor)
        animateBackgroundColor(backgroundColor)

        // Update progress indicator with smooth animation
        val newProgress = ((position + 1).toFloat() / onboardingPages.size.toFloat()) * 100
        val currentProgress = binding.progressIndicator.progress
        AnimationUtils.createProgressAnimation(
            binding.progressIndicator,
            currentProgress,
            newProgress.toInt(),
            AnimationUtils.DURATION_MEDIUM
        ).start()
    }

    private fun animateButtonVisibility(button: View, show: Boolean) {
        if (show && button.visibility != View.VISIBLE) {
            button.visibility = View.VISIBLE
            button.alpha = 0f
            button.animate()
                .alpha(1f)
                .setDuration(AnimationUtils.DURATION_SHORT)
                .start()
        } else if (!show && button.visibility == View.VISIBLE) {
            button.animate()
                .alpha(0f)
                .setDuration(AnimationUtils.DURATION_SHORT)
                .doOnEnd { button.visibility = View.GONE }
                .start()
        }
    }

    private fun animateBackgroundColor(newColor: Int) {
        val currentColor = (binding.root.background as? android.graphics.drawable.ColorDrawable)?.color
            ?: ContextCompat.getColor(this, R.color.onboarding_bg_1)

        ValueAnimator.ofArgb(currentColor, newColor).apply {
            duration = AnimationUtils.DURATION_MEDIUM
            addUpdateListener { animator ->
                binding.root.setBackgroundColor(animator.animatedValue as Int)
            }
            start()
        }
    }
    
    private fun animatePageTransition() {
        // Animate buttons with subtle bounce effect
        val scaleX = ObjectAnimator.ofFloat(binding.btnNext, "scaleX", 0.9f, 1f)
        val scaleY = ObjectAnimator.ofFloat(binding.btnNext, "scaleY", 0.9f, 1f)
        val alpha = ObjectAnimator.ofFloat(binding.btnNext, "alpha", 0.7f, 1f)
        
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(scaleX, scaleY, alpha)
        animatorSet.duration = 300
        animatorSet.interpolator = AccelerateDecelerateInterpolator()
        animatorSet.start()
    }
    
    private fun completeOnboarding() {
        // Mark onboarding as completed
        settings.setOnboardingCompleted(true)
        settings.setFirstTimeUser(false)

        // Show completion message with call-to-action
        showCompletionMessage()
    }

    private fun showCompletionMessage() {
        // Create a brief success message
        val completionDialog = com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
            .setTitle("🎉 You're all set!")
            .setMessage("Focus is ready to help you build better digital habits. Let's start your journey to digital wellness!")
            .setPositiveButton("Let's Go!") { _, _ ->
                navigateToMainApp()
            }
            .setCancelable(false)
            .create()

        completionDialog.show()

        // Auto-dismiss after 3 seconds if user doesn't interact
        binding.root.postDelayed({
            if (completionDialog.isShowing) {
                completionDialog.dismiss()
                navigateToMainApp()
            }
        }, 3000)
    }

    private fun navigateToMainApp() {
        // Animate exit
        binding.root.animate()
            .alpha(0f)
            .setDuration(400)
            .withEndAction {
                // Navigate to main activity with welcome flag
                val intent = Intent(this, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    putExtra("show_welcome", true) // Flag to show welcome message in main app
                }
                startActivity(intent)
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                finish()
            }
            .start()
    }
    
    override fun onBackPressed() {
        val currentItem = binding.viewPager.currentItem
        if (currentItem > 0) {
            // Navigate to previous page with smooth animation
            binding.viewPager.currentItem = currentItem - 1
        } else {
            // On first page, show exit confirmation
            showExitConfirmation()
        }
    }

    private fun showExitConfirmation() {
        com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
            .setTitle("Exit Setup?")
            .setMessage("You can complete the setup anytime from the app settings.")
            .setPositiveButton("Exit") { _, _ ->
                // Mark as skipped and exit
                settings.setOnboardingCompleted(true)
                settings.setFirstTimeUser(false)
                super.onBackPressed()
            }
            .setNegativeButton("Continue Setup") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    override fun onResume() {
        super.onResume()
        // Resume any paused animations
        // This would be implemented if using the enhanced adapter with Lottie
    }

    override fun onPause() {
        super.onPause()
        // Pause animations to save battery and resources
        // This would be implemented if using the enhanced adapter with Lottie
    }

    override fun onDestroy() {
        super.onDestroy()
        // Clean up performance optimizations
        binding.viewPager.adapter = null
        binding.root.cleanupAnimationOptimizations()
    }
}

data class OnboardingPage(
    val title: String,
    val subtitle: String,
    val description: String,
    val imageRes: Int,
    val backgroundColor: Int
)