package com.aryanvbw.focus.detection.model

/**
 * Enum representing different scroll directions for video content
 */
enum class ScrollDirection {
    /**
     * No scrolling detected
     */
    NONE,
    
    /**
     * Vertical scrolling (typical for reels, shorts, TikTok)
     */
    VERTICAL,
    
    /**
     * Horizontal scrolling (typical for stories)
     */
    HORIZONTAL,
    
    /**
     * Both vertical and horizontal scrolling
     */
    BOTH;
    
    /**
     * Check if this direction includes vertical scrolling
     */
    fun hasVertical(): Boolean = this == VERTICAL || this == BOTH
    
    /**
     * Check if this direction includes horizontal scrolling
     */
    fun hasHorizontal(): Boolean = this == HORIZONTAL || this == BOTH
    
    /**
     * Check if any scrolling is present
     */
    fun hasScrolling(): Boolean = this != NONE
    
    /**
     * Get a human-readable description
     */
    fun getDescription(): String = when (this) {
        NONE -> "No scrolling"
        VERTICAL -> "Vertical scrolling"
        HORIZONTAL -> "Horizontal scrolling"
        BOTH -> "Multi-directional scrolling"
    }
}