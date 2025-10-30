package com.aryanvbw.focus.ui.onboarding

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.WindowManager

/**
 * Testing and validation helper for onboarding experience
 * Ensures compatibility across different devices and Android versions
 */
object OnboardingTestHelper {
    
    private const val TAG = "OnboardingTest"
    
    /**
     * Tests onboarding on different screen sizes
     */
    fun testScreenSizeCompatibility(context: Context): ScreenSizeTestResult {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        
        @Suppress("DEPRECATION")
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        
        val screenWidth = displayMetrics.widthPixels
        val screenHeight = displayMetrics.heightPixels
        val density = displayMetrics.density
        
        val screenCategory = when {
            screenWidth < 480 -> ScreenCategory.SMALL
            screenWidth < 720 -> ScreenCategory.MEDIUM
            screenWidth < 1080 -> ScreenCategory.LARGE
            else -> ScreenCategory.XLARGE
        }
        
        val issues = mutableListOf<String>()
        
        // Check for potential layout issues
        if (screenWidth < 360) {
            issues.add("Screen width too small for optimal onboarding experience")
        }
        
        if (screenHeight < 640) {
            issues.add("Screen height may cause content overlap")
        }
        
        if (density < 1.0f) {
            issues.add("Low density screen may affect visual quality")
        }
        
        return ScreenSizeTestResult(
            screenCategory = screenCategory,
            width = screenWidth,
            height = screenHeight,
            density = density,
            issues = issues
        )
    }
    
    /**
     * Tests accessibility compliance
     */
    fun testAccessibilityCompliance(context: Context): AccessibilityTestResult {
        val issues = mutableListOf<String>()
        val warnings = mutableListOf<String>()
        
        // Check if TalkBack is enabled
        val accessibilityManager = context.getSystemService(Context.ACCESSIBILITY_SERVICE) 
            as android.view.accessibility.AccessibilityManager
        
        val isTalkBackEnabled = accessibilityManager.isEnabled
        
        // Check animation settings
        val animationScale = PerformanceOptimizer.getAnimationDurationMultiplier(context)
        if (animationScale == 0f) {
            warnings.add("Animations are disabled - ensure static content is accessible")
        }
        
        // Check color contrast (this would need actual color analysis)
        // For now, we'll just note it as a requirement
        warnings.add("Verify color contrast ratios meet WCAG AA standards")
        
        // Check text size settings
        val configuration = context.resources.configuration
        val fontScale = configuration.fontScale
        
        if (fontScale > 1.3f) {
            warnings.add("Large font scale detected - verify text doesn't overflow")
        }
        
        return AccessibilityTestResult(
            isTalkBackEnabled = isTalkBackEnabled,
            animationScale = animationScale,
            fontScale = fontScale,
            issues = issues,
            warnings = warnings
        )
    }
    
    /**
     * Tests performance on different Android versions
     */
    fun testAndroidVersionCompatibility(): AndroidVersionTestResult {
        val issues = mutableListOf<String>()
        val warnings = mutableListOf<String>()
        
        when {
            Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP -> {
                issues.add("Android version too old - some animations may not work")
            }
            Build.VERSION.SDK_INT < Build.VERSION_CODES.N -> {
                warnings.add("Older Android version - consider reduced animations")
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                warnings.add("New Android version - test Material You theming")
            }
        }
        
        // Check available memory
        val runtime = Runtime.getRuntime()
        val maxMemory = runtime.maxMemory()
        val totalMemory = runtime.totalMemory()
        val freeMemory = runtime.freeMemory()
        val usedMemory = totalMemory - freeMemory
        
        if (maxMemory < 128 * 1024 * 1024) { // Less than 128MB
            warnings.add("Low memory device - consider minimal animations")
        }
        
        return AndroidVersionTestResult(
            sdkVersion = Build.VERSION.SDK_INT,
            maxMemory = maxMemory,
            usedMemory = usedMemory,
            issues = issues,
            warnings = warnings
        )
    }
    
