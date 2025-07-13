package com.aryanvbw.focus.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.aryanvbw.focus.R
import com.aryanvbw.focus.ui.MainActivity

/**
 * Helper class to manage notifications for the Focus app
 */
class NotificationHelper(private val context: Context) {

    private val notificationManager: NotificationManager by lazy {
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    companion object {
        const val CHANNEL_ID = "focus_service_channel"
        const val SERVICE_NOTIFICATION_ID = 1001
        const val CONTENT_BLOCKED_NOTIFICATION_ID = 1002
        const val TEMPORARY_NOTIFICATION_ID = 1003
    }

    init {
        createNotificationChannel()
    }

    /**
     * Check if notification permission is granted (Android 13+)
     */
    private fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true // No permission required for older versions
        }
    }

    /**
     * Create the notification channel for Android O and above
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                context.getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = context.getString(R.string.notification_channel_desc)
                setShowBadge(false)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Create a notification for the foreground service
     */
    fun createServiceNotification(): Notification {
        val pendingIntent = createMainActivityPendingIntent()

        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_shield)
            .setContentTitle(context.getString(R.string.app_name))
            .setContentText(context.getString(R.string.notification_service_running))
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setOngoing(true)
            .build()
    }

    /**
     * Show the service running notification
     */
    fun showServiceRunningNotification() {
        if (hasNotificationPermission()) {
            val notification = createServiceNotification()
            notificationManager.notify(SERVICE_NOTIFICATION_ID, notification)
        }
    }

    /**
     * Show notification when content is blocked
     */
    fun showContentBlockedNotification(packageName: String, contentType: String) {
        val appName = getAppNameFromPackage(packageName)
        val contentText = context.getString(
            R.string.notification_content_blocked, 
            "$appName $contentType"
        )
        
        val pendingIntent = createMainActivityPendingIntent()

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_block)
            .setContentTitle(context.getString(R.string.app_name))
            .setContentText(contentText)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_STATUS)
            .setAutoCancel(true)
            .build()

        if (hasNotificationPermission()) {
            notificationManager.notify(CONTENT_BLOCKED_NOTIFICATION_ID, notification)
        }
    }
    
    /**
     * Show a temporary notification with custom title and message
     * @param title The notification title
     * @param message The notification message
     * @param autoCancel Whether the notification should auto-cancel when tapped
     */
    fun showTemporaryNotification(title: String, message: String, autoCancel: Boolean = true) {
        val pendingIntent = createMainActivityPendingIntent()

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_shield)
            .setContentTitle(title)
            .setContentText(message)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_STATUS)
            .setAutoCancel(autoCancel)
            .setTimeoutAfter(3000) // Auto-dismiss after 3 seconds
            .build()

        if (hasNotificationPermission()) {
            notificationManager.notify(TEMPORARY_NOTIFICATION_ID, notification)
        }
    }
    
    /**
     * Show a notification with a specific ID
     */
    fun showNotification(notification: android.app.Notification, notificationId: Int) {
        if (hasNotificationPermission()) {
            notificationManager.notify(notificationId, notification)
        }
    }
    
    /**
     * Create a limit warning notification
     */
    fun createLimitWarningNotification(
        appName: String, 
        usageMinutes: Int, 
        limitMinutes: Int, 
        remainingMinutes: Int
    ): android.app.Notification {
        val pendingIntent = createMainActivityPendingIntent()
        
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_schedule)
            .setContentTitle("$appName - Approaching Limit")
            .setContentText("Used ${usageMinutes}/${limitMinutes} minutes. $remainingMinutes minutes remaining.")
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_STATUS)
            .setAutoCancel(true)
            .build()
    }
    
    /**
     * Create a limit exceeded notification
     */
    fun createLimitExceededNotification(
        appName: String, 
        usageMinutes: Int, 
        limitMinutes: Int, 
        exceededMinutes: Int
    ): android.app.Notification {
        val pendingIntent = createMainActivityPendingIntent()
        
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_block)
            .setContentTitle("$appName - Limit Exceeded")
            .setContentText("Used ${usageMinutes}/${limitMinutes} minutes. Exceeded by $exceededMinutes minutes.")
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_STATUS)
            .setAutoCancel(true)
            .build()
    }
    
    /**
     * Create a service notification for foreground services
     */
    fun createServiceNotification(title: String, content: String, notificationId: Int): android.app.Notification {
        val pendingIntent = createMainActivityPendingIntent()
        
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_shield)
            .setContentTitle(title)
            .setContentText(content)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setOngoing(true)
            .setAutoCancel(false)
            .build()
    }

    /**
     * Create a pending intent for the main activity
     */
    private fun createMainActivityPendingIntent(): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        return PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    /**
     * Get a user-friendly app name from a package name
     */
    private fun getAppNameFromPackage(packageName: String): String {
        return when (packageName) {
            AppSettings.PACKAGE_INSTAGRAM -> "Instagram"
            AppSettings.PACKAGE_YOUTUBE -> "YouTube"
            AppSettings.PACKAGE_SNAPCHAT -> "Snapchat"
            AppSettings.PACKAGE_TIKTOK -> "TikTok"
            AppSettings.PACKAGE_FACEBOOK -> "Facebook"
            AppSettings.PACKAGE_TWITTER -> "Twitter"
            else -> packageName
        }
    }
}
