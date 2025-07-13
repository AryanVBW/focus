package com.aryanvbw.focus.util

import android.view.accessibility.AccessibilityNodeInfo
import com.aryanvbw.focus.util.AppSettings.Companion.CONTENT_TYPE_EXPLORE
import com.aryanvbw.focus.util.AppSettings.Companion.CONTENT_TYPE_REELS
import com.aryanvbw.focus.util.AppSettings.Companion.CONTENT_TYPE_SHORTS
import com.aryanvbw.focus.util.AppSettings.Companion.CONTENT_TYPE_STORIES
import com.aryanvbw.focus.util.AppSettings.Companion.PACKAGE_FACEBOOK
import com.aryanvbw.focus.util.AppSettings.Companion.PACKAGE_INSTAGRAM
import com.aryanvbw.focus.util.AppSettings.Companion.PACKAGE_SNAPCHAT
import com.aryanvbw.focus.util.AppSettings.Companion.PACKAGE_TIKTOK
import com.aryanvbw.focus.util.AppSettings.Companion.PACKAGE_TWITTER
import com.aryanvbw.focus.util.AppSettings.Companion.PACKAGE_YOUTUBE

/**
 * Legacy utility class to detect distracting content in various apps
 * This class is maintained for backward compatibility while the new
 * enhanced detection system is being integrated.
 */
class ContentDetector(private val settings: AppSettings) {

    data class DetectionResult(val detected: Boolean, val contentType: String = "") {
        /**
         * Convert to enhanced detection result format
         */
        fun toEnhancedResult(): com.aryanvbw.focus.detection.model.VideoDetectionResult {
            return com.aryanvbw.focus.detection.model.VideoDetectionResult(
                detected = detected,
                contentType = contentType,
                confidence = if (detected) 0.7f else 0f
            )
        }
    }

    /**
     * Detect distracting content based on the app and UI structure
     */
    fun detectDistractingContent(rootNode: AccessibilityNodeInfo, packageName: String): DetectionResult {
        return when (packageName) {
            PACKAGE_INSTAGRAM -> detectInstagramContent(rootNode)
            PACKAGE_YOUTUBE -> detectYouTubeContent(rootNode)
            PACKAGE_SNAPCHAT -> detectSnapchatContent(rootNode)
            PACKAGE_TIKTOK -> detectTikTokContent(rootNode)
            PACKAGE_FACEBOOK -> detectFacebookContent(rootNode)
            PACKAGE_TWITTER -> detectTwitterContent(rootNode)
            else -> DetectionResult(false)
        }
    }
    
    /**
     * Detect Instagram reels, stories, and explore content
     * Enhanced detection with multiple approaches
     */
    private fun detectInstagramContent(rootNode: AccessibilityNodeInfo): DetectionResult {
        // Strategy 1: Check for reels via text and view IDs
        if (findNodesByText(rootNode, "reel", true).isNotEmpty() ||
            findNodesByText(rootNode, "Reels", false).isNotEmpty() ||
            findNodesByViewId(rootNode, "com.instagram.android:id/reels_tray").isNotEmpty() ||
            findNodesByViewId(rootNode, "com.instagram.android:id/reel_viewer_container").isNotEmpty() ||
            findNodesByViewId(rootNode, "com.instagram.android:id/clips_tab").isNotEmpty() ||
            findNodesByViewId(rootNode, "com.instagram.android:id/clips_tab_button").isNotEmpty()) {
            return DetectionResult(true, CONTENT_TYPE_REELS)
        }
        
        // Strategy 2: Look for WebView with reels URLs
        val webViewUrls = extractWebViewUrls(rootNode)
        for (url in webViewUrls) {
            if (url.contains("instagram.com/reels") || url.contains("instagram.com/reel")) {
                return DetectionResult(true, CONTENT_TYPE_REELS)
            }
        }
        
        // Strategy 3: Look for vertical video containers with specific characteristics of reels
        val verticalScrollContainers = findVerticalScrollContainers(rootNode)
        for (container in verticalScrollContainers) {
            // Check for reels-specific indicators
            val hasReelsIndicators = container.contentDescription?.toString()?.contains("reel", ignoreCase = true) == true ||
                                    findNodesByText(container, "reel", true).isNotEmpty() ||
                                    findNodesByText(container, "Reels", false).isNotEmpty()
            
            // Look for like button, comment button, share button that commonly appear in reels
            val hasReelsControls = findNodesByViewId(container, "com.instagram.android:id/like_button").isNotEmpty() &&
                                 findNodesByViewId(container, "com.instagram.android:id/comment_button").isNotEmpty() &&
                                 findNodesByViewId(container, "com.instagram.android:id/share_button").isNotEmpty()
            
            if (hasReelsIndicators || hasReelsControls) {
                return DetectionResult(true, CONTENT_TYPE_REELS)
            }
        }
        
        // Check for stories
        if (findNodesByText(rootNode, "story", true).isNotEmpty() ||
            findNodesByViewId(rootNode, "com.instagram.android:id/stories_tray").isNotEmpty() ||
            findNodesByViewId(rootNode, "com.instagram.android:id/story_viewer_container").isNotEmpty()) {
            return DetectionResult(true, CONTENT_TYPE_STORIES)
        }
        
        // Check for explore page
        if (findNodesByViewId(rootNode, "com.instagram.android:id/explore_grid").isNotEmpty() ||
            findNodesByViewId(rootNode, "com.instagram.android:id/explore_container").isNotEmpty()) {
            return DetectionResult(true, CONTENT_TYPE_EXPLORE)
        }
        
        return DetectionResult(false)
    }
    
