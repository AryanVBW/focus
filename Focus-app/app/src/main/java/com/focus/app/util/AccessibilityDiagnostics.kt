package com.aryanvbw.focus.util

import android.accessibilityservice.AccessibilityServiceInfo
import android.content.ComponentName
import android.content.Context
import android.provider.Settings
import android.view.accessibility.AccessibilityManager
import android.util.Log
import com.aryanvbw.focus.service.FocusAccessibilityService

/**
 * Utility class to help diagnose and troubleshoot accessibility service issues
 */
object AccessibilityDiagnostics {
    
    private const val TAG = "AccessibilityDiagnostics"
    
    /**
     * Performs a comprehensive check of accessibility service status
     */
    fun diagnoseAccessibilityService(context: Context): DiagnosticResult {
        val result = DiagnosticResult()
        
        try {
            // Check if accessibility manager is available
            val accessibilityManager = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as? AccessibilityManager
            if (accessibilityManager == null) {
                result.addError("AccessibilityManager not available")
                return result
            }
            
            // Check if accessibility is enabled at all
            if (!accessibilityManager.isEnabled) {
                result.addWarning("Accessibility services are disabled on this device")
            }
            
            // Check our specific service
            checkServiceInList(context, accessibilityManager, result)
            checkServiceInSettings(context, result)
            checkServicePermissions(context, result)
            
        } catch (e: Exception) {
            result.addError("Error during diagnostics: ${e.message}")
            Log.e(TAG, "Diagnostic error", e)
        }
        
        return result
    }
    
    private fun checkServiceInList(
        context: Context, 
        accessibilityManager: AccessibilityManager, 
        result: DiagnosticResult
    ) {
        try {
            val enabledServices = accessibilityManager.getEnabledAccessibilityServiceList(
                AccessibilityServiceInfo.FEEDBACK_ALL_MASK
            )
            
            val ourService = enabledServices.find {
                it.resolveInfo.serviceInfo.packageName == context.packageName &&
                        it.resolveInfo.serviceInfo.name == FocusAccessibilityService::class.java.name
            }
            
            if (ourService != null) {
                result.addSuccess("Service found in enabled services list")
                result.serviceInfo = ourService
            } else {
                result.addError("Service NOT found in enabled services list")
            }
            
        } catch (e: Exception) {
            result.addError("Error checking enabled services: ${e.message}")
        }
    }
    
    private fun checkServiceInSettings(context: Context, result: DiagnosticResult) {
        try {
            val enabledServicesSetting = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            )
            
            val expectedService = ComponentName(
                context.packageName,
                FocusAccessibilityService::class.java.name
            ).flattenToString()
            
            if (enabledServicesSetting?.contains(expectedService) == true) {
                result.addSuccess("Service found in system settings")
            } else {
                result.addError("Service NOT found in system settings")
                result.addInfo("Settings value: $enabledServicesSetting")
                result.addInfo("Expected service: $expectedService")
            }
            
        } catch (e: Exception) {
            result.addError("Error checking system settings: ${e.message}")
        }
    }
    
    private fun checkServicePermissions(context: Context, result: DiagnosticResult) {
        try {
            // Check if app has the required permissions
            val packageManager = context.packageManager
            val packageInfo = packageManager.getPackageInfo(context.packageName, 0)
            
            result.addInfo("App version: ${packageInfo.versionName} (${packageInfo.versionCode})")
            result.addInfo("Target SDK: ${packageInfo.applicationInfo.targetSdkVersion}")
            
        } catch (e: Exception) {
            result.addWarning("Error checking app permissions: ${e.message}")
        }
    }
    
    /**
     * Gets a user-friendly troubleshooting guide based on diagnostic results
     */
    fun getTroubleshootingSteps(result: DiagnosticResult): List<String> {
        val steps = mutableListOf<String>()
        
        if (result.hasErrors()) {
            steps.add("Go to Settings > Accessibility")
            steps.add("Look for 'Focus Blocker' or 'Focus' in the list")
            steps.add("Toggle the service ON")
            steps.add("Tap 'Allow' when prompted for permissions")
        }
        
        if (result.hasWarnings()) {
            steps.add("Restart your device if the service doesn't appear")
            steps.add("Check Battery optimization settings for Focus app")
            steps.add("Ensure Focus app is not being killed by device optimization")
        }
        
        steps.add("Test the service by opening Instagram and navigating to Reels")
        
        return steps
    }
    
    data class DiagnosticResult(
        private val errors: MutableList<String> = mutableListOf(),
        private val warnings: MutableList<String> = mutableListOf(),
        private val successes: MutableList<String> = mutableListOf(),
        private val info: MutableList<String> = mutableListOf(),
        var serviceInfo: AccessibilityServiceInfo? = null
    ) {
        fun addError(message: String) = errors.add(message)
        fun addWarning(message: String) = warnings.add(message)
        fun addSuccess(message: String) = successes.add(message)
        fun addInfo(message: String) = info.add(message)
        
        fun hasErrors() = errors.isNotEmpty()
        fun hasWarnings() = warnings.isNotEmpty()
        fun isServiceEnabled() = successes.any { it.contains("Service found") }
        
        fun getErrors() = errors.toList()
        fun getWarnings() = warnings.toList()
        fun getSuccesses() = successes.toList()
        fun getInfo() = info.toList()
        
        override fun toString(): String {
            val sb = StringBuilder()
            sb.appendLine("=== Accessibility Service Diagnostics ===")
            
            if (successes.isNotEmpty()) {
                sb.appendLine("\n✅ Successes:")
                successes.forEach { sb.appendLine("  • $it") }
            }
            
            if (warnings.isNotEmpty()) {
                sb.appendLine("\n⚠️ Warnings:")
                warnings.forEach { sb.appendLine("  • $it") }
            }
            
            if (errors.isNotEmpty()) {
                sb.appendLine("\n❌ Errors:")
                errors.forEach { sb.appendLine("  • $it") }
            }
            
            if (info.isNotEmpty()) {
                sb.appendLine("\nℹ️ Info:")
                info.forEach { sb.appendLine("  • $it") }
            }
            
            return sb.toString()
        }
    }
}
