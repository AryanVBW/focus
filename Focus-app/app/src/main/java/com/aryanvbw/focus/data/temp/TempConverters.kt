package com.aryanvbw.focus.data.temp

import androidx.room.TypeConverter
import java.util.Date

class TempConverters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}
