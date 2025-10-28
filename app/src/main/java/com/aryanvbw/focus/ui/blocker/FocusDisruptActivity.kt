package com.aryanvbw.focus.ui.blocker

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Vibrator
import android.view.View
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import com.aryanvbw.focus.R
import com.aryanvbw.focus.util.AppSettings
import com.aryanvbw.focus.ui.MainActivity
import kotlin.random.Random

/**
 * Full-screen disruption activity that appears when short-form video apps are detected
 * Provides motivational messaging and focus redirection
 */
class FocusDisruptActivity : AppCompatActivity() {
    
    private lateinit var appSettings: AppSettings
    private lateinit var vibrator: Vibrator
    private var blockedPackageName: String? = null
    private var contentType: String? = null
    private var temporaryUnblockHandler: Handler? = null
    private var temporaryUnblockRunnable: Runnable? = null
    
    // UI Elements
    private lateinit var focusIcon: ImageView
    private lateinit var pulseBackground: View
    private lateinit var disruptionTitle: TextView
    private lateinit var motivationalQuote: TextView
    
    // Animation variables
    private var pulseAnimator: ValueAnimator? = null
    private var iconAnimator: AnimatorSet? = null
    
    companion object {
        const val EXTRA_PACKAGE_NAME = "package_name"
        const val EXTRA_CONTENT_TYPE = "content_type"
        const val EXTRA_APP_NAME = "app_name"
        
        private val MOTIVATIONAL_QUOTES = arrayOf(
            "Stay Focused 👀",
            "Take a break from reels",
            "Your goals are waiting ⭐",
            "Focus on what matters",
            "Break the scroll cycle 🔄",
            "Time to be productive 💪",
            "Your future self will thank you",
            "Mindful moments matter",
            "Choose progress over distraction",
            "Every moment counts ⏰"
        )
        
        private val APP_SPECIFIC_MESSAGES = mapOf(
            AppSettings.PACKAGE_INSTAGRAM to arrayOf(
                "Instagram Reels detected",
                "Step away from the endless scroll",
                "Real life is more interesting"
            ),
            AppSettings.PACKAGE_YOUTUBE to arrayOf(
                "YouTube Shorts detected",
                "Time to focus on your goals",
                "Choose learning over scrolling"
            ),
            AppSettings.PACKAGE_TIKTOK to arrayOf(
                "TikTok detected",
                "Break free from the algorithm",
                "Your attention is valuable"
            ),
            AppSettings.PACKAGE_SNAPCHAT to arrayOf(
                "Snapchat Stories detected",
                "Connect with real moments",
                "Focus on meaningful connections"
            ),
            AppSettings.PACKAGE_FACEBOOK to arrayOf(
                "Facebook Reels detected",
                "Time for intentional browsing",
                "Choose quality over quantity"
            )
        )
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            setContentView(R.layout.activity_focus_disrupt)
            
            // Make activity full screen and secure
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN or
                WindowManager.LayoutParams.FLAG_SECURE or
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED,
                WindowManager.LayoutParams.FLAG_FULLSCREEN or
                WindowManager.LayoutParams.FLAG_SECURE or
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
            )
            
            // Initialize components
            appSettings = AppSettings(this)
            vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
            
            // Get intent extras
            blockedPackageName = intent.getStringExtra(EXTRA_PACKAGE_NAME)
            contentType = intent.getStringExtra(EXTRA_CONTENT_TYPE)
            val appName = intent.getStringExtra(EXTRA_APP_NAME)
            
            android.util.Log.d("FocusDisruptActivity", "Starting activity for package: $blockedPackageName, content: $contentType, app: $appName")
            
            // Initialize UI elements
            initializeViews()
            setupUI(appName)
            setupButtons()
            startAnimations()
            
            // Provide haptic feedback
            try {
                if (vibrator.hasVibrator()) {
                    vibrator.vibrate(100)
                }
            } catch (e: Exception) {
                android.util.Log.w("FocusDisruptActivity", "Failed to provide haptic feedback", e)
            }
            
