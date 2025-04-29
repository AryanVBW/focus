package com.focus.app.ui.stats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.focus.app.databinding.FragmentStatsBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class StatsFragment : Fragment() {

    private var _binding: FragmentStatsBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var statsViewModel: StatsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        statsViewModel = ViewModelProvider(this).get(StatsViewModel::class.java)
        _binding = FragmentStatsBinding.inflate(inflater, container, false)
        
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupDateSelection()
        observeViewModel()
    }
    
    private fun setupDateSelection() {
        // Set current date in header
        updateDateHeader(Calendar.getInstance().time)
        
        // Previous day button
        binding.buttonPrevDay.setOnClickListener {
            statsViewModel.loadPreviousDay()
        }
        
        // Next day button
        binding.buttonNextDay.setOnClickListener {
            statsViewModel.loadNextDay()
        }
    }
    
    private fun observeViewModel() {
        // Observe selected date
        statsViewModel.selectedDate.observe(viewLifecycleOwner) { date ->
            updateDateHeader(date)
        }
        
        // Observe blocked content count
        statsViewModel.blockedContentCount.observe(viewLifecycleOwner) { count ->
            binding.textBlockedCount.text = count.toString()
        }
        
        // Observe estimated time saved
        statsViewModel.estimatedTimeSaved.observe(viewLifecycleOwner) { minutes ->
            binding.textTimeSaved.text = "$minutes min"
        }
        
        // Observe app-specific stats
        statsViewModel.appStats.observe(viewLifecycleOwner) { stats ->
            // In a full implementation, this would update a RecyclerView
            // with detailed per-app statistics
            binding.textAppStats.text = buildAppStatsText(stats)
        }
        
        // Observe content type stats
        statsViewModel.contentTypeStats.observe(viewLifecycleOwner) { stats ->
            // In a full implementation, this would update a chart or graph
            // showing breakdown by content type
            binding.textContentTypeStats.text = buildContentTypeStatsText(stats)
        }
    }
    
    private fun updateDateHeader(date: Date) {
        val dateFormat = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault())
        binding.textDate.text = dateFormat.format(date)
    }
    
    private fun buildAppStatsText(stats: Map<String, Int>): String {
        if (stats.isEmpty()) {
            return "No data for this day"
        }
        
        val sb = StringBuilder()
        stats.forEach { (app, count) ->
            sb.append("$app: $count blocks\n")
        }
        return sb.toString()
    }
    
    private fun buildContentTypeStatsText(stats: Map<String, Int>): String {
        if (stats.isEmpty()) {
            return "No data for this day"
        }
        
        val sb = StringBuilder()
        stats.forEach { (type, count) ->
            sb.append("$type: $count blocks\n")
        }
        return sb.toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
