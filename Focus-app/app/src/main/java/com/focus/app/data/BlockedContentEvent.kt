package com.aryanvbw.focus.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "blocked_content_events")
data class BlockedContentEvent(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val appPackage: String,
    val contentType: String,
    val timestamp: Date = Date()
)
