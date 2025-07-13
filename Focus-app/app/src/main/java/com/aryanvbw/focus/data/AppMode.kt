package com.aryanvbw.focus.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Entity to store app mode configuration (Normal/Focus)
 */
@Entity(tableName = "app_modes")
data class AppMode(
    @PrimaryKey
    val id: String = "current_mode",
    val mode: String, // "normal" or "focus"
    val isActive: Boolean = true,
    val scheduledStartTime: String? = null, // HH:mm format
    val scheduledEndTime: String? = null, // HH:mm format
    val activeDays: String = "1,2,3,4,5,6,7", // Days of week (1=Monday, 7=Sunday)
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)