    /**
     * Detect YouTube shorts and suggested videos with enhanced detection
     */
    private fun detectYouTubeContent(rootNode: AccessibilityNodeInfo): DetectionResult {
        // Strategy 1: Look for shorts UI elements via text and view IDs
        if (findNodesByText(rootNode, "shorts", true).isNotEmpty() ||
            findNodesByText(rootNode, "Shorts", false).isNotEmpty() ||
            findNodesByViewId(rootNode, "com.google.android.youtube:id/shorts_container").isNotEmpty() ||
            findNodesByViewId(rootNode, "com.google.android.youtube:id/shorts_shelf").isNotEmpty() ||
            findNodesByViewId(rootNode, "com.google.android.youtube:id/shorts_tab").isNotEmpty()) {
            return DetectionResult(true, CONTENT_TYPE_SHORTS)
        }
        
        // Strategy 2: Look for WebView with shorts URLs
        val webViewUrls = extractWebViewUrls(rootNode)
        for (url in webViewUrls) {
            if (url.contains("youtube.com/shorts") || url.contains("youtu.be/shorts")) {
                return DetectionResult(true, CONTENT_TYPE_SHORTS)
            }
        }
        
        // Strategy 3: Look for vertical video containers with specific characteristics of shorts
        val verticalScrollContainers = findVerticalScrollContainers(rootNode)
        for (container in verticalScrollContainers) {
            // Check for shorts-specific indicators
            val hasShortsIndicators = container.contentDescription?.toString()?.contains("shorts", ignoreCase = true) == true ||
                                     findNodesByText(container, "shorts", true).isNotEmpty() ||
                                     findNodesByText(container, "Shorts", false).isNotEmpty()
            
            // Count video player indicators in vertical scroll view (typical of shorts feed)
            var videoPlayerCount = 0
            videoPlayerCount = countVideoPlayers(container, videoPlayerCount)
            
            // Shorts feed typically has multiple video players in a vertical scroll container
            if (hasShortsIndicators || videoPlayerCount > 1) {
                return DetectionResult(true, CONTENT_TYPE_SHORTS)
            }
        }
        
        // Strategy 4: Look for specific shorts UI patterns
        // YouTube shorts have a distinctive like/dislike/comment/share button layout vertically aligned
        val hasLikeButton = findNodesByViewId(rootNode, "com.google.android.youtube:id/like_button").isNotEmpty()
        val hasDislikeButton = findNodesByViewId(rootNode, "com.google.android.youtube:id/dislike_button").isNotEmpty()
        val hasCommentButton = findNodesByViewId(rootNode, "com.google.android.youtube:id/comment_button").isNotEmpty()
        val hasShareButton = findNodesByViewId(rootNode, "com.google.android.youtube:id/share_button").isNotEmpty()
        
        // If we find the shorts-specific UI layout
        if (hasLikeButton && hasDislikeButton && hasCommentButton && hasShareButton) {
            // Only if they are in a vertical layout (characteristic of shorts)
            val shortsControls = findVerticalButtonLayout(rootNode)
            if (shortsControls) {
                return DetectionResult(true, CONTENT_TYPE_SHORTS)
            }
        }
        
        // Check for explore / recommended feed
        if (findNodesByViewId(rootNode, "com.google.android.youtube:id/explore_entry_point").isNotEmpty() ||
            findNodesByText(rootNode, "Explore", false).isNotEmpty() ||
            findNodesByViewId(rootNode, "com.google.android.youtube:id/results").isNotEmpty()) {
            return DetectionResult(true, CONTENT_TYPE_EXPLORE)
        }
        
        return DetectionResult(false)
    }
    
    /**
     * Count video player elements in a container
     */
    private fun countVideoPlayers(node: AccessibilityNodeInfo?, count: Int): Int {
        if (node == null) return count
        
        var currentCount = count
        try {
            // Check if this node has video player characteristics
            if (node.className?.contains("PlayerView") == true ||
                node.className?.contains("VideoView") == true ||
                node.contentDescription?.toString()?.contains("video player", ignoreCase = true) == true) {
                currentCount++
            }
            
            // Recursively check children
            for (i in 0 until node.childCount) {
                val child = node.getChild(i) ?: continue
                currentCount = countVideoPlayers(child, currentCount)
            }
        } catch (e: Exception) {
            // Ignore errors in traversal
        }
        
        return currentCount
    }
    
