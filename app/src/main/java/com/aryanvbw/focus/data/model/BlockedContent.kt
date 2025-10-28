package com.aryanvbw.focus.data.model

data class BlockedContent(
    val id: Long,
    val type: String, // "app" or "website"
    val identifier: String, // package name for apps, domain for websites
    val displayName: String,
    val isEnabled: Boolean = true
)
