package com.aryanvbw.focus.data.model

data class BlockingStats(
    val blockedAppsCount: Int,
    val blockedWebsitesCount: Int,
    val focusTimeMinutes: Int,
    val blocksToday: Int
)
