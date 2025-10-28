package com.aryanvbw.focus.data.temp

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import java.util.Date

@Dao
interface TempEventDao {
    @Insert
    suspend fun insert(event: TempEvent)

    @Query("SELECT * FROM temp_events ORDER BY timestamp DESC")
    fun getAllEvents(): LiveData<List<TempEvent>>

    @Query("SELECT COUNT(*) FROM temp_events WHERE timestamp >= :startTime")
    suspend fun getEventCountSince(startTime: Date): Int
}
