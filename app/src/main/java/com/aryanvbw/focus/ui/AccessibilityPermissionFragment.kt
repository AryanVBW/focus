package com.aryanvbw.focus.ui

import android.accessibilityservice.AccessibilityServiceInfo
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.os.Build
import android.text.TextUtils
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.aryanvbw.focus.R
import com.aryanvbw.focus.databinding.FragmentPermissionAccessibilityBinding
import com.aryanvbw.focus.service.FocusAccessibilityService
import com.aryanvbw.focus.util.AppSettings
import com.aryanvbw.focus.util.AccessibilityDiagnostics

/**
 * Dedicated screen that prompts the user to grant Accessibility permission and
 * guides them to the system settings. Once permission is granted, the user is
 * automatically returned to the Home screen and the service is enabled.
 */
class AccessibilityPermissionFragment : Fragment() {

    // Use a runnable to periodically check permission status after user returns from settings
    private val permissionCheckRunnable = object : Runnable {
        override fun run() {
            if (isAdded && isAccessibilityServiceEnabled()) {
                activateAndRedirectHome()
            } else if (isAdded) {
                // After multiple attempts, run diagnostics to help troubleshoot
                if (troubleshootingAttempts >= 2) {
                    runDiagnosticsAndShowTroubleshooting()
                } else {
                    // Check again after a short delay
                    permissionHandler.postDelayed(this, 1500) // Check every 1.5 seconds
                }
            }
        }
    }
    
    private val permissionHandler = Handler(Looper.getMainLooper())
    private var isNavigating = false // Flag to prevent multiple navigation attempts

    private var _binding: FragmentPermissionAccessibilityBinding? = null
    private val binding get() = _binding!!

    private lateinit var appSettings: AppSettings