    /**
     * Tests animation performance
     */
    fun testAnimationPerformance(context: Context): AnimationTestResult {
        val issues = mutableListOf<String>()
        val recommendations = mutableListOf<String>()
        
        val isLowEndDevice = PerformanceOptimizer.isLowEndDevice()
        val supportsAdvanced = PerformanceOptimizer.supportsAdvancedAnimations(context)
        val animationConfig = PerformanceOptimizer.getRecommendedAnimationConfig(context)
        
        when (animationConfig) {
            PerformanceOptimizer.AnimationConfig.MINIMAL -> {
                recommendations.add("Use minimal animations for best performance")
            }
            PerformanceOptimizer.AnimationConfig.REDUCED -> {
                recommendations.add("Use reduced animation set for good performance")
            }
            PerformanceOptimizer.AnimationConfig.FULL -> {
                recommendations.add("Full animation support available")
            }
        }
        
        if (isLowEndDevice) {
            issues.add("Low-end device detected - animations may be choppy")
        }
        
        return AnimationTestResult(
            isLowEndDevice = isLowEndDevice,
            supportsAdvancedAnimations = supportsAdvanced,
            recommendedConfig = animationConfig,
            issues = issues,
            recommendations = recommendations
        )
    }
    
    /**
     * Runs comprehensive onboarding tests
     */
    fun runComprehensiveTest(context: Context): OnboardingTestReport {
        Log.d(TAG, "Running comprehensive onboarding tests...")
        
        val screenTest = testScreenSizeCompatibility(context)
        val accessibilityTest = testAccessibilityCompliance(context)
        val versionTest = testAndroidVersionCompatibility()
        val animationTest = testAnimationPerformance(context)
        
        val allIssues = screenTest.issues + accessibilityTest.issues + 
                       versionTest.issues + animationTest.issues
        
        val allWarnings = accessibilityTest.warnings + versionTest.warnings
        val allRecommendations = animationTest.recommendations
        
        val overallScore = calculateOverallScore(allIssues.size, allWarnings.size)
        
        Log.d(TAG, "Test completed. Score: $overallScore/100")
        
        return OnboardingTestReport(
            screenTest = screenTest,
            accessibilityTest = accessibilityTest,
            versionTest = versionTest,
            animationTest = animationTest,
            overallScore = overallScore,
            criticalIssues = allIssues,
            warnings = allWarnings,
            recommendations = allRecommendations
        )
    }
    
    private fun calculateOverallScore(issues: Int, warnings: Int): Int {
        val baseScore = 100
        val issueDeduction = issues * 20
        val warningDeduction = warnings * 5
        
        return (baseScore - issueDeduction - warningDeduction).coerceAtLeast(0)
    }
    
    enum class ScreenCategory {
        SMALL, MEDIUM, LARGE, XLARGE
    }
    
    data class ScreenSizeTestResult(
        val screenCategory: ScreenCategory,
        val width: Int,
        val height: Int,
        val density: Float,
        val issues: List<String>
    )
    
    data class AccessibilityTestResult(
        val isTalkBackEnabled: Boolean,
        val animationScale: Float,
        val fontScale: Float,
        val issues: List<String>,
        val warnings: List<String>
    )
    
    data class AndroidVersionTestResult(
        val sdkVersion: Int,
        val maxMemory: Long,
        val usedMemory: Long,
        val issues: List<String>,
        val warnings: List<String>
    )
    
    data class AnimationTestResult(
        val isLowEndDevice: Boolean,
        val supportsAdvancedAnimations: Boolean,
        val recommendedConfig: PerformanceOptimizer.AnimationConfig,
        val issues: List<String>,
        val recommendations: List<String>
    )
    
    data class OnboardingTestReport(
        val screenTest: ScreenSizeTestResult,
        val accessibilityTest: AccessibilityTestResult,
        val versionTest: AndroidVersionTestResult,
        val animationTest: AnimationTestResult,
        val overallScore: Int,
        val criticalIssues: List<String>,
        val warnings: List<String>,
        val recommendations: List<String>
    )
}
