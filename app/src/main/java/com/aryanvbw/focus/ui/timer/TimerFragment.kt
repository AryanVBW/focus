package com.aryanvbw.focus.ui.timer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.aryanvbw.focus.databinding.FragmentTimerBinding

class TimerFragment : Fragment() {

    private var _binding: FragmentTimerBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var timerViewModel: TimerViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        timerViewModel = ViewModelProvider(this).get(TimerViewModel::class.java)
        _binding = FragmentTimerBinding.inflate(inflater, container, false)
        
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupUI()
        observeViewModel()
    }
    
    private fun setupUI() {
        // Setup click listeners
        binding.btnStartFocusSession.setOnClickListener {
            timerViewModel.startFocusSession()
        }
        
        binding.btnBlockDistractions.setOnClickListener {
            // Navigate to block fragment or start blocking
            timerViewModel.blockDistractions()
        }
        
        binding.btnViewProgress.setOnClickListener {
            // Navigate to progress fragment
            timerViewModel.viewProgress()
        }
        
        binding.ivSettings.setOnClickListener {
            // Navigate to settings
            timerViewModel.openSettings()
        }
    }
    
    private fun observeViewModel() {
        timerViewModel.focusedHours.observe(viewLifecycleOwner) { hours ->
            binding.tvHoursFocusedValue.text = hours.toString()
        }
        
        timerViewModel.sessionsCompleted.observe(viewLifecycleOwner) { sessions ->
            binding.tvSessionsCompletedValue.text = sessions.toString()
        }
        
        timerViewModel.distractionsBlocked.observe(viewLifecycleOwner) { blocked ->
            binding.tvDistractionsBlockedValue.text = blocked.toString()
        }
        
        timerViewModel.timerHours.observe(viewLifecycleOwner) { hours ->
            binding.tvHours.text = String.format("%02d", hours)
        }
        
        timerViewModel.timerMinutes.observe(viewLifecycleOwner) { minutes ->
            binding.tvMinutes.text = String.format("%02d", minutes)
        }
        
        timerViewModel.timerSeconds.observe(viewLifecycleOwner) { seconds ->
            binding.tvSeconds.text = String.format("%02d", seconds)
        }
        
        timerViewModel.isTimerRunning.observe(viewLifecycleOwner) { isRunning ->
            binding.tvStartFocusSession.text = if (isRunning) "Stop Focus Session" else "Start Focus Session"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}