    // Troubleshooting variables
    private var troubleshootingAttempts = 0
    private val maxTroubleshootingAttempts = 3

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPermissionAccessibilityBinding.inflate(inflater, container, false)
        appSettings = AppSettings(requireContext())
        return binding.root
    }

    /**
     * Activate Focus mode and redirect to home screen
     */
    private fun activateAndRedirectHome() {
        if (isNavigating || !isAdded) return // Prevent multiple calls or calls when detached

        isNavigating = true // Set the flag
        try {
            // Stop checking for permissions
            permissionHandler.removeCallbacks(permissionCheckRunnable)
            
            // Enable the service
            appSettings.setServiceEnabled(true)
            
            // Turn on Focus mode to automatically block distractions
            appSettings.getPreferences().edit().putBoolean(AppSettings.KEY_FOCUS_MODE, true).apply()
            
            // Show a toast to let user know blocking is now active
            Toast.makeText(requireContext(), 
                "Focus activated! Distracting content like reels and shorts will now be blocked", 
                Toast.LENGTH_LONG).show()
            
            // Add a small delay before navigating to ensure the service starts properly
            Handler(Looper.getMainLooper()).postDelayed({
                // Make sure fragment is still attached before navigating
                if (isAdded) {
                    try {
                        findNavController().navigate(R.id.navigation_home)
                    } catch (e: Exception) {
                        isNavigating = false // Reset flag on error
                        Log.e("AccessibilityFragment", "Navigation error: ${e.message}")
                        // Fallback to activity if navigation fails
                        activity?.recreate()
                    } finally {
                       // Optional: Reset flag after successful navigation if needed, though usually not necessary as fragment is destroyed.
                       // isNavigating = false
                    }
                }
            }, 500) // Half-second delay to ensure service starts properly
        } catch (e: Exception) {
            isNavigating = false // Reset flag on error
            Log.e("AccessibilityFragment", "Error activating focus mode: ${e.message}")
            Toast.makeText(requireContext(), 
                "There was an error activating Focus mode. Please try again.", 
                Toast.LENGTH_LONG).show()
            
            // Try to recover by recreating the activity
            activity?.recreate()
        }
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonGrantAccess.setOnClickListener {
            // Handle grant access click with improved guidance
            handleGrantAccessClick()
        }
    }

    override fun onResume() {
        super.onResume()
        // If user granted the permission, enable service and go Home
        if (isAccessibilityServiceEnabled()) {
            activateAndRedirectHome()
        }
    }
    
    override fun onPause() {
        super.onPause()
        // Stop checking for permissions when fragment is paused
        permissionHandler.removeCallbacks(permissionCheckRunnable)
    }

    /**
     * Enhanced accessibility service detection with better error handling
     */
    private fun isAccessibilityServiceEnabled(): Boolean {
        try {
            val accessibilityManager = requireContext().getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
            
            // Method 1: Check enabled services list
            val enabledServices = accessibilityManager.getEnabledAccessibilityServiceList(
                AccessibilityServiceInfo.FEEDBACK_ALL_MASK
            )
            
            val isEnabledInList = enabledServices.any {
                it.resolveInfo.serviceInfo.packageName == requireContext().packageName &&
                        it.resolveInfo.serviceInfo.name == FocusAccessibilityService::class.java.name
            }
            
            // Method 2: Check system settings (more reliable on some devices)
            val enabledServicesSetting = Settings.Secure.getString(
                requireContext().contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            )
            
            val expectedService = ComponentName(
                requireContext().packageName,
                FocusAccessibilityService::class.java.name
            ).flattenToString()
            
            val isEnabledInSettings = enabledServicesSetting?.contains(expectedService) == true
            
            Log.d("AccessibilityFragment", "Service enabled - List: $isEnabledInList, Settings: $isEnabledInSettings")
            
            return isEnabledInList || isEnabledInSettings
            
        } catch (e: Exception) {
            Log.e("AccessibilityFragment", "Error checking accessibility service status: ${e.message}")
            return false
        }
    }
    
    /**
     * Provides device-specific instructions for enabling accessibility service
     */
    private fun getDeviceSpecificInstructions(): String {
        val manufacturer = Build.MANUFACTURER.lowercase()
        return when {
            manufacturer.contains("samsung") -> {
                "Samsung Device Instructions:\n" +
                "1. Go to Settings > Accessibility\n" +
                "2. Find 'Focus Blocker' under Downloaded apps\n" +
                "3. Toggle it ON\n" +
                "4. Tap 'Allow' when prompted\n" +
                "5. Also check Settings > Apps > Focus > Special access"
            }
            manufacturer.contains("xiaomi") || manufacturer.contains("redmi") -> {
                "Xiaomi/MIUI Instructions:\n" +
                "1. Go to Settings > Accessibility\n" +
                "2. Find 'Focus Blocker'\n" +
                "3. Enable the service\n" +
                "4. Go to Settings > Apps > Manage apps > Focus\n" +
                "5. Enable 'Autostart' and 'Display pop-up windows while running in background'"
            }
            manufacturer.contains("oneplus") -> {
                "OnePlus Instructions:\n" +
                "1. Go to Settings > Accessibility\n" +
                "2. Enable 'Focus Blocker'\n" +
                "3. Go to Settings > Apps > Focus > App permissions\n" +
                "4. Enable 'Display over other apps'\n" +
                "5. Check Battery optimization settings"
            }
            manufacturer.contains("huawei") -> {
                "Huawei Instructions:\n" +
                "1. Go to Settings > Accessibility\n" +
                "2. Enable 'Focus Blocker'\n" +
                "3. Go to Settings > Apps > Focus\n" +
                "4. Enable 'Autostart' in App launch\n" +
                "5. Add to Protected apps in Battery settings"
            }
            else -> {
                "General Instructions:\n" +
                "1. Go to Settings > Accessibility\n" +
                "2. Look for 'Focus Blocker' or 'Focus'\n" +
                "3. Toggle the service ON\n" +
                "4. Grant any additional permissions requested\n" +
                "5. If issues persist, check Battery optimization settings"
            }
        }
    }
    
    /**
     * Shows troubleshooting dialog with device-specific help
     */
    private fun showTroubleshootingDialog() {
        if (!isAdded) return
        
        val instructions = getDeviceSpecificInstructions()
        
        val dialogMessage = """
            Having trouble enabling accessibility service?
            
            $instructions
            
            Common issues:
            • Service not visible: Try restarting your device
            • Can't enable: Check if app has all permissions
            • Keeps turning off: Disable battery optimization for Focus
            
            Still having issues? Contact support with your device model.
        """.trimIndent()
        
        Toast.makeText(requireContext(), dialogMessage, Toast.LENGTH_LONG).show()
    }
    
    /**
     * Enhanced button click handler with better user guidance
     */
    private fun handleGrantAccessClick() {
        try {
            // Show device-specific instructions first
            if (troubleshootingAttempts >= maxTroubleshootingAttempts) {
                showTroubleshootingDialog()
                return
            }
            
            // Increment troubleshooting attempts
            troubleshootingAttempts++
            
            // Show a more helpful toast
            Toast.makeText(
                requireContext(),
                "Opening Accessibility Settings. Look for 'Focus Blocker' and toggle it ON.",
                Toast.LENGTH_LONG
            ).show()
            
            // Open accessibility settings
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            startActivity(intent)
            
            // Start checking for permission after a longer delay to give user time
            permissionHandler.postDelayed(permissionCheckRunnable, 2000) // 2 seconds delay
            
        } catch (e: Exception) {
            Log.e("AccessibilityFragment", "Error opening accessibility settings: ${e.message}")
            Toast.makeText(
                requireContext(),
                "Unable to open settings. Please go to Settings > Accessibility manually.",
                Toast.LENGTH_LONG
            ).show()
        }
    }
    
    /**
     * Runs diagnostics and shows detailed troubleshooting information
     */
    private fun runDiagnosticsAndShowTroubleshooting() {
        if (!isAdded) return
        
        Log.d("AccessibilityFragment", "Running accessibility diagnostics...")
        
        val diagnosticResult = AccessibilityDiagnostics.diagnoseAccessibilityService(requireContext())
        
        // Log detailed diagnostics
        Log.d("AccessibilityFragment", diagnosticResult.toString())
        
        if (diagnosticResult.isServiceEnabled()) {
            // Service is working, proceed normally
            activateAndRedirectHome()
        } else {
            // Show troubleshooting steps
            val troubleshootingSteps = AccessibilityDiagnostics.getTroubleshootingSteps(diagnosticResult)
            val stepsText = troubleshootingSteps.joinToString("\n• ", "• ")
            
            val message = """
                Accessibility Service Setup Required
                
                $stepsText
                
                Device: ${Build.MANUFACTURER} ${Build.MODEL}
                Android: ${Build.VERSION.RELEASE}
                
                If you continue having issues, please contact support with the above information.
            """.trimIndent()
            
            // Show in a dialog or toast based on message length
            if (message.length > 200) {
                showLongTroubleshootingMessage(message)
            } else {
                Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
            }
        }
    }
    
    /**
     * Shows long troubleshooting message in a scrollable dialog
     */
    private fun showLongTroubleshootingMessage(message: String) {
        if (!isAdded) return
        
        // For now, use a long toast. In a real implementation, you might want a dialog
        Toast.makeText(requireContext(), 
            "Check the app logs for detailed troubleshooting information", 
            Toast.LENGTH_LONG).show()
        
        // Log the full message for debugging
        Log.i("AccessibilityFragment", "Troubleshooting guide:\n$message")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        permissionHandler.removeCallbacks(permissionCheckRunnable) // Ensure cleanup
        _binding = null
    }
}
