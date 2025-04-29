package com.focus.app.data.temp

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "temp_events")
data class TempEvent(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val description: String,
    val timestamp: Date = Date()
)
