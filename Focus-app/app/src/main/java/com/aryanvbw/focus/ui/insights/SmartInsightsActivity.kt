package com.aryanvbw.focus.ui.insights

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.aryanvbw.focus.databinding.ActivitySmartInsightsBinding

class SmartInsightsActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivitySmartInsightsBinding
    private val viewModel: SmartInsightsViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySmartInsightsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupToolbar()
        setupObservers()
        
        // Load data
        viewModel.loadInsights()
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }
    
    private fun setupObservers() {
        viewModel.productivityScore.observe(this) { score ->
            binding.tvProductivityScore.text = score.toString()
            
            // Update score trend
            val trend = if (score >= 75) "↗ +5 from yesterday" else "↘ -2 from yesterday"
            binding.tvScoreTrend.text = trend
        }
        
        viewModel.weeklyStats.observe(this) { stats ->
            binding.tvFocusSessions.text = "${stats.focusHours}h ${stats.focusMinutes}m"
            binding.tvAvgSession.text = "${(stats.focusHours * 60 + stats.focusMinutes) / 7}m"
            binding.tvPeakHour.text = "2-4 PM" // Static for now
            binding.tvStreakDays.text = stats.streakDays.toString()
        }
        
        // Note: Insights and Goals are handled by RecyclerViews in the actual layout
        // The adapters should be set up here when the ViewModels are properly implemented
    }
}