    /**
     * Check if like/dislike/comment/share buttons are arranged vertically (YouTube Shorts layout)
     */
    private fun findVerticalButtonLayout(rootNode: AccessibilityNodeInfo): Boolean {
        try {
            // Find like button as starting point
            val likeButtons = findNodesByViewId(rootNode, "com.google.android.youtube:id/like_button")
            if (likeButtons.isEmpty()) return false
            
            // From like button, check if other buttons are stacked vertically
            for (likeButton in likeButtons) {
                // Get parent to check button arrangement
                val parent = likeButton.parent ?: continue
                
                // Vertical layout will have buttons as siblings in same parent with similar X coordinates but different Y
                val siblingButtons = mutableListOf<AccessibilityNodeInfo>()
                for (i in 0 until parent.childCount) {
                    val child = parent.getChild(i) ?: continue
                    if (child.isClickable && 
                        (child.viewIdResourceName?.contains("button") == true ||
                         child.className?.contains("Button") == true)) {
                        siblingButtons.add(child)
                    }
                }
                
                // If we found multiple buttons (like, dislike, comment, share), check if they're stacked vertically
                if (siblingButtons.size >= 3) {
                    return true // Simplification - in full implementation would check bounds to confirm vertical alignment
                }
            }
        } catch (e: Exception) {
            // Ignore exceptions during layout detection
        }
        
        return false
    }
    
    /**
     * Detect Snapchat stories
     */
    private fun detectSnapchatContent(rootNode: AccessibilityNodeInfo): DetectionResult {
        // Check for stories
        if (findNodesByText(rootNode, "story", true).isNotEmpty() ||
            findNodesByViewId(rootNode, "com.snapchat.android:id/stories_container").isNotEmpty()) {
            return DetectionResult(true, CONTENT_TYPE_STORIES)
        }
        
        // Check for discover content
        if (findNodesByText(rootNode, "discover", true).isNotEmpty() ||
            findNodesByViewId(rootNode, "com.snapchat.android:id/discover_container").isNotEmpty()) {
            return DetectionResult(true, CONTENT_TYPE_EXPLORE)
        }
        
        return DetectionResult(false)
    }
    
    /**
     * Detect TikTok feed and suggested videos
     */
    private fun detectTikTokContent(rootNode: AccessibilityNodeInfo): DetectionResult {
        // Assume all TikTok content is distracting (main feed)
        if (findNodesByViewId(rootNode, "com.zhiliaoapp.musically:id/feed_video").isNotEmpty() ||
            findNodesByViewId(rootNode, "com.zhiliaoapp.musically:id/video_container").isNotEmpty()) {
            return DetectionResult(true, CONTENT_TYPE_SHORTS)
        }
        
        return DetectionResult(false)
    }
    
    /**
     * Detect Facebook reels and stories
     */
    private fun detectFacebookContent(rootNode: AccessibilityNodeInfo): DetectionResult {
        // Check for reels
        if (findNodesByText(rootNode, "reel", true).isNotEmpty() ||
            findNodesByViewId(rootNode, "com.facebook.katana:id/reels_container").isNotEmpty()) {
            return DetectionResult(true, CONTENT_TYPE_REELS)
        }
        
        // Check for stories
        if (findNodesByText(rootNode, "story", true).isNotEmpty() ||
            findNodesByViewId(rootNode, "com.facebook.katana:id/stories_container").isNotEmpty()) {
            return DetectionResult(true, CONTENT_TYPE_STORIES)
        }
        
        return DetectionResult(false)
    }
    
    /**
     * Detect Twitter/X fleets and explore content
     */
    private fun detectTwitterContent(rootNode: AccessibilityNodeInfo): DetectionResult {
        // Check for "For You" feed (more distracting than Following feed)
        if (findNodesByText(rootNode, "For You", false).isNotEmpty()) {
            return DetectionResult(true, CONTENT_TYPE_EXPLORE)
        }
        
        // Check for explore page
        if (findNodesByViewId(rootNode, "com.twitter.android:id/explore_container").isNotEmpty()) {
            return DetectionResult(true, CONTENT_TYPE_EXPLORE)
        }
        
        return DetectionResult(false)
    }
    
    /**
     * Find nodes by text content
     */
    private fun findNodesByText(node: AccessibilityNodeInfo, text: String, partial: Boolean): List<AccessibilityNodeInfo> {
        return try {
            if (partial) {
                node.findAccessibilityNodeInfosByText(text)
            } else {
                node.findAccessibilityNodeInfosByText(text).filter {
                    it.text?.toString() == text
                }
            }
        } catch (e: Exception) {
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
            emptyList()
        }
    }
    
