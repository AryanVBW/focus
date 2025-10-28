package com.aryanvbw.focus.detection

import android.graphics.Rect
import android.util.Log
import android.view.accessibility.AccessibilityNodeInfo
import com.aryanvbw.focus.detection.model.VideoDetectionResult
import com.aryanvbw.focus.detection.model.ScrollDirection
import com.aryanvbw.focus.util.AppSettings

/**
 * Enhanced video content detector specifically for reels and short videos
 * with improved scrolling detection and blocking capabilities
 */
class VideoContentDetector(private val settings: AppSettings) {
    
    companion object {
        private const val TAG = "VideoContentDetector"
        
        // Video player class names
        private val VIDEO_PLAYER_CLASSES = setOf(
            "VideoView", "PlayerView", "ExoPlayerView", "MediaPlayerView",
            "SurfaceView", "TextureView", "GLSurfaceView"
        )
        
        // Video control indicators
        private val VIDEO_CONTROL_IDS = setOf(
            "exo_progress", "player_control_view", "video_controls",
            "play_button", "pause_button", "seek_bar", "progress_bar"
        )
        
        // Minimum dimensions for video content (to filter out small thumbnails)
        private const val MIN_VIDEO_WIDTH = 200
        private const val MIN_VIDEO_HEIGHT = 300
        
        // Scroll detection thresholds
        private const val VERTICAL_SCROLL_THRESHOLD = 1.2 // height/width ratio
        private const val MIN_SCROLL_CONTAINER_HEIGHT = 500
    }
    
    /**
     * Detect video content with enhanced scrolling analysis
     */
    fun detectVideoContent(rootNode: AccessibilityNodeInfo, packageName: String): VideoDetectionResult {
        return when (packageName) {
            AppSettings.PACKAGE_INSTAGRAM -> detectInstagramVideoContent(rootNode)
            AppSettings.PACKAGE_YOUTUBE -> detectYouTubeVideoContent(rootNode)
            AppSettings.PACKAGE_TIKTOK -> detectTikTokVideoContent(rootNode)
            AppSettings.PACKAGE_FACEBOOK -> detectFacebookVideoContent(rootNode)
            AppSettings.PACKAGE_SNAPCHAT -> detectSnapchatVideoContent(rootNode)
            else -> VideoDetectionResult(false)
        }
    }
    
    /**
     * Detect Instagram Reels with enhanced scrolling detection
     */
    private fun detectInstagramVideoContent(rootNode: AccessibilityNodeInfo): VideoDetectionResult {
        // Check for reels indicators
        val reelsIndicators = findNodesByText(rootNode, "reel", true) +
                             findNodesByText(rootNode, "Reels", false) +
                             findNodesByViewId(rootNode, "com.instagram.android:id/reels_tray") +
                             findNodesByViewId(rootNode, "com.instagram.android:id/reel_viewer_container") +
                             findNodesByViewId(rootNode, "com.instagram.android:id/clips_tab")
        
        if (reelsIndicators.isNotEmpty()) {
            val scrollContainers = findVerticalScrollContainers(rootNode)
            val hasVerticalVideoScroll = scrollContainers.any { isVerticalVideoScrollContainer(it) }
            
            return VideoDetectionResult(
                detected = true,
                contentType = AppSettings.CONTENT_TYPE_REELS,
                hasVerticalScroll = hasVerticalVideoScroll,
                scrollContainers = scrollContainers,
                scrollDirection = if (hasVerticalVideoScroll) ScrollDirection.VERTICAL else ScrollDirection.NONE
            )
        }
        
        // Check for stories
        val storiesIndicators = findNodesByText(rootNode, "story", true) +
                               findNodesByViewId(rootNode, "com.instagram.android:id/stories_tray")
        
        if (storiesIndicators.isNotEmpty()) {
            return VideoDetectionResult(
                detected = true,
                contentType = AppSettings.CONTENT_TYPE_STORIES,
                hasVerticalScroll = false,
                scrollDirection = ScrollDirection.HORIZONTAL
            )
        }
        
        return VideoDetectionResult(false)
    }
    
