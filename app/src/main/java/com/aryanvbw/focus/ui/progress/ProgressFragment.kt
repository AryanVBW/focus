package com.aryanvbw.focus.ui.progress

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.aryanvbw.focus.databinding.FragmentProgressBinding

class ProgressFragment : Fragment() {

    private var _binding: FragmentProgressBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var progressViewModel: ProgressViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        progressViewModel = ViewModelProvider(this).get(ProgressViewModel::class.java)
        _binding = FragmentProgressBinding.inflate(inflater, container, false)
        
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupUI()
        observeViewModel()
    }
    
    private fun setupUI() {
        // Setup click listeners
        binding.ivMenu.setOnClickListener {
            // Open navigation drawer or menu
            progressViewModel.openMenu()
        }
        
        binding.cardBestFocusDay.setOnClickListener {
            progressViewModel.showBestFocusDayDetails()
        }
        
        binding.cardLongestSession.setOnClickListener {
            progressViewModel.showLongestSessionDetails()
        }
        
        binding.cardDistractionsBlocked.setOnClickListener {
            progressViewModel.showDistractionsBlockedDetails()
        }
    }
    
    private fun observeViewModel() {
        progressViewModel.weeklyFocusTime.observe(viewLifecycleOwner) { time ->
            binding.tvWeeklyFocusTime.text = time
        }
        
        progressViewModel.dailyStreak.observe(viewLifecycleOwner) { streak ->
            binding.tvDailyStreakValue.text = "$streak Days"
        }
        
        progressViewModel.bestFocusDay.observe(viewLifecycleOwner) { day ->
            binding.tvBestFocusDayValue.text = day
        }
        
        progressViewModel.longestSession.observe(viewLifecycleOwner) { session ->
            binding.tvLongestSessionValue.text = session
        }
        
        progressViewModel.distractionsBlocked.observe(viewLifecycleOwner) { blocked ->
            binding.tvDistractionsBlockedValue.text = blocked.toString()
        }
        
        progressViewModel.weeklyChartData.observe(viewLifecycleOwner) { chartData ->
            // Update chart with data
            updateWeeklyChart(chartData)
        }
    }
    
    private fun updateWeeklyChart(chartData: List<Float>) {
        // This is a placeholder for chart update logic
        // In a real implementation, you would update the chart bars with the actual data
        // For now, the chart is static in the XML layout
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}