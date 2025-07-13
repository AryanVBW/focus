package com.aryanvbw.focus.ui

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import com.aryanvbw.focus.R
import com.aryanvbw.focus.databinding.ActivitySplashBinding
import com.aryanvbw.focus.util.AppSettings
import com.aryanvbw.focus.ui.onboarding.OnboardingActivity
import com.aryanvbw.focus.ui.MainActivity

class SplashActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivitySplashBinding
    private lateinit var appSettings: AppSettings
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        appSettings = AppSettings(this)
        
        setupUI()
        startAnimations()
    }
    
    private fun setupUI() {
        // Hide system UI for immersive experience
        window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        )
        
        // Set initial alpha for animations
        binding.logoIcon.alpha = 0f
        binding.appName.alpha = 0f
        binding.tagline.alpha = 0f
    }
    
    private fun startAnimations() {
        // Logo fade in
        ObjectAnimator.ofFloat(binding.logoIcon, "alpha", 0f, 1f).apply {
            duration = 800
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }
        
        // App name fade in with delay
        ObjectAnimator.ofFloat(binding.appName, "alpha", 0f, 1f).apply {
            duration = 600
            startDelay = 400
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }
        
        // Tagline fade in with delay
        ObjectAnimator.ofFloat(binding.tagline, "alpha", 0f, 1f).apply {
            duration = 600
            startDelay = 800
            interpolator = AccelerateDecelerateInterpolator()
            doOnEnd {
                // Navigate after animations complete
                Handler(Looper.getMainLooper()).postDelayed({
                    navigateToNextScreen()
                }, 1200)
            }
            start()
        }
    }
    
    private fun navigateToNextScreen() {
        val intent = if (appSettings.isFirstTimeUser()) {
            Intent(this, OnboardingActivity::class.java)
        } else {
            Intent(this, MainActivity::class.java)
        }
        
        startActivity(intent)
        
        // Custom transition
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        
        finish()
    }
    
    override fun onBackPressed() {
        // Disable back button on splash screen
        super.onBackPressed()
    }
}