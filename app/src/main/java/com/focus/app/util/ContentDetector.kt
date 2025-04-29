package com.focus.app.util

import android.view.accessibility.AccessibilityNodeInfo
import com.focus.app.util.AppSettings.Companion.CONTENT_TYPE_EXPLORE
import com.focus.app.util.AppSettings.Companion.CONTENT_TYPE_REELS
import com.focus.app.util.AppSettings.Companion.CONTENT_TYPE_SHORTS
import com.focus.app.util.AppSettings.Companion.CONTENT_TYPE_STORIES
import com.focus.app.util.AppSettings.Companion.PACKAGE_FACEBOOK
import com.focus.app.util.AppSettings.Companion.PACKAGE_INSTAGRAM
import com.focus.app.util.AppSettings.Companion.PACKAGE_SNAPCHAT
import com.focus.app.util.AppSettings.Companion.PACKAGE_TIKTOK
import com.focus.app.util.AppSettings.Companion.PACKAGE_TWITTER
import com.focus.app.util.AppSettings.Companion.PACKAGE_YOUTUBE

/**
 * Utility class to detect distracting content in various apps
 */
class ContentDetector(private val settings: AppSettings) {

    data class DetectionResult(val detected: Boolean, val contentType: String = "")

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
     */
    private fun detectInstagramContent(rootNode: AccessibilityNodeInfo): DetectionResult {
        // Check for reels
        if (findNodesByText(rootNode, "reel", true).isNotEmpty() ||
            findNodesByViewId(rootNode, "com.instagram.android:id/reels_tray").isNotEmpty() ||
            findNodesByViewId(rootNode, "com.instagram.android:id/reel_viewer_container").isNotEmpty()) {
            return DetectionResult(true, CONTENT_TYPE_REELS)
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
     * Detect YouTube shorts and suggested videos
     */
    private fun detectYouTubeContent(rootNode: AccessibilityNodeInfo): DetectionResult {
        // Check for shorts
        if (findNodesByText(rootNode, "shorts", true).isNotEmpty() ||
            findNodesByViewId(rootNode, "com.google.android.youtube:id/shorts_container").isNotEmpty()) {
            return DetectionResult(true, CONTENT_TYPE_SHORTS)
        }
        
        // Check for explore / recommended feed
        if (findNodesByViewId(rootNode, "com.google.android.youtube:id/explore_entry_point").isNotEmpty()) {
            return DetectionResult(true, CONTENT_TYPE_EXPLORE)
        }
        
        return DetectionResult(false)
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
}
