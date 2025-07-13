package com.aryanvbw.focus.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.aryanvbw.focus.R
import com.aryanvbw.focus.databinding.FragmentSettingsBinding
import com.aryanvbw.focus.util.AppSettings

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var settingsViewModel: SettingsViewModel
    private lateinit var appSettings: AppSettings

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        settingsViewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        appSettings = AppSettings(requireContext())
        
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupUI()
        observeViewModel()
    }
    
    private fun setupUI() {
        // Setup click listeners
        binding.ivBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
        
        // Focus Sessions settings
        binding.cardTimerLength.setOnClickListener {
            settingsViewModel.openTimerLengthSettings()
        }
        
        binding.cardShortBreak.setOnClickListener {
            settingsViewModel.openShortBreakSettings()
        }
        
        binding.cardLongBreak.setOnClickListener {
            settingsViewModel.openLongBreakSettings()
        }
        
        // Blocking settings
        binding.cardWebsites.setOnClickListener {
            settingsViewModel.openWebsiteBlockingSettings()
        }
        
        binding.cardApps.setOnClickListener {
            findNavController().navigate(R.id.action_settings_to_blocked_apps)
        }
        
        binding.cardSchedules.setOnClickListener {
            settingsViewModel.openScheduleSettings()
        }
        
        // Notifications settings
        binding.cardMotivationQuotes.setOnClickListener {
            settingsViewModel.openMotivationQuotesSettings()
        }
        
        binding.cardBreakReminders.setOnClickListener {
            settingsViewModel.openBreakRemindersSettings()
        }
        
        // Account settings
        binding.cardLogOut.setOnClickListener {
            showLogoutConfirmation()
        }
    }
    
    private fun observeViewModel() {
        settingsViewModel.navigationEvent.observe(viewLifecycleOwner) { event ->
            when (event) {
                "timer_length" -> showTimerLengthDialog()
                "short_break" -> showShortBreakDialog()
                "long_break" -> showLongBreakDialog()
                "websites" -> showWebsiteBlockingDialog()
                "schedules" -> showScheduleDialog()
                "motivation_quotes" -> showMotivationQuotesDialog()
                "break_reminders" -> showBreakRemindersDialog()
            }
        }
    }
    
    private fun showTimerLengthDialog() {
        val options = arrayOf("15 minutes", "25 minutes", "30 minutes", "45 minutes", "60 minutes")
        val values = arrayOf(15, 25, 30, 45, 60)
        val currentValue = appSettings.getTimerLength()
        val currentIndex = values.indexOf(currentValue)
        
        AlertDialog.Builder(requireContext())
            .setTitle("Timer Length")
            .setSingleChoiceItems(options, currentIndex) { dialog, which ->
                appSettings.setTimerLength(values[which])
                dialog.dismiss()
                Toast.makeText(requireContext(), "Timer length updated", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun showShortBreakDialog() {
        val options = arrayOf("3 minutes", "5 minutes", "10 minutes", "15 minutes")
        val values = arrayOf(3, 5, 10, 15)
        val currentValue = appSettings.getShortBreakLength()
        val currentIndex = values.indexOf(currentValue)
        
        AlertDialog.Builder(requireContext())
            .setTitle("Short Break Length")
            .setSingleChoiceItems(options, currentIndex) { dialog, which ->
                appSettings.setShortBreakLength(values[which])
                dialog.dismiss()
                Toast.makeText(requireContext(), "Short break length updated", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun showLongBreakDialog() {
        val options = arrayOf("15 minutes", "20 minutes", "30 minutes", "45 minutes")
        val values = arrayOf(15, 20, 30, 45)
        val currentValue = appSettings.getLongBreakLength()
        val currentIndex = values.indexOf(currentValue)
        
        AlertDialog.Builder(requireContext())
            .setTitle("Long Break Length")
            .setSingleChoiceItems(options, currentIndex) { dialog, which ->
                appSettings.setLongBreakLength(values[which])
                dialog.dismiss()
                Toast.makeText(requireContext(), "Long break length updated", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun showWebsiteBlockingDialog() {
        Toast.makeText(requireContext(), "Website blocking settings", Toast.LENGTH_SHORT).show()
    }
    
    private fun showMotivationQuotesDialog() {
        val isEnabled = appSettings.getMotivationQuotesEnabled()
        
        AlertDialog.Builder(requireContext())
            .setTitle("Motivation Quotes")
            .setMessage("Enable motivational quotes during focus sessions?")
            .setPositiveButton(if (isEnabled) "Disable" else "Enable") { _, _ ->
                appSettings.setMotivationQuotesEnabled(!isEnabled)
                Toast.makeText(requireContext(), 
                    if (!isEnabled) "Motivation quotes enabled" else "Motivation quotes disabled", 
                    Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun showBreakRemindersDialog() {
        val isEnabled = appSettings.getBreakRemindersEnabled()
        
        AlertDialog.Builder(requireContext())
            .setTitle("Break Reminders")
            .setMessage("Enable break reminders during focus sessions?")
            .setPositiveButton(if (isEnabled) "Disable" else "Enable") { _, _ ->
                appSettings.setBreakRemindersEnabled(!isEnabled)
                Toast.makeText(requireContext(), 
                    if (!isEnabled) "Break reminders enabled" else "Break reminders disabled", 
                    Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun showScheduleDialog() {
        Toast.makeText(requireContext(), "Schedule settings", Toast.LENGTH_SHORT).show()
    }
    
    private fun showLogoutConfirmation() {
        AlertDialog.Builder(requireContext())
            .setTitle("Log Out")
            .setMessage("Are you sure you want to log out? Your data will be saved.")
            .setPositiveButton("Log Out") { _, _ ->
                performLogout()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun performLogout() {
        // Clear user session data
        appSettings.clearUserSession()
        
        Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show()
        
        // Navigate to login or main screen
        requireActivity().finish()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
