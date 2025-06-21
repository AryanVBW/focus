package com.aryanvbw.focus.util

import android.accessibilityservice.AccessibilityService
import android.app.ActivityManager
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import com.aryanvbw.focus.receiver.FocusDeviceAdminReceiver

/**
 * Utility class for handling different blocking actions
 */
class BlockingActionHandler(
    private val context: Context,
    private val accessibilityService: AccessibilityService? = null
) {
    
    private val TAG = "BlockingActionHandler"
    
    companion object {
        const val ACTION_CLOSE_PLAYER = "close_player"
        const val ACTION_CLOSE_APP = "close_app"
        const val ACTION_LOCK_SCREEN = "lock_screen"
    }
    
    /**
     * Execute the appropriate blocking action based on user settings
     */
    fun executeBlockingAction(packageName: String, action: String) {
        when (action) {
            ACTION_CLOSE_PLAYER -> closePlayer()
            ACTION_CLOSE_APP -> closeApp(packageName)
            ACTION_LOCK_SCREEN -> lockScreen()
            else -> {
                Log.w(TAG, "Unknown blocking action: $action, defaulting to close player")
                closePlayer()
            }
        }
    }
    
    /**
     * Close player/return to previous screen (default behavior)
     */
    private fun closePlayer() {
        try {
            accessibilityService?.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK)
            Log.d(TAG, "Executed close player action (back navigation)")
        } catch (e: Exception) {
            Log.e(TAG, "Error executing close player action: ${e.message}")
        }
    }
    
    /**
     * Force stop the blocked app
     */
    private fun closeApp(packageName: String) {
        try {
            val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            
            // First try to bring user to home screen
            val homeIntent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_HOME)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(homeIntent)
            
            // Then attempt to kill the app
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                // For API 21+, use killBackgroundProcesses
                activityManager.killBackgroundProcesses(packageName)
                Log.d(TAG, "Executed close app action for package: $packageName")
            } else {
                // For older versions, use the deprecated method
                @Suppress("DEPRECATION")
                activityManager.restartPackage(packageName)
                Log.d(TAG, "Executed close app action (restart) for package: $packageName")
            }
            
            // Show user feedback
            try {
                Toast.makeText(context, "Focus: Closed blocked app", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.e(TAG, "Error showing toast: ${e.message}")
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error executing close app action: ${e.message}")
            // Fallback to close player action
            closePlayer()
        }
    }
    
    /**
     * Lock the device screen
     */
    private fun lockScreen() {
        try {
            val devicePolicyManager = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
            val componentName = ComponentName(context, FocusDeviceAdminReceiver::class.java)
            
            if (devicePolicyManager.isAdminActive(componentName)) {
                devicePolicyManager.lockNow()
                Log.d(TAG, "Executed lock screen action")
            } else {
                Log.w(TAG, "Device admin not active, cannot lock screen. Falling back to close player.")
                // Show user a message about needing device admin
                try {
                    Toast.makeText(context, 
                        "Focus: Enable Device Admin in settings to use screen lock", 
                        Toast.LENGTH_LONG).show()
                } catch (e: Exception) {
                    Log.e(TAG, "Error showing toast: ${e.message}")
                }
                // Fallback to close player action
                closePlayer()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error executing lock screen action: ${e.message}")
            // Fallback to close player action
            closePlayer()
        }
    }
    
    /**
     * Check if device admin is enabled for lock screen functionality
     */
    fun isDeviceAdminEnabled(): Boolean {
        return try {
            val devicePolicyManager = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
            val componentName = ComponentName(context, FocusDeviceAdminReceiver::class.java)
            devicePolicyManager.isAdminActive(componentName)
        } catch (e: Exception) {
            Log.e(TAG, "Error checking device admin status: ${e.message}")
            false
        }
    }
    
    /**
     * Request device admin permission
     */
    fun requestDeviceAdminPermission() {
        try {
            val devicePolicyManager = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
            val componentName = ComponentName(context, FocusDeviceAdminReceiver::class.java)
            
            val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
                putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName)
                putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, 
                    "Focus needs device admin permission to lock screen when blocking content")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Error requesting device admin permission: ${e.message}")
        }
    }
}
