package com.focus.app.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.focus.app.R
import com.focus.app.databinding.FragmentHomeBinding
import com.focus.app.util.AppSettings

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var settings: AppSettings

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        settings = AppSettings(requireContext())
        
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupUI()
        observeViewModel()
    }
    
    private fun setupUI() {
        // Set the initial state of the focus mode switch
        binding.switchFocusMode.isChecked = settings.isFocusModeEnabled()
        
        // Handle focus mode switch changes
        binding.switchFocusMode.setOnCheckedChangeListener { _, isChecked ->
            settings.getPreferences().edit().putBoolean(AppSettings.KEY_FOCUS_MODE, isChecked).apply()
            updateUIState(isChecked)
        }
        
        // Set initial UI state based on current settings
        updateUIState(settings.isFocusModeEnabled())
        
        // Set up the start/stop button
        binding.buttonToggleService.setOnClickListener {
            val isEnabled = !settings.isServiceEnabled()
            settings.setServiceEnabled(isEnabled)
            updateServiceButtonState(isEnabled)
        }
        
        // Update service button state based on current setting
        updateServiceButtonState(settings.isServiceEnabled())
        
        // Set up the Focus Now button - this is our main action button
        binding.buttonFocusNow.setOnClickListener {
            activateFocusMode()
        }
        
        // Setup user details
        setupUserProfile()
    }
    
    /**
     * Activate Focus Mode to block distracting content
     */
    private fun activateFocusMode() {
        // Enable the service
        settings.setServiceEnabled(true)
        
        // Enable focus mode to block all distractions
        settings.getPreferences().edit().putBoolean(AppSettings.KEY_FOCUS_MODE, true).apply()
        
        // Update UI states
        binding.switchFocusMode.isChecked = true
        updateUIState(true)
        updateServiceButtonState(true)
        
        // Show success message
        Toast.makeText(requireContext(), 
            "Focus Mode activated! All distracting content will be blocked", 
            Toast.LENGTH_LONG).show()
    }
    
    /**
     * Setup user profile information
     */
    private fun setupUserProfile() {
        // Set user name and status
        binding.textUserName.text = "Focus User"
        
        // Update status based on focus mode
        updateUserStatus(settings.isFocusModeEnabled())
    }
    
    /**
     * Update user status based on focus mode
     */
    private fun updateUserStatus(focusModeEnabled: Boolean) {
        if (focusModeEnabled) {
            binding.textUserStatus.text = "Currently focused and productive"
        } else {
            binding.textUserStatus.text = "Ready to focus and be productive"
        }
    }
    
    private fun updateUIState(focusModeEnabled: Boolean) {
        if (focusModeEnabled) {
            binding.imageFocusMode.setImageResource(R.drawable.ic_shield)
            binding.textFocusMode.setText(R.string.setting_focus_mode)
            binding.textFocusModeDesc.setText(R.string.setting_focus_mode_desc)
            binding.buttonFocusNow.text = "FOCUS MODE ACTIVE"
            binding.buttonFocusNow.isEnabled = false
        } else {
            binding.imageFocusMode.setImageResource(R.drawable.ic_shield)
            binding.textFocusMode.setText(R.string.setting_focus_mode)
            binding.textFocusModeDesc.setText(R.string.setting_focus_mode_desc)
            binding.buttonFocusNow.text = "FOCUS NOW"
            binding.buttonFocusNow.isEnabled = true
        }
        
        // Also update user status
        updateUserStatus(focusModeEnabled)
    }
    
    private fun updateServiceButtonState(isEnabled: Boolean) {
        if (isEnabled) {
            binding.buttonToggleService.setText(R.string.action_stop)
            binding.buttonToggleService.setBackgroundColor(resources.getColor(R.color.error, null))
            binding.textServiceStatus.text = "Focus is protecting you from distractions"
            binding.imageServiceStatus.setImageResource(R.drawable.ic_shield)
        } else {
            binding.buttonToggleService.setText(R.string.action_start)
            binding.buttonToggleService.setBackgroundColor(resources.getColor(R.color.primary, null))
            binding.textServiceStatus.text = "Focus is not running"
            binding.imageServiceStatus.setImageResource(R.drawable.ic_block)
        }
    }
    
    private fun observeViewModel() {
        homeViewModel.todayBlockedCount.observe(viewLifecycleOwner) { count ->
            binding.textBlockedCount.text = count.toString()
        }
        
        homeViewModel.totalTimeBlocked.observe(viewLifecycleOwner) { timeInMinutes ->
            binding.textTimeBlocked.text = "$timeInMinutes min"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
