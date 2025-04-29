package com.focus.app.ui

import android.accessibilityservice.AccessibilityServiceInfo
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
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.focus.app.R
import com.focus.app.databinding.FragmentPermissionAccessibilityBinding
import com.focus.app.service.FocusAccessibilityService
import com.focus.app.util.AppSettings

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
                // Check again after a short delay
                permissionHandler.postDelayed(this, 1000) // Check every second
            }
        }
    }
    
    private val permissionHandler = Handler(Looper.getMainLooper())
    private var isNavigating = false // Flag to prevent multiple navigation attempts

    private var _binding: FragmentPermissionAccessibilityBinding? = null
    private val binding get() = _binding!!

    private lateinit var appSettings: AppSettings

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
                "Focus activated! YouTube and Instagram reels will now be blocked", 
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
            // Open system Accessibility settings
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            
            // Start periodic permission check
            permissionHandler.postDelayed(permissionCheckRunnable, 500) // Start checking after 500ms
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

    private fun isAccessibilityServiceEnabled(): Boolean {
        val accessibilityManager = requireContext().getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        val enabledServices = accessibilityManager.getEnabledAccessibilityServiceList(
            AccessibilityServiceInfo.FEEDBACK_ALL_MASK
        )
        return enabledServices.any {
            it.resolveInfo.serviceInfo.packageName == requireContext().packageName &&
                    it.resolveInfo.serviceInfo.name == FocusAccessibilityService::class.java.name
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        permissionHandler.removeCallbacks(permissionCheckRunnable) // Ensure cleanup
        _binding = null
    }
}
