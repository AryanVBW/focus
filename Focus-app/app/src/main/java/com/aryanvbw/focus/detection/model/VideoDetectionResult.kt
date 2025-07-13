package com.aryanvbw.focus.detection.model

import android.view.accessibility.AccessibilityNodeInfo

/**
 * Result of video content detection with enhanced scrolling information
 */
data class VideoDetectionResult(
    val detected: Boolean,
    val contentType: String = "",
    val hasVerticalScroll: Boolean = false,
    val hasHorizontalScroll: Boolean = false,
    val scrollContainers: List<AccessibilityNodeInfo> = emptyList(),
    val scrollDirection: ScrollDirection = ScrollDirection.NONE,
    val videoPlayerNodes: List<AccessibilityNodeInfo> = emptyList(),
    val confidence: Float = 0.0f // Confidence level of detection (0.0 - 1.0)
) {
    /**
     * Check if any form of scrolling is detected
     */
    fun hasScrolling(): Boolean = hasVerticalScroll || hasHorizontalScroll
    
    /**
     * Check if this is vertical video content (like reels/shorts)
     */
    fun isVerticalVideoContent(): Boolean = detected && hasVerticalScroll && 
        (contentType == "reels" || contentType == "shorts")
    
    /**
     * Check if this is horizontal video content (like stories)
     */
    fun isHorizontalVideoContent(): Boolean = detected && hasHorizontalScroll && 
        contentType == "stories"
    
    /**
     * Get the primary scroll container if available
     */
    fun getPrimaryScrollContainer(): AccessibilityNodeInfo? = scrollContainers.firstOrNull()
    
    /**
     * Check if blocking should be applied based on detection confidence
     */
    fun shouldBlock(minimumConfidence: Float = 0.5f): Boolean = 
        detected && confidence >= minimumConfidence
}