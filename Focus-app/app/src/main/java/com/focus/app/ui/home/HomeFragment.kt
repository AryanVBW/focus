package com.aryanvbw.focus.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.aryanvbw.focus.R
import com.aryanvbw.focus.databinding.FragmentHomeEnhancedBinding
import com.aryanvbw.focus.util.AppSettings
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeEnhancedBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var settings: AppSettings

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        _binding = FragmentHomeEnhancedBinding.inflate(inflater, container, false)
        settings = AppSettings(requireContext())
        
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupUI()
        observeViewModel()
    }
    
    private fun setupUI() {
        // Set up user greeting
        setupUserGreeting()
        
        // Set up quick action buttons
        setupQuickActions()
        
        // Set up stats preview
        setupStatsPreview()
        
        // Update service status
        updateServiceStatus()
        
        // Set the initial state of the focus mode switch
        binding.switchFocusMode.isChecked = settings.isFocusModeEnabled()
        
        // Handle focus mode switch changes
        binding.switchFocusMode.setOnCheckedChangeListener { _, isChecked ->
            settings.getPreferences().edit().putBoolean(AppSettings.KEY_FOCUS_MODE, isChecked).apply()
            updateUIState(isChecked)
        }
        
        // Set initial UI state based on current settings
        updateUIState(settings.isFocusModeEnabled())
    }
    
    /**
     * Set up user greeting based on time of day
     */
    private fun setupUserGreeting() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        
        val greeting = when {
            hour < 12 -> "Good Morning"
            hour < 17 -> "Good Afternoon"
            else -> "Good Evening"
        }
        
        binding.textGreeting.text = greeting
        binding.textUserName.text = "Focus User"
        
        // Update focus streak - this could be calculated from usage data
        val streakDays = 3 // Placeholder value
        binding.textFocusStreak.text = "🔥 $streakDays day focus streak!"
    }
    
    /**
     * Set up quick action buttons
     */
    private fun setupQuickActions() {
        binding.buttonFocusNow.setOnClickListener {
            activateFocusMode()
        }
        
        binding.buttonQuickBreak.setOnClickListener {
            takeBreak()
        }
        
        binding.buttonSchedule.setOnClickListener {
            // Navigate to settings - this would need to be implemented based on navigation
            Toast.makeText(requireContext(), "Opening schedule settings...", Toast.LENGTH_SHORT).show()
        }
    }
    
    /**
     * Set up stats preview with current data
     */
    private fun setupStatsPreview() {
        // These will be updated by the ViewModel observer
        binding.textBlocksCount.text = "0"
        binding.textFocusTime.text = "0 min"
        binding.textProductivityScore.text = "0%"
    }
    
    /**
     * Take a break - temporarily disable focus mode
     */
    private fun takeBreak() {
        settings.getPreferences().edit().putBoolean(AppSettings.KEY_FOCUS_MODE, false).apply()
        binding.switchFocusMode.isChecked = false
        updateUIState(false)
        
        Toast.makeText(requireContext(), 
            "Break time! Focus mode temporarily disabled", 
            Toast.LENGTH_SHORT).show()
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
        
        // Show success message
        Toast.makeText(requireContext(), 
            "Focus Mode activated! All distracting content will be blocked", 
            Toast.LENGTH_LONG).show()
    }
    
    private fun updateUIState(focusModeEnabled: Boolean) {
        if (focusModeEnabled) {
            binding.buttonFocusNow.text = "FOCUS MODE ACTIVE"
            binding.buttonFocusNow.isEnabled = false
            binding.textServiceStatus.text = "Focus mode is protecting you from distractions"
        } else {
            binding.buttonFocusNow.text = "FOCUS NOW"
            binding.buttonFocusNow.isEnabled = true
            binding.textServiceStatus.text = "Ready to focus and be productive"
        }
    }
    
    /**
     * Update service status display
     */
    private fun updateServiceStatus() {
        val isServiceEnabled = settings.isServiceEnabled()
        if (isServiceEnabled) {
            binding.imageServiceStatus.setImageResource(R.drawable.ic_shield)
            binding.textServiceStatus.text = "Focus is protecting you from distractions"
        } else {
            binding.imageServiceStatus.setImageResource(R.drawable.ic_block)
            binding.textServiceStatus.text = "Focus is not running"
        }
    }

    private fun observeViewModel() {
        homeViewModel.todayBlockedCount.observe(viewLifecycleOwner) { count ->
            binding.textBlocksCount.text = count.toString()
        }
        
        homeViewModel.estimatedTimeSavedMinutes.observe(viewLifecycleOwner) { timeInMinutes ->
            val hours = timeInMinutes / 60
            val minutes = timeInMinutes % 60
            binding.textFocusTime.text = if (hours > 0) "${hours}h ${minutes}m" else "${minutes}m"
        }
        
        // TODO: Add productivity score calculation to ViewModel
        // For now, show a placeholder
        binding.textProductivityScore.text = "85%"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
