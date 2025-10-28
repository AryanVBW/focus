package com.aryanvbw.focus.data.temp

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [TempEvent::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(TempConverters::class)
abstract class TempDatabase : RoomDatabase() {
    abstract fun tempEventDao(): TempEventDao

    companion object {
        @Volatile
        private var INSTANCE: TempDatabase? = null

        fun getDatabase(context: Context): TempDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TempDatabase::class.java,
                    "temp_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