            // Auto-dismiss after cooldown period if enabled
            try {
                val cooldownTime = appSettings.getDisruptionCooldownTime()
                if (cooldownTime > 0) {
                    Handler(Looper.getMainLooper()).postDelayed({
                        finishAndRemoveTask()
                    }, cooldownTime)
                }
            } catch (e: Exception) {
                android.util.Log.w("FocusDisruptActivity", "Failed to setup auto-dismiss", e)
            }
            
        } catch (e: Exception) {
            android.util.Log.e("FocusDisruptActivity", "Critical error in onCreate", e)
            // Fallback: finish activity gracefully
            finish()
        }
    }
    
    private fun initializeViews() {
        try {
            focusIcon = findViewById(R.id.iv_focus_icon)
            pulseBackground = findViewById(R.id.pulse_background)
            disruptionTitle = findViewById(R.id.tv_disruption_title)
            motivationalQuote = findViewById(R.id.tv_motivational_quote)
            
            android.util.Log.d("FocusDisruptActivity", "All views initialized successfully")
        } catch (e: Exception) {
            android.util.Log.e("FocusDisruptActivity", "Failed to initialize views", e)
            throw e // Re-throw to be caught by onCreate
        }
    }
    
    private fun setupUI(appName: String?) {
        val messageText = findViewById<TextView>(R.id.tv_disruption_message)
        
        // Set random motivational quote
        val randomQuote = MOTIVATIONAL_QUOTES[Random.nextInt(MOTIVATIONAL_QUOTES.size)]
        disruptionTitle.text = randomQuote
        
        // Set app-specific message or generic message
        val message = if (blockedPackageName != null && APP_SPECIFIC_MESSAGES.containsKey(blockedPackageName)) {
            val appMessages = APP_SPECIFIC_MESSAGES[blockedPackageName]!!
            appMessages[Random.nextInt(appMessages.size)]
        } else {
            "${appName ?: "App"} content blocked for your focus"
        }
        messageText.text = message
        
        // Set motivational quotes
        val quotes = arrayOf(
            "\"Your attention is your most valuable asset\"",
            "\"Focus is the gateway to all thinking\"",
            "\"Where attention goes, energy flows\"",
            "\"The successful warrior is the average person with laser-like focus\"",
            "\"Concentrate all your thoughts upon the work at hand\"",
            "\"Focus on being productive instead of busy\""
        )
        
        motivationalQuote.text = quotes.random()
    }
    
    private fun setupButtons() {
        try {
            val continueButton = findViewById<Button>(R.id.btn_continue_anyway)
            val focusButton = findViewById<Button>(R.id.btn_stay_focused)
            val settingsButton = findViewById<Button>(R.id.btn_open_settings)
            
            // Continue anyway button (with temporary unblock)
            continueButton.setOnClickListener {
                try {
                    animateButtonPress(it) {
                        handleTemporaryUnblock()
                    }
                } catch (e: Exception) {
                    android.util.Log.e("FocusDisruptActivity", "Error in continue button click", e)
                    handleTemporaryUnblock() // Fallback without animation
                }
            }
            
            // Stay focused button
            focusButton.setOnClickListener {
                try {
                    animateButtonPress(it) {
                        // Provide positive feedback
                        try {
                            if (vibrator.hasVibrator()) {
                                vibrator.vibrate(50)
                            }
                        } catch (e: Exception) {
                            android.util.Log.w("FocusDisruptActivity", "Failed to vibrate", e)
                        }
                        
                        // Go to home screen
                        try {
                            val homeIntent = Intent(Intent.ACTION_MAIN).apply {
                                addCategory(Intent.CATEGORY_HOME)
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            }
                            startActivity(homeIntent)
                            finishAndRemoveTask()
                        } catch (e: Exception) {
                            android.util.Log.e("FocusDisruptActivity", "Failed to go to home", e)
                            finish() // Fallback
                        }
                    }
                } catch (e: Exception) {
                    android.util.Log.e("FocusDisruptActivity", "Error in focus button click", e)
                    finish() // Fallback
                }
            }
            
            // Settings button
            settingsButton.setOnClickListener {
                try {
                    animateButtonPress(it) {
                        try {
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } catch (e: Exception) {
                            android.util.Log.e("FocusDisruptActivity", "Failed to open settings", e)
                            finish() // Fallback
                        }
                    }
                } catch (e: Exception) {
                    android.util.Log.e("FocusDisruptActivity", "Error in settings button click", e)
                    finish() // Fallback
                }
            }
            
            // Hide continue button if temporary unblock is disabled
            try {
                if (!appSettings.isTemporaryUnblockEnabled()) {
                    continueButton.visibility = View.GONE
                }
            } catch (e: Exception) {
                android.util.Log.w("FocusDisruptActivity", "Failed to check temporary unblock setting", e)
            }
            
        } catch (e: Exception) {
            android.util.Log.e("FocusDisruptActivity", "Failed to setup buttons", e)
            throw e // Re-throw to be caught by onCreate
        }
    }
    
    private fun handleTemporaryUnblock() {
        try {
            if (appSettings.isTemporaryUnblockEnabled()) {
                val unblockDuration = appSettings.getTemporaryUnblockDuration()
                appSettings.setTemporaryUnblock(blockedPackageName ?: "", unblockDuration)
                
                android.util.Log.d("FocusDisruptActivity", "Temporary unblock set for ${blockedPackageName} for ${unblockDuration}ms")
                
                // Return to the blocked app
                finish()
            } else {
                // Just close the disruption
                finish()
            }
        } catch (e: Exception) {
            android.util.Log.e("FocusDisruptActivity", "Error in handleTemporaryUnblock", e)
            finish() // Fallback
        }
    }
    
    override fun onBackPressed() {
        // Prevent back button from dismissing the disruption
        // User must choose one of the provided options
    }
    
    private fun startAnimations() {
        try {
            // Start entrance animations
            animateEntrance()
            
            // Start pulsing animation for background
            startPulseAnimation()
            
            // Start floating animation for icon
            startIconFloatingAnimation()
            
            android.util.Log.d("FocusDisruptActivity", "All animations started successfully")
        } catch (e: Exception) {
            android.util.Log.w("FocusDisruptActivity", "Failed to start animations", e)
            // Continue without animations - not critical
        }
    }
    
    private fun animateEntrance() {
        try {
            // Animate title entrance
            disruptionTitle.alpha = 0f
            disruptionTitle.translationY = -50f
            ViewCompat.animate(disruptionTitle)
                .alpha(1f)
                .translationY(0f)
                .setDuration(800)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .setStartDelay(200)
                .start()
                
            // Animate icon entrance
            focusIcon.alpha = 0f
            focusIcon.scaleX = 0.5f
            focusIcon.scaleY = 0.5f
            ViewCompat.animate(focusIcon)
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(600)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .start()
        } catch (e: Exception) {
            android.util.Log.w("FocusDisruptActivity", "Failed to animate entrance", e)
            // Set views to visible state as fallback
            try {
                disruptionTitle.alpha = 1f
                disruptionTitle.translationY = 0f
                focusIcon.alpha = 1f
                focusIcon.scaleX = 1f
                focusIcon.scaleY = 1f
            } catch (fallbackE: Exception) {
                android.util.Log.w("FocusDisruptActivity", "Failed to set fallback view states", fallbackE)
            }
        }
    }
    
    private fun startPulseAnimation() {
        try {
            pulseAnimator = ValueAnimator.ofFloat(0.3f, 0.7f).apply {
                duration = 2000
                repeatCount = ValueAnimator.INFINITE
                repeatMode = ValueAnimator.REVERSE
                interpolator = AccelerateDecelerateInterpolator()
                
                addUpdateListener { animation ->
                    try {
                        val alpha = animation.animatedValue as Float
                        pulseBackground.alpha = alpha
                    } catch (e: Exception) {
                        android.util.Log.w("FocusDisruptActivity", "Error in pulse animation update", e)
                    }
                }
                
                start()
            }
        } catch (e: Exception) {
            android.util.Log.w("FocusDisruptActivity", "Failed to start pulse animation", e)
            // Set static alpha as fallback
            try {
                pulseBackground.alpha = 0.5f
            } catch (fallbackE: Exception) {
                android.util.Log.w("FocusDisruptActivity", "Failed to set fallback pulse alpha", fallbackE)
            }
        }
    }
    
    private fun startIconFloatingAnimation() {
        val translateY = ObjectAnimator.ofFloat(focusIcon, "translationY", 0f, -20f, 0f).apply {
            duration = 3000
            repeatCount = ObjectAnimator.INFINITE
            interpolator = AccelerateDecelerateInterpolator()
        }
        
        val rotate = ObjectAnimator.ofFloat(focusIcon, "rotation", 0f, 5f, 0f, -5f, 0f).apply {
            duration = 4000
            repeatCount = ObjectAnimator.INFINITE
            interpolator = AccelerateDecelerateInterpolator()
        }
        
        iconAnimator = AnimatorSet().apply {
            playTogether(translateY, rotate)
            start()
        }
    }
    
    private fun animateButtonPress(view: View, onComplete: () -> Unit) {
        val scaleDown = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.95f).apply {
            duration = 100
        }
        val scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.95f).apply {
            duration = 100
        }
        val scaleUp = ObjectAnimator.ofFloat(view, "scaleX", 0.95f, 1f).apply {
            duration = 100
        }
        val scaleUpY = ObjectAnimator.ofFloat(view, "scaleY", 0.95f, 1f).apply {
            duration = 100
        }
        
        val animatorSet = AnimatorSet().apply {
            play(scaleDown).with(scaleDownY)
            play(scaleUp).with(scaleUpY).after(scaleDown)
        }
        
        animatorSet.start()
        
        // Execute callback after animation
        Handler(Looper.getMainLooper()).postDelayed({
            onComplete()
        }, 200)
    }
    
    override fun onPause() {
        super.onPause()
        // Finish activity when user navigates away
        finish()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        
        try {
            temporaryUnblockHandler?.removeCallbacks(temporaryUnblockRunnable!!)
        } catch (e: Exception) {
            android.util.Log.w("FocusDisruptActivity", "Error removing handler callbacks", e)
        }
        
        // Clean up animations
        try {
            pulseAnimator?.cancel()
            iconAnimator?.cancel()
        } catch (e: Exception) {
            android.util.Log.w("FocusDisruptActivity", "Error canceling animations", e)
        }
        
        android.util.Log.d("FocusDisruptActivity", "Activity destroyed and cleaned up")
    }
}