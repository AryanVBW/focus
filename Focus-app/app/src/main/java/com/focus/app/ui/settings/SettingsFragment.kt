package com.aryanvbw.focus.ui.settings

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.aryanvbw.focus.R
import com.aryanvbw.focus.util.AppSettings

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        
        // Setup preference change listeners if needed
        setupPreferenceListeners()
    }
    
    private fun setupPreferenceListeners() {
        // Get the focus mode preference
        val focusModePref = findPreference<SwitchPreferenceCompat>(AppSettings.KEY_FOCUS_MODE)
        
        // Set listener for focus mode changes
        focusModePref?.setOnPreferenceChangeListener { _, newValue ->
            // When focus mode changes, we may want to update UI or trigger other actions
            val focusModeEnabled = newValue as Boolean
            
            // Return true to update the preference value
            true
        }

        // --- Blocked Apps Navigation --- 
        val blockedAppsPref = findPreference<Preference>(AppSettings.KEY_BLOCKED_APPS)
        blockedAppsPref?.setOnPreferenceClickListener { 
            // Navigate to the BlockedAppsFragment using the action defined in navigation graph
            findNavController().navigate(R.id.action_settings_to_blocked_apps)
            true // Indicate the click was handled
        }
    }
}
