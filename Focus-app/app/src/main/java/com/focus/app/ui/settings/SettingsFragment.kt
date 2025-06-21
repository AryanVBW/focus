package com.aryanvbw.focus.ui.settings

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.aryanvbw.focus.R
import com.aryanvbw.focus.util.AppSettings

class SettingsFragment : PreferenceFragmentCompat() {

    private lateinit var appSettings: AppSettings

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        appSettings = AppSettings(requireContext())
        setupPreferenceListeners()
    }
    
    private fun setupPreferenceListeners() {
        // Focus mode preference
        setupFocusModePreference()
        
        // Blocked apps navigation
        setupBlockedAppsPreference()
        
        // Smart blocking preference
        setupSmartBlockingPreference()
        
        // Blocking intensity preference
        setupBlockingIntensityPreference()
        
        // Blocking action preference
        setupBlockingActionPreference()
        
        // Test blocking action preference
        setupTestBlockingActionPreference()
        
        // Schedule focus mode
        setupScheduleFocusPreference()
        
        // Focus schedule configuration
        setupFocusSchedulePreference()
        
        // Break reminders
        setupBreakRemindersPreference()
        
        // Whitelist mode
        setupWhitelistModePreference()
        
        // Export data
        setupExportDataPreference()
        
        // Reset settings
        setupResetSettingsPreference()
        
        // Notification style
        setupNotificationStylePreference()
    }
    
    private fun setupFocusModePreference() {
        val focusModePref = findPreference<SwitchPreferenceCompat>(AppSettings.KEY_FOCUS_MODE)
        focusModePref?.setOnPreferenceChangeListener { _, newValue ->
            val focusModeEnabled = newValue as Boolean
            if (focusModeEnabled) {
                showFocusModeWarning()
            }
            true
        }
    }
    
    private fun setupBlockedAppsPreference() {
        val blockedAppsPref = findPreference<Preference>(AppSettings.KEY_BLOCKED_APPS)
        blockedAppsPref?.setOnPreferenceClickListener { 
            findNavController().navigate(R.id.action_settings_to_blocked_apps)
            true
        }
    }
    
    private fun setupSmartBlockingPreference() {
        val smartBlockingPref = findPreference<SwitchPreferenceCompat>("smart_blocking")
        smartBlockingPref?.setOnPreferenceChangeListener { _, newValue ->
            val enabled = newValue as Boolean
            if (enabled) {
                Toast.makeText(requireContext(), 
                    "Smart blocking uses advanced detection algorithms", 
                    Toast.LENGTH_SHORT).show()
            }
            true
        }
    }
    
    private fun setupBlockingIntensityPreference() {
        val intensityPref = findPreference<ListPreference>("blocking_intensity")
        intensityPref?.setOnPreferenceChangeListener { _, newValue ->
            val intensity = newValue as String
            when (intensity) {
                "light" -> intensityPref.summary = "Light blocking - Some content may get through"
                "medium" -> intensityPref.summary = "Medium blocking - Balanced approach"
                "strict" -> intensityPref.summary = "Strict blocking - More aggressive detection"
                "maximum" -> intensityPref.summary = "Maximum blocking - Highest sensitivity"
            }
            true
        }
    }
    
    private fun setupBlockingActionPreference() {
        val actionPref = findPreference<ListPreference>("blocking_action")
        actionPref?.setOnPreferenceChangeListener { _, newValue ->
            val action = newValue as String
            when (action) {
                "close_player" -> {
                    actionPref.summary = "Return to previous screen (default behavior)"
                }
                "close_app" -> {
                    actionPref.summary = "Force stop the blocked app completely"
                    showBlockingActionWarning("Close App", 
                        "This will completely close blocked apps. This may cause data loss if the app was not saved.")
                }
                "lock_screen" -> {
                    actionPref.summary = "Lock device screen to break addiction"
                    showBlockingActionWarning("Lock Screen", 
                        "This will lock your device screen immediately when blocked content is detected. Make sure you know your unlock method.")
                    // Check if device admin is enabled
                    checkDeviceAdminForLockScreen()
                }
            }
            true
        }
        
        // Set initial summary based on current value
        updateBlockingActionSummary()
    }
    
    private fun updateBlockingActionSummary() {
        val actionPref = findPreference<ListPreference>("blocking_action")
        val currentAction = appSettings.getBlockingAction()
        when (currentAction) {
            "close_player" -> actionPref?.summary = "Return to previous screen (default behavior)"
            "close_app" -> actionPref?.summary = "Force stop the blocked app completely"
            "lock_screen" -> actionPref?.summary = "Lock device screen to break addiction"
        }
    }
    
    private fun checkDeviceAdminForLockScreen() {
        // This will be handled by the BlockingActionHandler when the action is actually used
        // We can show a helpful message here
        Toast.makeText(requireContext(), 
            "If device admin is not enabled, Focus will ask for permission when needed", 
            Toast.LENGTH_LONG).show()
    }
    
    private fun setupTestBlockingActionPreference() {
        val testPref = findPreference<Preference>("test_blocking_action")
        testPref?.setOnPreferenceClickListener {
            testBlockingAction()
            true
        }
    }
    
    private fun testBlockingAction() {
        val currentAction = appSettings.getBlockingAction()
        val actionName = when (currentAction) {
            "close_player" -> "Close Player (Back navigation)"
            "close_app" -> "Close App (Force stop)"
            "lock_screen" -> "Lock Screen"
            else -> "Unknown action"
        }
        
        AlertDialog.Builder(requireContext())
            .setTitle("Test Blocking Action")
            .setMessage("Current action: $actionName\n\nThis will demonstrate what happens when blocked content is detected. Continue?")
            .setPositiveButton("Test Now") { _, _ ->
                performTestBlockingAction(currentAction)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun performTestBlockingAction(action: String) {
        try {
            val blockingActionHandler = com.aryanvbw.focus.util.BlockingActionHandler(requireContext())
            
            // Show a brief message about what's happening
            Toast.makeText(requireContext(), "Testing blocking action: $action", Toast.LENGTH_SHORT).show()
            
            // Execute the blocking action with a test package name
            blockingActionHandler.executeBlockingAction("com.focus.app.test", action)
            
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Error testing blocking action: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
    
    private fun setupScheduleFocusPreference() {
        val schedulePref = findPreference<SwitchPreferenceCompat>("schedule_focus_mode")
        schedulePref?.setOnPreferenceChangeListener { _, newValue ->
            val enabled = newValue as Boolean
            if (enabled) {
                Toast.makeText(requireContext(), 
                    "You can set focus hours in the schedule settings", 
                    Toast.LENGTH_LONG).show()
            }
            true
        }
    }
    
    private fun setupFocusSchedulePreference() {
        val scheduleConfigPref = findPreference<Preference>("focus_schedule")
        scheduleConfigPref?.setOnPreferenceClickListener {
            // TODO: Implement schedule configuration dialog
            showScheduleDialog()
            true
        }
    }
    
    private fun setupBreakRemindersPreference() {
        val breakPref = findPreference<SwitchPreferenceCompat>("break_reminders")
        breakPref?.setOnPreferenceChangeListener { _, newValue ->
            val enabled = newValue as Boolean
            if (enabled) {
                Toast.makeText(requireContext(), 
                    "Break reminders help maintain healthy focus habits", 
                    Toast.LENGTH_SHORT).show()
            }
            true
        }
    }
    
    private fun setupWhitelistModePreference() {
        val whitelistPref = findPreference<SwitchPreferenceCompat>("whitelist_mode")
        whitelistPref?.setOnPreferenceChangeListener { _, newValue ->
            val enabled = newValue as Boolean
            if (enabled) {
                showWhitelistWarning()
                return@setOnPreferenceChangeListener false // Don't change until user confirms
            }
            true
        }
    }
    
    private fun setupExportDataPreference() {
        val exportPref = findPreference<Preference>("export_data")
        exportPref?.setOnPreferenceClickListener {
            exportUsageData()
            true
        }
    }
    
    private fun setupResetSettingsPreference() {
        val resetPref = findPreference<Preference>("reset_settings")
        resetPref?.setOnPreferenceClickListener {
            showResetConfirmation()
            true
        }
    }
    
    private fun setupNotificationStylePreference() {
        val notificationPref = findPreference<ListPreference>("notification_style")
        notificationPref?.setOnPreferenceChangeListener { _, newValue ->
            val style = newValue as String
            when (style) {
                "minimal" -> notificationPref.summary = "Show minimal toast notifications"
                "standard" -> notificationPref.summary = "Show standard notifications"
                "detailed" -> notificationPref.summary = "Show detailed blocking information"
                "silent" -> notificationPref.summary = "No notifications (silent mode)"
            }
            true
        }
    }
    
    private fun showFocusModeWarning() {
        AlertDialog.Builder(requireContext())
            .setTitle("Enable Focus Mode?")
            .setMessage("Focus Mode will block all distracting apps and content. You can disable it anytime.")
            .setPositiveButton("Enable") { _, _ -> 
                Toast.makeText(requireContext(), "Focus Mode enabled", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun showWhitelistWarning() {
        AlertDialog.Builder(requireContext())
            .setTitle("Enable Whitelist Mode?")
            .setMessage("Whitelist mode will block ALL apps except those you specifically allow. This is very restrictive.")
            .setPositiveButton("Enable") { _, _ -> 
                findPreference<SwitchPreferenceCompat>("whitelist_mode")?.isChecked = true
                Toast.makeText(requireContext(), "Whitelist mode enabled", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun showBlockingActionWarning(title: String, message: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("$title Selected")
            .setMessage(message)
            .setPositiveButton("I Understand", null)
            .show()
    }
    
    private fun showScheduleDialog() {
        ScheduleConfigDialog(requireContext()).show()
    }
    
    private fun exportUsageData() {
        // TODO: Implement data export functionality
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Focus App Usage Data")
            putExtra(Intent.EXTRA_TEXT, "Usage data export feature coming soon!")
        }
        startActivity(Intent.createChooser(intent, "Export Usage Data"))
    }
    
    private fun showResetConfirmation() {
        AlertDialog.Builder(requireContext())
            .setTitle("Reset All Settings?")
            .setMessage("This will reset all your Focus app settings to defaults. This cannot be undone.")
            .setPositiveButton("Reset") { _, _ -> 
                resetAllSettings()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun resetAllSettings() {
        // Reset all preferences to defaults
        appSettings.getPreferences().edit().clear().apply()
        
        // Refresh the preference screen
        preferenceScreen.removeAll()
        addPreferencesFromResource(R.xml.preferences)
        setupPreferenceListeners()
        
        Toast.makeText(requireContext(), 
            "All settings have been reset to defaults", 
            Toast.LENGTH_LONG).show()
    }
}
