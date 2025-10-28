package com.aryanvbw.focus

import android.app.Application
import android.util.Log
import com.aryanvbw.focus.util.AppSettings

/**
 * Main application class for Focus app
 */
class FocusApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        
        // Initialize app settings and run migrations
        val appSettings = AppSettings(this)
        appSettings.ensurePlayerCloseDefault()
        
        Log.i("FocusApplication", "Application initialized with proper blocking defaults")
    }
}
