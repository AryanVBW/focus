package com.aryanvbw.focus.receiver

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

/**
 * Device Admin Receiver for Focus app
 * Handles device admin events for screen locking functionality
 */
class FocusDeviceAdminReceiver : DeviceAdminReceiver() {
    
    private val TAG = "FocusDeviceAdminReceiver"
    
    override fun onEnabled(context: Context, intent: Intent) {
        super.onEnabled(context, intent)
        Log.d(TAG, "Device Admin enabled for Focus")
        Toast.makeText(context, "Focus: Device Admin enabled - Screen lock now available", Toast.LENGTH_LONG).show()
    }
    
    override fun onDisabled(context: Context, intent: Intent) {
        super.onDisabled(context, intent)
        Log.d(TAG, "Device Admin disabled for Focus")
        Toast.makeText(context, "Focus: Device Admin disabled - Screen lock unavailable", Toast.LENGTH_LONG).show()
    }
    
    override fun onDisableRequested(context: Context, intent: Intent): CharSequence {
        Log.d(TAG, "Device Admin disable requested for Focus")
        return "Disabling device admin will prevent Focus from locking your screen when blocking content"
    }
}