    /**
     * Detect YouTube Shorts with enhanced scrolling detection
     */
    private fun detectYouTubeVideoContent(rootNode: AccessibilityNodeInfo): VideoDetectionResult {
        // Check for shorts indicators
        val shortsIndicators = findNodesByText(rootNode, "shorts", true) +
                              findNodesByText(rootNode, "Shorts", false) +
                              findNodesByViewId(rootNode, "com.google.android.youtube:id/shorts_container") +
                              findNodesByViewId(rootNode, "com.google.android.youtube:id/shorts_shelf")
        
        if (shortsIndicators.isNotEmpty()) {
            val scrollContainers = findVerticalScrollContainers(rootNode)
            val hasVerticalVideoScroll = scrollContainers.any { isVerticalVideoScrollContainer(it) }
            
            return VideoDetectionResult(
                detected = true,
                contentType = AppSettings.CONTENT_TYPE_SHORTS,
                hasVerticalScroll = hasVerticalVideoScroll,
                scrollContainers = scrollContainers,
                scrollDirection = if (hasVerticalVideoScroll) ScrollDirection.VERTICAL else ScrollDirection.NONE
            )
        }
        
        return VideoDetectionResult(false)
    }
    
    /**
     * Detect TikTok video content (all content is considered video)
     */
    private fun detectTikTokVideoContent(rootNode: AccessibilityNodeInfo): VideoDetectionResult {
        val videoIndicators = findNodesByViewId(rootNode, "com.zhiliaoapp.musically:id/feed_video") +
                             findNodesByViewId(rootNode, "com.zhiliaoapp.musically:id/video_container")
        
        if (videoIndicators.isNotEmpty()) {
            val scrollContainers = findVerticalScrollContainers(rootNode)
            
            return VideoDetectionResult(
                detected = true,
                contentType = AppSettings.CONTENT_TYPE_SHORTS,
                hasVerticalScroll = true,
                scrollContainers = scrollContainers,
                scrollDirection = ScrollDirection.VERTICAL
            )
        }
        
        return VideoDetectionResult(false)
    }
    
    /**
     * Detect Facebook Reels
     */
    private fun detectFacebookVideoContent(rootNode: AccessibilityNodeInfo): VideoDetectionResult {
        val reelsIndicators = findNodesByText(rootNode, "reel", true) +
                             findNodesByViewId(rootNode, "com.facebook.katana:id/reels_container")
        
        if (reelsIndicators.isNotEmpty()) {
            val scrollContainers = findVerticalScrollContainers(rootNode)
            val hasVerticalVideoScroll = scrollContainers.any { isVerticalVideoScrollContainer(it) }
            
            return VideoDetectionResult(
                detected = true,
                contentType = AppSettings.CONTENT_TYPE_REELS,
                hasVerticalScroll = hasVerticalVideoScroll,
                scrollContainers = scrollContainers,
                scrollDirection = if (hasVerticalVideoScroll) ScrollDirection.VERTICAL else ScrollDirection.NONE
            )
        }
        
        return VideoDetectionResult(false)
    }
    
    /**
     * Detect Snapchat stories and spotlight
     */
    private fun detectSnapchatVideoContent(rootNode: AccessibilityNodeInfo): VideoDetectionResult {
        val storiesIndicators = findNodesByText(rootNode, "story", true) +
                               findNodesByViewId(rootNode, "com.snapchat.android:id/stories_container")
        
        if (storiesIndicators.isNotEmpty()) {
            return VideoDetectionResult(
                detected = true,
                contentType = AppSettings.CONTENT_TYPE_STORIES,
                hasVerticalScroll = false,
                scrollDirection = ScrollDirection.HORIZONTAL
            )
        }
        
        return VideoDetectionResult(false)
    }
    
    /**
     * Find vertical scroll containers that likely contain video content
     */
    private fun findVerticalScrollContainers(rootNode: AccessibilityNodeInfo): List<AccessibilityNodeInfo> {
        val containers = mutableListOf<AccessibilityNodeInfo>()
        findScrollableNodes(rootNode, containers)
        
        return containers.filter { container ->
            try {
                val bounds = Rect()
                container.getBoundsInScreen(bounds)
                
                // Check if it's a vertical scroll container
                val isVertical = bounds.height() > bounds.width() * VERTICAL_SCROLL_THRESHOLD
                val isLargeEnough = bounds.height() > MIN_SCROLL_CONTAINER_HEIGHT
                val isScrollable = container.isScrollable
                val isRecyclerOrList = container.className?.let { className ->
                    className.contains("RecyclerView") || 
                    className.contains("ListView") || 
                    className.contains("ScrollView")
                } ?: false
                
                isVertical && isLargeEnough && isScrollable && isRecyclerOrList
            } catch (e: Exception) {
                Log.e(TAG, "Error analyzing scroll container: ${e.message}")
                false
            }
        }
    }
    
