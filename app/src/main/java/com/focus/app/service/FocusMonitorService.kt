package com.focus.app.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.focus.app.util.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * A foreground service to keep the accessibility service running
 * and monitor app usage in the background
 */
class FocusMonitorService : Service() {
    
    private val TAG = "FocusMonitorService"
    private lateinit var notificationHelper: NotificationHelper
    private val serviceScope = CoroutineScope(Dispatchers.Default)
    private var monitoringJob: Job? = null
    
    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Monitor service created")
        notificationHelper = NotificationHelper(this)
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "Monitor service started")
        
        // Start foreground service with notification
        val notification = notificationHelper.createServiceNotification()
        startForeground(NotificationHelper.SERVICE_NOTIFICATION_ID, notification)
        
        // Start monitoring task
        startMonitoring()
        
        return START_STICKY
    }
    
    private fun startMonitoring() {
        monitoringJob = serviceScope.launch {
            while(true) {
                // Periodic check to ensure service is running
                // This could also collect usage statistics
                Log.d(TAG, "Focus monitoring service is active")
                delay(60000) // Check every minute
            }
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Monitor service destroyed")
        monitoringJob?.cancel()
    }
    
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
