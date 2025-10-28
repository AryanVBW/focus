package com.aryanvbw.focus.detection

import android.util.Log
import android.view.accessibility.AccessibilityNodeInfo
import com.aryanvbw.focus.blocking.ScrollBlockingHandler
import com.aryanvbw.focus.detection.model.VideoDetectionResult
import com.aryanvbw.focus.util.AppSettings
import com.aryanvbw.focus.util.ContentDetector

/**
 * Coordinator that manages both legacy content detection and new video detection
 * Provides a unified interface for content detection and blocking decisions
 */
class ContentDetectionCoordinator(
    private val settings: AppSettings,
    private val legacyDetector: ContentDetector,
    private val videoDetector: VideoContentDetector,
    private val scrollBlockingHandler: ScrollBlockingHandler
) {
    
    companion object {
        private const val TAG = "ContentDetectionCoordinator"
        
        // Detection confidence thresholds
        private const val HIGH_CONFIDENCE_THRESHOLD = 0.8f
        private const val MEDIUM_CONFIDENCE_THRESHOLD = 0.6f
        private const val LOW_CONFIDENCE_THRESHOLD = 0.4f
    }
    
    /**
     * Comprehensive content detection result
     */
    data class DetectionResult(
        val shouldBlock: Boolean,
        val contentType: String,
        val detectionMethod: DetectionMethod,
        val confidence: Float,
        val videoResult: VideoDetectionResult? = null,
        val legacyResult: ContentDetector.DetectionResult? = null,
        val blockingStrategy: BlockingStrategy = BlockingStrategy.BACK_NAVIGATION
    )
    
    /**
     * Detection methods used
     */
    enum class DetectionMethod {
        LEGACY_ONLY,
        VIDEO_ONLY,
        COMBINED,
        ENHANCED_VIDEO
    }
    
    /**
     * Blocking strategies available
     */
    enum class BlockingStrategy {
        BACK_NAVIGATION,
        SCROLL_BLOCKING,
        APP_REDIRECT,
        COUNTER_GESTURE,
        HYBRID
    }
    
    /**
     * Perform comprehensive content detection
     */
    fun detectContent(rootNode: AccessibilityNodeInfo, packageName: String): DetectionResult {
        try {
            // Run both detection methods
            val legacyResult = legacyDetector.detectDistractingContent(rootNode, packageName)
            val videoResult = videoDetector.detectVideoContent(rootNode, packageName)
            
            // Determine the best result and strategy
            return combineDetectionResults(legacyResult, videoResult, packageName)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error in content detection: ${e.message}")
            return DetectionResult(
                shouldBlock = false,
                contentType = "",
                detectionMethod = DetectionMethod.LEGACY_ONLY,
                confidence = 0f
            )
        }
    }
    
    /**
     * Combine results from legacy and video detection
     */
    private fun combineDetectionResults(
        legacyResult: ContentDetector.DetectionResult,
        videoResult: VideoDetectionResult,
        packageName: String
    ): DetectionResult {
        
        // Calculate confidence scores
        val legacyConfidence = if (legacyResult.detected) 0.7f else 0f
        val videoConfidence = videoResult.confidence
        
        return when {
            // High confidence video detection with scrolling
            videoResult.detected && videoResult.hasScrolling() && videoConfidence > HIGH_CONFIDENCE_THRESHOLD -> {
                DetectionResult(
                    shouldBlock = true,
                    contentType = videoResult.contentType,
                    detectionMethod = DetectionMethod.ENHANCED_VIDEO,
                    confidence = videoConfidence,
                    videoResult = videoResult,
                    legacyResult = legacyResult,
                    blockingStrategy = determineOptimalBlockingStrategy(videoResult, packageName)
                )
            }
            
            // Both methods detect content
            legacyResult.detected && videoResult.detected -> {
                val combinedConfidence = (legacyConfidence + videoConfidence) / 2f
                DetectionResult(
                    shouldBlock = true,
                    contentType = videoResult.contentType.ifEmpty { legacyResult.contentType },
                    detectionMethod = DetectionMethod.COMBINED,
                    confidence = combinedConfidence,
                    videoResult = videoResult,
                    legacyResult = legacyResult,
                    blockingStrategy = if (videoResult.hasScrolling()) {
                        BlockingStrategy.SCROLL_BLOCKING
                    } else {
                        BlockingStrategy.BACK_NAVIGATION
                    }
                )
            }
            
            // Only video detection
            videoResult.detected && videoConfidence > MEDIUM_CONFIDENCE_THRESHOLD -> {
                DetectionResult(
                    shouldBlock = true,
                    contentType = videoResult.contentType,
                    detectionMethod = DetectionMethod.VIDEO_ONLY,
                    confidence = videoConfidence,
                    videoResult = videoResult,
                    blockingStrategy = if (videoResult.hasScrolling()) {
                        BlockingStrategy.SCROLL_BLOCKING
                    } else {
                        BlockingStrategy.BACK_NAVIGATION
                    }
                )
            }
            
            // Only legacy detection
            legacyResult.detected -> {
                DetectionResult(
                    shouldBlock = true,
                    contentType = legacyResult.contentType,
                    detectionMethod = DetectionMethod.LEGACY_ONLY,
                    confidence = legacyConfidence,
                    legacyResult = legacyResult,
                    blockingStrategy = BlockingStrategy.BACK_NAVIGATION
                )
            }
            
            // No detection
            else -> {
                DetectionResult(
                    shouldBlock = false,
                    contentType = "",
                    detectionMethod = DetectionMethod.LEGACY_ONLY,
                    confidence = 0f
                )
            }
        }
    }
    
    /**
     * Determine the optimal blocking strategy based on detection results
     */
    private fun determineOptimalBlockingStrategy(
        videoResult: VideoDetectionResult,
        packageName: String
    ): BlockingStrategy {
        return when {
            // High-confidence vertical video content with scrolling
            videoResult.isVerticalVideoContent() && videoResult.confidence > HIGH_CONFIDENCE_THRESHOLD -> {
                BlockingStrategy.SCROLL_BLOCKING
            }
            
            // Apps with known safe navigation areas
            packageName in setOf(
                AppSettings.PACKAGE_INSTAGRAM,
                AppSettings.PACKAGE_SNAPCHAT,
                AppSettings.PACKAGE_YOUTUBE
            ) && videoResult.hasScrolling() -> {
                BlockingStrategy.APP_REDIRECT
            }
            
            // Horizontal content (stories)
            videoResult.isHorizontalVideoContent() -> {
                BlockingStrategy.BACK_NAVIGATION
            }
            
            // Complex scrolling patterns
            videoResult.scrollDirection.hasVertical() && videoResult.scrollDirection.hasHorizontal() -> {
                BlockingStrategy.HYBRID
            }
            
            // Default case
            else -> BlockingStrategy.BACK_NAVIGATION
        }
    }
    
    /**
     * Execute blocking based on detection result
     */
    fun executeBlocking(detectionResult: DetectionResult, packageName: String): Boolean {
        if (!detectionResult.shouldBlock) {
            return false
        }
        
        return when (detectionResult.blockingStrategy) {
            BlockingStrategy.SCROLL_BLOCKING -> {
                detectionResult.videoResult?.let { videoResult ->
                    scrollBlockingHandler.blockScrolling(videoResult, packageName)
                } ?: false
            }
            
            BlockingStrategy.APP_REDIRECT -> {
                detectionResult.videoResult?.let { videoResult ->
                    scrollBlockingHandler.blockScrolling(videoResult, packageName)
                } ?: false
            }
            
            BlockingStrategy.COUNTER_GESTURE -> {
                detectionResult.videoResult?.let { videoResult ->
                    scrollBlockingHandler.blockScrolling(videoResult, packageName)
                } ?: false
            }
            
            BlockingStrategy.HYBRID -> {
                // Try scroll blocking first, fallback to back navigation
                val scrollSuccess = detectionResult.videoResult?.let { videoResult ->
                    scrollBlockingHandler.blockScrolling(videoResult, packageName)
                } ?: false
                
                if (!scrollSuccess) {
                    // Fallback to back navigation
                    executeBackNavigation()
                } else {
                    true
                }
            }
            
            BlockingStrategy.BACK_NAVIGATION -> {
                executeBackNavigation()
            }
        }
    }
    
    /**
     * Execute back navigation as fallback
     */
    private fun executeBackNavigation(): Boolean {
        return try {
            // This would need access to the accessibility service
            // For now, return true as placeholder
            Log.d(TAG, "Executing back navigation")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error executing back navigation: ${e.message}")
            false
        }
    }
    
    // Note: Content blocking decisions are now handled by AppSettings.shouldBlockContent()
    // which properly integrates Focus Mode and user preferences
    
    /**
     * Get detection statistics
     */
    fun getDetectionStats(): DetectionStats {
        return DetectionStats(
            totalDetections = 0, // Would be tracked in implementation
            videoDetections = 0,
            legacyDetections = 0,
            combinedDetections = 0,
            averageConfidence = 0f
        )
    }
}

/**
 * Statistics for detection performance
 */
data class DetectionStats(
    val totalDetections: Int,
    val videoDetections: Int,
    val legacyDetections: Int,
    val combinedDetections: Int,
    val averageConfidence: Float
) {
    val videoDetectionRate: Float
        get() = if (totalDetections > 0) videoDetections.toFloat() / totalDetections else 0f
        
    val combinedDetectionRate: Float
        get() = if (totalDetections > 0) combinedDetections.toFloat() / totalDetections else 0f
}