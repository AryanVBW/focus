package com.aryanvbw.focus.ui.analytics

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.aryanvbw.focus.R
import com.aryanvbw.focus.databinding.ActivityUsageAnalyticsBinding
import com.aryanvbw.focus.util.AppSettings
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.utils.ColorTemplate
import java.text.SimpleDateFormat
import java.util.*

class UsageAnalyticsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUsageAnalyticsBinding
    private lateinit var viewModel: UsageAnalyticsViewModel
    private lateinit var settings: AppSettings
    private lateinit var appUsageAdapter: AppUsageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsageAnalyticsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeComponents()
        setupToolbar()
        setupRecyclerView()
        setupCharts()
        observeData()
        loadAnalyticsData()
    }

    private fun initializeComponents() {
        viewModel = ViewModelProvider(this)[UsageAnalyticsViewModel::class.java]
        settings = AppSettings(this)
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = "Usage Analytics"
        }
        
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        appUsageAdapter = AppUsageAdapter { appUsage ->
            // Handle app usage item click - show detailed view
            showAppDetailDialog(appUsage)
        }
        
        binding.recyclerAppUsage.apply {
            layoutManager = LinearLayoutManager(this@UsageAnalyticsActivity)
            adapter = appUsageAdapter
        }
    }

    private fun setupCharts() {
        setupWeeklyChart()
        setupCategoryChart()
        setupHourlyChart()
    }

    private fun setupWeeklyChart() {
        binding.chartWeeklyUsage.apply {
            description.isEnabled = false
            setTouchEnabled(true)
            setDragEnabled(true)
            setScaleEnabled(true)
            setPinchZoom(true)
            setDrawGridBackground(false)
            
            xAxis.apply {
                setDrawGridLines(false)
                position = com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM
            }
            
            axisLeft.apply {
                setDrawGridLines(true)
                axisMinimum = 0f
            }
            
            axisRight.isEnabled = false
            legend.isEnabled = true
        }
    }

    private fun setupCategoryChart() {
        binding.chartCategoryUsage.apply {
            description.isEnabled = false
            setUsePercentValues(true)
            setEntryLabelColor(resources.getColor(R.color.text_primary, null))
            setEntryLabelTextSize(12f)
            setDrawHoleEnabled(true)
            setHoleColor(resources.getColor(R.color.background_light, null))
            setHoleRadius(58f)
            setTransparentCircleRadius(61f)
            setDrawCenterText(true)
            centerText = "App Categories"
            setCenterTextSize(16f)
            setRotationAngle(0f)
            setRotationEnabled(true)
            setHighlightPerTapEnabled(true)
        }
    }

    private fun setupHourlyChart() {
        binding.chartHourlyUsage.apply {
            description.isEnabled = false
            setTouchEnabled(true)
            setDragEnabled(true)
            setScaleEnabled(true)
            setPinchZoom(true)
            setDrawGridBackground(false)
            
            xAxis.apply {
                setDrawGridLines(false)
                position = com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM
                granularity = 1f
            }
            
            axisLeft.apply {
                setDrawGridLines(true)
                axisMinimum = 0f
            }
            
            axisRight.isEnabled = false
            legend.isEnabled = false
        }
    }

    private fun observeData() {
        viewModel.appUsageList.observe(this) { usageList ->
            appUsageAdapter.updateData(usageList)
            updateStatsCards(usageList)
        }

        viewModel.weeklyData.observe(this) { weeklyData ->
            updateWeeklyChart(weeklyData)
        }

        viewModel.categoryData.observe(this) { categoryData ->
            updateCategoryChart(categoryData)
        }

        viewModel.hourlyData.observe(this) { hourlyData ->
            updateHourlyChart(hourlyData)
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun loadAnalyticsData() {
        val selectedPeriod = binding.spinnerTimePeriod.selectedItem.toString()
        viewModel.loadUsageData(selectedPeriod)
        
        binding.spinnerTimePeriod.setOnItemSelectedListener(object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) {
                val period = parent?.getItemAtPosition(position).toString()
                viewModel.loadUsageData(period)
            }
            
            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        })
    }

    private fun updateStatsCards(usageList: List<AppUsageData>) {
        val totalUsageTime = usageList.sumOf { it.usageTimeMinutes }
        val totalOpenCount = usageList.sumOf { it.openCount }
        val mostUsedApp = usageList.maxByOrNull { it.usageTimeMinutes }
        
        binding.apply {
            textTotalUsageTime.text = formatUsageTime(totalUsageTime)
            textTotalOpenCount.text = totalOpenCount.toString()
            textMostUsedApp.text = mostUsedApp?.appName ?: "N/A"
            textAverageSession.text = if (totalOpenCount > 0) {
                formatUsageTime(totalUsageTime / totalOpenCount)
            } else "0m"
        }
    }

    private fun updateWeeklyChart(weeklyData: List<DailyUsageData>) {
        val entries = weeklyData.mapIndexed { index, data ->
            BarEntry(index.toFloat(), data.totalMinutes.toFloat())
        }

        val dataSet = BarDataSet(entries, "Daily Usage (Minutes)").apply {
            colors = ColorTemplate.MATERIAL_COLORS.toList()
            valueTextSize = 12f
            valueTextColor = resources.getColor(R.color.text_primary, null)
        }

        val barData = BarData(dataSet)
        binding.chartWeeklyUsage.apply {
            data = barData
            animateY(1000)
            invalidate()
        }
    }

    private fun updateCategoryChart(categoryData: List<CategoryUsageData>) {
        val entries = categoryData.map { category ->
            PieEntry(category.percentage, category.categoryName)
        }

        val dataSet = PieDataSet(entries, "").apply {
            colors = ColorTemplate.JOYFUL_COLORS.toList()
            valueTextSize = 12f
            valueTextColor = resources.getColor(R.color.text_primary, null)
        }

        val pieData = PieData(dataSet)
        binding.chartCategoryUsage.apply {
            data = pieData
            animateY(1000)
            invalidate()
        }
    }

    private fun updateHourlyChart(hourlyData: List<HourlyUsageData>) {
        val entries = hourlyData.map { hour ->
            Entry(hour.hour.toFloat(), hour.usageMinutes.toFloat())
        }

        val dataSet = LineDataSet(entries, "Hourly Usage").apply {
            color = resources.getColor(R.color.primary, null)
            setCircleColor(resources.getColor(R.color.primary, null))
            lineWidth = 2f
            circleRadius = 4f
            setDrawCircleHole(false)
            valueTextSize = 10f
            setDrawFilled(true)
            // fillDrawable = resources.getDrawable(R.drawable.chart_fill_gradient, null) // TODO: Add gradient drawable
        }

        val lineData = LineData(dataSet)
        binding.chartHourlyUsage.apply {
            data = lineData
            animateX(1000)
            invalidate()
        }
    }

    private fun showAppDetailDialog(appUsage: AppUsageData) {
        // Show detailed dialog with app-specific analytics
        // TODO: Implement AppDetailDialogFragment
        // val dialog = AppDetailDialogFragment.newInstance(appUsage)
        // dialog.show(supportFragmentManager, "AppDetailDialog")
    }

    private fun formatUsageTime(minutes: Long): String {
        return when {
            minutes < 60 -> "${minutes}m"
            minutes < 1440 -> "${minutes / 60}h ${minutes % 60}m"
            else -> "${minutes / 1440}d ${(minutes % 1440) / 60}h"
        }
    }
}
