package com.aryanvbw.focus.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.aryanvbw.focus.R
import com.aryanvbw.focus.databinding.FragmentHomeBinding
import com.aryanvbw.focus.service.UsageAnalyticsService
import com.aryanvbw.focus.util.AppSettings
import com.aryanvbw.focus.ui.analytics.UsageAnalyticsActivity
import com.aryanvbw.focus.ui.limits.AppLimitsActivity
import com.aryanvbw.focus.ui.blocking.ContentBlockingActivity
import com.aryanvbw.focus.ui.insights.SmartInsightsActivity
import com.aryanvbw.focus.ui.components.AnimatedFocusButton
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    
    private val homeViewModel: HomeViewModel by lazy {
        HomeViewModel(requireActivity().application)
    }
    
    private lateinit var settings: AppSettings

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        settings = AppSettings(requireContext())
        
        setupUI()
        setupObservers()
        updateTimeDisplay()
        
        // Load initial mode
        val currentMode = settings.getCurrentAppMode()
        updateModeUI(currentMode)
        binding.btnAnimatedFocus.setActive(currentMode == AppSettings.APP_MODE_FOCUS)
    }

    private fun setupUI() {
        // Setup animated focus button
        binding.btnAnimatedFocus.onClickListener = { isActive ->
            val newMode = if (isActive) AppSettings.APP_MODE_FOCUS else AppSettings.APP_MODE_NORMAL
            onModeChanged(newMode)
        }
        
        // Set up click listeners for remaining components
        // Focus mode toggle is handled by the animated button
    }

    private fun setupObservers() {
        // Observe blocked content events for statistics
        homeViewModel.todayBlockedCount.observe(viewLifecycleOwner) { count ->
            binding.tvBlockedCountValue.text = count.toString()
        }
        
        // Observe usage analytics data
        lifecycleScope.launch {
            homeViewModel.estimatedTimeSavedMinutes.observe(viewLifecycleOwner) { focusTimeMinutes ->
                val hours = focusTimeMinutes / 60
                val minutes = focusTimeMinutes % 60
                binding.tvFocusTimeValue.text = if (hours > 0) {
                    "${hours}h ${minutes}m"
                } else {
                    "${minutes}m"
                }
            }
        }
    }

    private fun onModeChanged(mode: String) {
        settings.setCurrentAppMode(mode)
        homeViewModel.setAppMode(mode)
        updateModeUI(mode)
        
        when (mode) {
            AppSettings.APP_MODE_NORMAL -> {
                // Start usage analytics service
                startUsageAnalyticsService()
                showModeChangeMessage("Normal Mode", "Monitor your digital habits and get insights")
            }
            AppSettings.APP_MODE_FOCUS -> {
                // Enable focus mode blocking
                settings.setServiceEnabled(true)
                showModeChangeMessage("Focus Mode", "Block distracting content and stay focused")
            }
        }
    }
    
    private fun updateModeUI(mode: String) {
        val isActive = mode == AppSettings.APP_MODE_FOCUS
        binding.btnAnimatedFocus.setActive(isActive)
        
        // Update focus status text
        binding.tvFocusStatus.text = if (isActive) {
            "Focus Mode is Active"
        } else {
            "Tap to activate Focus Mode"
        }
        
        // Update mode description
        binding.tvModeDescription.text = if (isActive) {
            "Blocking distractions • Stay focused"
        } else {
            "Block distractions and stay focused"
        }
        
        // Update stats with placeholder values
        binding.tvBlockedCountValue.text = "127"
        binding.tvFocusTimeValue.text = "2h 45m"
        binding.tvStreakValue.text = "7"
        binding.tvProductivityScore.text = "85%"
        
        // Update time display with current time
        updateTimeDisplay()
    }
    
    private fun updateTimeDisplay() {
        val calendar = Calendar.getInstance()
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val greeting = when {
            hour < 12 -> "Good morning!"
            hour < 17 -> "Good afternoon!"
            else -> "Good evening!"
        }
        binding.tvWelcomeMessage.text = greeting
    }
    
    private fun startUsageAnalyticsService() {
        if (settings.isUsageAnalyticsEnabled()) {
            val intent = Intent(requireContext(), UsageAnalyticsService::class.java)
            requireContext().startService(intent)
        }
    }
    
    private fun showModeChangeMessage(modeTitle: String, description: String) {
        Toast.makeText(
            requireContext(),
            "$modeTitle activated! $description",
            Toast.LENGTH_LONG
        ).show()
    }
    
    override fun onResume() {
        super.onResume()
        // Update time display when fragment resumes
        updateTimeDisplay()
        
        // Refresh statistics
        val currentMode = settings.getCurrentAppMode()
        updateModeUI(currentMode)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
