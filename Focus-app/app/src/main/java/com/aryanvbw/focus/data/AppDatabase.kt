package com.aryanvbw.focus.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        BlockedContentEvent::class,
        AppUsageEvent::class,
        AppLimit::class,
        NotificationEvent::class,
        AppMode::class
    ],
    version = 2,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun blockedContentEventDao(): BlockedContentEventDao
    abstract fun appUsageEventDao(): AppUsageEventDao
    abstract fun appLimitDao(): AppLimitDao
    abstract fun notificationEventDao(): NotificationEventDao
    abstract fun appModeDao(): AppModeDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "focus_database"
                )
                .addMigrations(MIGRATION_1_2)
                .build()
                INSTANCE = instance
                instance
            }
        }
        
        private val MIGRATION_1_2 = object : androidx.room.migration.Migration(1, 2) {
            override fun migrate(database: androidx.sqlite.db.SupportSQLiteDatabase) {
                // Create app_usage_events table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS app_usage_events (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        packageName TEXT NOT NULL,
                        appName TEXT NOT NULL,
                        category TEXT NOT NULL,
                        startTime INTEGER NOT NULL,
                        endTime INTEGER,
                        usageDurationMs INTEGER NOT NULL,
                        timestamp INTEGER NOT NULL
                    )
                """)
                
                // Create app_limits table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS app_limits (
                        packageName TEXT PRIMARY KEY NOT NULL,
                        appName TEXT NOT NULL,
                        dailyLimitMinutes INTEGER NOT NULL,
                        isEnabled INTEGER NOT NULL DEFAULT 1,
                        createdAt INTEGER NOT NULL,
                        updatedAt INTEGER NOT NULL
                    )
                """)
                
                // Create notification_events table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS notification_events (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        packageName TEXT NOT NULL,
                        appName TEXT NOT NULL,
                        title TEXT NOT NULL,
                        content TEXT NOT NULL,
                        isBlocked INTEGER NOT NULL DEFAULT 0,
                        priority INTEGER NOT NULL,
                        timestamp INTEGER NOT NULL
                    )
                """)
                
                // Create app_modes table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS app_modes (
                        id TEXT PRIMARY KEY NOT NULL,
                        mode TEXT NOT NULL,
                        isActive INTEGER NOT NULL DEFAULT 1,
                        scheduledStartTime TEXT,
                        scheduledEndTime TEXT,
                        activeDays TEXT NOT NULL DEFAULT '1,2,3,4,5,6,7',
                        createdAt INTEGER NOT NULL,
                        updatedAt INTEGER NOT NULL
                    )
                """)
                
                // Insert default normal mode
                database.execSQL("""
                    INSERT OR REPLACE INTO app_modes (id, mode, isActive, createdAt, updatedAt)
                    VALUES ('current_mode', 'normal', 1, ${System.currentTimeMillis()}, ${System.currentTimeMillis()})
                """)
            }
        }
    }
}
