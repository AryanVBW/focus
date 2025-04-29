package com.focus.app.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Toast
import com.focus.app.data.AppDatabase
import com.focus.app.data.BlockedContentEvent
import com.focus.app.data.BlockedContentEventDao
import com.focus.app.util.AppSettings
import com.focus.app.util.ContentDetector
import com.focus.app.util.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date

/**
 * Accessibility service to detect and block distracting content
 * This is the core of the Focus app's functionality
 */
class FocusAccessibilityService : AccessibilityService() {

    private val TAG = "FocusAccessService"
    private lateinit var settings: AppSettings
    private lateinit var contentDetector: ContentDetector
    private lateinit var notificationHelper: NotificationHelper
    private lateinit var blockedContentEventDao: BlockedContentEventDao
    private val serviceScope = CoroutineScope(Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Service created")
        settings = AppSettings(this)
        contentDetector = ContentDetector(settings)
        notificationHelper = NotificationHelper(this)
        
        // Initialize database
        val database = AppDatabase.getInstance(this)
        blockedContentEventDao = database.blockedContentEventDao()
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d(TAG, "Service connected")
        notificationHelper.showServiceRunningNotification()
        
        // Start the foreground monitoring service
        val intent = Intent(this, FocusMonitorService::class.java)
        startService(intent)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        // Only process if service is enabled
        if (!settings.isServiceEnabled()) {
            return
        }

        val packageName = event.packageName?.toString() ?: return
        
        // Check if this app is configured to be monitored
        if (!settings.isAppMonitored(packageName)) {
            return
        }

        // Process different event types
        when (event.eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED, 
            AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED,
            AccessibilityEvent.TYPE_VIEW_SCROLLED -> {
                processContentForBlocking(event, packageName)
            }
        }
    }

    private fun processContentForBlocking(event: AccessibilityEvent, packageName: String) {
        val rootNode = rootInActiveWindow ?: return
        
        try {
            // Detect reels/shorts/stories based on app-specific patterns
            val detectionResult = contentDetector.detectDistractingContent(rootNode, packageName)
            
            if (detectionResult.detected) {
                handleDistractingContent(rootNode, packageName, detectionResult.contentType)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error processing content: ${e.message}")
        } finally {
            rootNode.recycle()
        }
    }

    private fun handleDistractingContent(rootNode: AccessibilityNodeInfo, packageName: String, contentType: String) {
        // Block the content based on settings
        if (settings.shouldBlockContentType(contentType)) {
            // Navigate back or perform other actions to block content
            performGlobalAction(GLOBAL_ACTION_BACK)
            
            // Show notification
            notificationHelper.showContentBlockedNotification(packageName, contentType)
            
            // Log the event
            logBlockedEvent(packageName, contentType)
            
            // Show toast (optional)
            Toast.makeText(this, "Blocked distracting content: $contentType", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun logBlockedEvent(packageName: String, contentType: String) {
        serviceScope.launch {
            val event = BlockedContentEvent(
                timestamp = Date().time,
                appPackage = packageName,
                contentType = contentType
            )
            blockedContentEventDao.insert(event)
        }
    }

    override fun onInterrupt() {
        Log.d(TAG, "Service interrupted")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Service destroyed")
        
        // Stop the foreground service
        val intent = Intent(this, FocusMonitorService::class.java)
        stopService(intent)
    }
}
