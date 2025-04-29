package com.focus.app

import android.app.Application
import com.focus.app.data.temp.TempDatabase

/**
 * Main application class for Focus app
 */
class FocusApplication : Application() {

    val database: TempDatabase by lazy { TempDatabase.getDatabase(this) }

    override fun onCreate() {
        super.onCreate()
        // Initialize any application-wide components here
        
        // Preload database instance
        // AppDatabase.getInstance(this)
    }
}
