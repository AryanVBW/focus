package com.aryanvbw.focus.ui.onboarding

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.aryanvbw.focus.R
import com.aryanvbw.focus.databinding.ActivityOnboardingBinding
import com.aryanvbw.focus.ui.MainActivity
import com.aryanvbw.focus.ui.components.ColorBendsBackgroundView
import com.aryanvbw.focus.util.AppSettings
import com.google.android.material.tabs.TabLayoutMediator

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var settings: AppSettings
    private lateinit var onboardingAdapter: OnboardingPagerAdapter
    private lateinit var colorBendsBackground: ColorBendsBackgroundView
    
    private val onboardingPages = listOf(
        OnboardingPage(
            title = "Welcome to Focus",
            subtitle = "Regain control of your digital life",
            description = "Focus helps you break free from digital distractions and build healthier technology habits.",
            imageRes = R.drawable.ic_onboarding_welcome,
            backgroundColor = R.color.onboarding_bg_1
        ),
        OnboardingPage(
            title = "Two Powerful Modes",
            subtitle = "Monitor & Focus for digital wellness",
            description = "Switch between monitoring your habits and actively blocking distractions when you need to focus.",
            imageRes = R.drawable.ic_onboarding_modes,
            backgroundColor = R.color.onboarding_bg_2
        ),
        OnboardingPage(
            title = "Smart Content Blocking",
            subtitle = "Block what matters, when it matters",
            description = "Intelligently blocks reels, shorts, and other addictive content across popular social media apps.",
            imageRes = R.drawable.ic_onboarding_blocking,
            backgroundColor = R.color.onboarding_bg_3
        ),
        OnboardingPage(
            title = "Privacy First",
            subtitle = "Your data stays on your device",
            description = "All monitoring happens locally. We never see, store, or transmit your personal data.",
            imageRes = R.drawable.ic_onboarding_privacy,
            backgroundColor = R.color.onboarding_bg_4
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        settings = AppSettings(this)

        // Initialize color bends background
        colorBendsBackground = binding.colorBendsBackground
        setupViewPager()
        setupClickListeners()
        setupInitialState()
    }
    
    private fun setupViewPager() {
        onboardingAdapter = OnboardingPagerAdapter(onboardingPages)
        binding.viewPager.adapter = onboardingAdapter
        
        // Setup page indicator
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { _, _ -> }.attach()
        
        // Page change listener for smooth transitions
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateUIForPage(position)
                animatePageTransition()
            }
        })
    }
    
    private fun setupClickListeners() {
        binding.btnNext.setOnClickListener {
            val currentItem = binding.viewPager.currentItem
            if (currentItem < onboardingPages.size - 1) {
                binding.viewPager.currentItem = currentItem + 1
            } else {
                completeOnboarding()
            }
        }
        
        binding.btnSkip.setOnClickListener {
            completeOnboarding()
        }
        
        binding.btnGetStarted.setOnClickListener {
            completeOnboarding()
        }
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
        
        // Update button visibility and text
        if (isLastPage) {
            binding.btnNext.visibility = View.GONE
            binding.btnSkip.visibility = View.GONE
            binding.btnGetStarted.visibility = View.VISIBLE
        } else {
            binding.btnNext.visibility = View.VISIBLE
            binding.btnSkip.visibility = View.VISIBLE
            binding.btnGetStarted.visibility = View.GONE
        }

        // Color-bends background is already animated, no need for manual color changes
        // The animated background provides a continuous flowing effect

        // Update progress indicator with smooth animation
        val newProgress = ((position + 1).toFloat() / onboardingPages.size.toFloat()) * 100
        val currentProgress = binding.progressIndicator.progress
        val progressAnimator = ObjectAnimator.ofInt(binding.progressIndicator, "progress", currentProgress, newProgress.toInt())
        progressAnimator.duration = 300
        progressAnimator.start()
    }

    private fun animateButtonVisibility(button: View, show: Boolean) {
        if (show && button.visibility != View.VISIBLE) {
            button.visibility = View.VISIBLE
            button.alpha = 0f
            button.animate()
                .alpha(1f)
                .setDuration(200)
                .start()
        } else if (!show && button.visibility == View.VISIBLE) {
            button.animate()
                .alpha(0f)
                .setDuration(200)
                .withEndAction { button.visibility = View.GONE }
                .start()
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
        
        // Animate exit
        binding.root.animate()
            .alpha(0f)
            .setDuration(400)
            .withEndAction {
                // Navigate to main activity
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                finish()
            }
            .start()
    }
    
    override fun onBackPressed() {
        val currentItem = binding.viewPager.currentItem
        if (currentItem > 0) {
            binding.viewPager.currentItem = currentItem - 1
        } else {
            super.onBackPressed()
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
        // Resume color-bends background animation
        colorBendsBackground.resumeAnimation()
        // Resume any paused animations
        // This would be implemented if using the enhanced adapter with Lottie
    }

    override fun onPause() {
        super.onPause()
        // Pause color-bends background animation to save battery
        colorBendsBackground.pauseAnimation()
        // Pause animations to save battery and resources
        // This would be implemented if using the enhanced adapter with Lottie
    }

    override fun onDestroy() {
        super.onDestroy()
        // Stop color-bends background animation
        colorBendsBackground.stopAnimation()
        // Clean up resources
        binding.viewPager.adapter = null
    }
}

data class OnboardingPage(
    val title: String,
    val subtitle: String,
    val description: String,
    val imageRes: Int,
    val backgroundColor: Int
)