    /**
     * Find vertical scrolling containers that are likely to contain short-form video content
     */
    private fun findVerticalScrollContainers(node: AccessibilityNodeInfo): List<AccessibilityNodeInfo> {
        val results = mutableListOf<AccessibilityNodeInfo>()
        
        try {
            // Find scrollable containers
            val scrollables = mutableListOf<AccessibilityNodeInfo>()
            findScrollableNodes(node, scrollables)
            
            // Filter for vertical containers that might be video feeds
            for (scrollable in scrollables) {
                // Check if vertical scrolling is enabled
                if (scrollable.isScrollable && 
                    (scrollable.className?.contains("RecyclerView") == true || 
                     scrollable.className?.contains("ListView") == true ||
                     scrollable.className?.contains("ScrollView") == true)) {
                    
                    // Additional checks for video content indicators
                    val hasVideoIndicators = checkForVideoIndicators(scrollable)
                    
                    if (hasVideoIndicators) {
                        results.add(scrollable)
                    }
                }
            }
        } catch (e: Exception) {
            // Log error but continue
        }
        
        return results
    }
    
    /**
     * Recursively find scrollable nodes in the accessibility hierarchy
     */
    private fun findScrollableNodes(node: AccessibilityNodeInfo?, results: MutableList<AccessibilityNodeInfo>) {
        if (node == null) return
        
        try {
            if (node.isScrollable) {
                results.add(node)
            }
            
            // Recursively check children
            for (i in 0 until node.childCount) {
                val child = node.getChild(i) ?: continue
                findScrollableNodes(child, results)
            }
        } catch (e: Exception) {
            // Ignore errors in traversal
        }
    }
    
    /**
     * Check if a node or its children have indicators of video content
     */
    private fun checkForVideoIndicators(node: AccessibilityNodeInfo?): Boolean {
        if (node == null) return false
        
        try {
            // Check if this node has video-related characteristics
            if (node.className?.contains("VideoView") == true ||
                node.className?.contains("PlayerView") == true ||
                node.className?.contains("ExoPlayer") == true ||
                node.contentDescription?.toString()?.contains("video", ignoreCase = true) == true) {
                return true
            }
            
            // Check for common video player controls
            val hasPlayButton = findNodesByText(node, "play", true).isNotEmpty()
            val hasPauseButton = findNodesByText(node, "pause", true).isNotEmpty()
            val hasVideoControls = findNodesByViewId(node, "exo_progress").isNotEmpty() ||
                                  findNodesByViewId(node, "player_control_view").isNotEmpty()
            
            if (hasPlayButton || hasPauseButton || hasVideoControls) {
                return true
            }
            
            // Recursively check children
            for (i in 0 until node.childCount) {
                val child = node.getChild(i) ?: continue
                if (checkForVideoIndicators(child)) {
                    return true
                }
            }
        } catch (e: Exception) {
            // Ignore errors in traversal
        }
        
        return false
    }
    
    /**
     * Extract URLs from WebView nodes
     * This helps detect YouTube shorts or Instagram reels from WebView content
     */
    private fun extractWebViewUrls(node: AccessibilityNodeInfo): List<String> {
        val urls = mutableListOf<String>()
        
        try {
            // Find WebView nodes
            val webViews = mutableListOf<AccessibilityNodeInfo>()
            findNodesByClassName(node, "android.webkit.WebView", webViews)
            
            for (webView in webViews) {
                // Try to extract URL from content description or text
                val contentDesc = webView.contentDescription?.toString()
                if (contentDesc != null && isValidUrl(contentDesc)) {
                    urls.add(contentDesc)
                }
                
                val text = webView.text?.toString()
                if (text != null && isValidUrl(text)) {
                    urls.add(text)
                }
            }
        } catch (e: Exception) {
            // Log error but continue
        }
        
        return urls
    }
    
    /**
     * Find nodes by class name
     */
    private fun findNodesByClassName(node: AccessibilityNodeInfo?, className: String, results: MutableList<AccessibilityNodeInfo>) {
        if (node == null) return
        
        try {
            if (node.className?.contains(className) == true) {
                results.add(node)
            }
            
            // Recursively check children
            for (i in 0 until node.childCount) {
                val child = node.getChild(i) ?: continue
                findNodesByClassName(child, className, results)
            }
        } catch (e: Exception) {
            // Ignore errors in traversal
        }
    }
    
    /**
     * Check if a string is a valid URL
     */
    private fun isValidUrl(url: String): Boolean {
        return try {
            android.net.Uri.parse(url).scheme?.startsWith("http") == true
        } catch (e: Exception) {
            false
        }
    }
}