    /**
     * Check if a scroll container contains video content
     */
    private fun isVerticalVideoScrollContainer(container: AccessibilityNodeInfo): Boolean {
        return try {
            // Check for video player elements
            val hasVideoPlayers = hasVideoPlayerElements(container)
            
            // Check for video control elements
            val hasVideoControls = hasVideoControlElements(container)
            
            // Check for video-related content descriptions
            val hasVideoDescriptions = hasVideoContentDescriptions(container)
            
            hasVideoPlayers || hasVideoControls || hasVideoDescriptions
        } catch (e: Exception) {
            Log.e(TAG, "Error checking video scroll container: ${e.message}")
            false
        }
    }
    
    /**
     * Check for video player elements in the container
     */
    private fun hasVideoPlayerElements(container: AccessibilityNodeInfo): Boolean {
        return findVideoPlayerNodes(container).isNotEmpty()
    }
    
    /**
     * Check for video control elements
     */
    private fun hasVideoControlElements(container: AccessibilityNodeInfo): Boolean {
        return VIDEO_CONTROL_IDS.any { controlId ->
            findNodesByViewId(container, controlId).isNotEmpty()
        }
    }
    
    /**
     * Check for video-related content descriptions
     */
    private fun hasVideoContentDescriptions(container: AccessibilityNodeInfo): Boolean {
        return checkNodeForVideoDescriptions(container)
    }
    
    /**
     * Find video player nodes in the hierarchy
     */
    private fun findVideoPlayerNodes(node: AccessibilityNodeInfo): List<AccessibilityNodeInfo> {
        val videoNodes = mutableListOf<AccessibilityNodeInfo>()
        findVideoPlayerNodesRecursive(node, videoNodes)
        return videoNodes
    }
    
    /**
     * Recursively find video player nodes
     */
    private fun findVideoPlayerNodesRecursive(node: AccessibilityNodeInfo?, results: MutableList<AccessibilityNodeInfo>) {
        if (node == null) return
        
        try {
            // Check if this node is a video player
            val className = node.className?.toString()
            if (className != null && VIDEO_PLAYER_CLASSES.any { className.contains(it) }) {
                // Verify it's large enough to be a main video player
                val bounds = Rect()
                node.getBoundsInScreen(bounds)
                if (bounds.width() >= MIN_VIDEO_WIDTH && bounds.height() >= MIN_VIDEO_HEIGHT) {
                    results.add(node)
                }
            }
            
            // Recursively check children
            for (i in 0 until node.childCount) {
                val child = node.getChild(i)
                findVideoPlayerNodesRecursive(child, results)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in video player node search: ${e.message}")
        }
    }
    
    /**
     * Check node and children for video-related content descriptions
     */
    private fun checkNodeForVideoDescriptions(node: AccessibilityNodeInfo?): Boolean {
        if (node == null) return false
        
        try {
            // Check current node
            val contentDesc = node.contentDescription?.toString()?.lowercase()
            val text = node.text?.toString()?.lowercase()
            
            val videoKeywords = setOf("video", "play", "pause", "player", "reel", "short", "clip")
            
            if (contentDesc != null && videoKeywords.any { contentDesc.contains(it) }) {
                return true
            }
            
            if (text != null && videoKeywords.any { text.contains(it) }) {
                return true
            }
            
            // Check children
            for (i in 0 until node.childCount) {
                val child = node.getChild(i)
                if (checkNodeForVideoDescriptions(child)) {
                    return true
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error checking video descriptions: ${e.message}")
        }
        
        return false
    }
    
    /**
     * Find scrollable nodes recursively
     */
    private fun findScrollableNodes(node: AccessibilityNodeInfo?, results: MutableList<AccessibilityNodeInfo>) {
        if (node == null) return
        
        try {
            if (node.isScrollable) {
                results.add(node)
            }
            
            for (i in 0 until node.childCount) {
                val child = node.getChild(i)
                findScrollableNodes(child, results)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error finding scrollable nodes: ${e.message}")
        }
    }
    
    /**
     * Find nodes by text content
     */
    private fun findNodesByText(node: AccessibilityNodeInfo, text: String, partial: Boolean): List<AccessibilityNodeInfo> {
        return try {
            val nodes = node.findAccessibilityNodeInfosByText(text)
            if (partial) {
                nodes
            } else {
                nodes.filter { it.text?.toString() == text }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error finding nodes by text: ${e.message}")
            emptyList()
        }
    }
    
    /**
     * Find nodes by view ID
     */
    private fun findNodesByViewId(node: AccessibilityNodeInfo, viewId: String): List<AccessibilityNodeInfo> {
        return try {
            node.findAccessibilityNodeInfosByViewId(viewId)
        } catch (e: Exception) {
            Log.e(TAG, "Error finding nodes by view ID: ${e.message}")
            emptyList()
        }
    }
}