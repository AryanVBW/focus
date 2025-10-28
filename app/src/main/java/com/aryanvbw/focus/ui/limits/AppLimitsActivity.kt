package com.aryanvbw.focus.ui.limits

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.aryanvbw.focus.R
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.aryanvbw.focus.ui.dialogs.AddLimitDialogFragment
import com.aryanvbw.focus.ui.dialogs.EditLimitDialogFragment
import com.aryanvbw.focus.util.AppUsageManager
import kotlinx.coroutines.launch

class AppLimitsActivity : AppCompatActivity() {
    
    private lateinit var toolbar: Toolbar
    private lateinit var rvAppLimits: RecyclerView
    private lateinit var btnAddLimit: Button
    private lateinit var btnAddFirstLimit: Button
    private lateinit var fabAddLimit: FloatingActionButton
    private lateinit var btnBulkEdit: Button
    private lateinit var btnFilter: Button
    private lateinit var progressLoading: ProgressBar
    private lateinit var layoutEmptyState: LinearLayout
    private lateinit var tvActiveLimitsCount: TextView
    private lateinit var tvExceededCount: TextView
    private lateinit var tvTimeSaved: TextView
    private val viewModel: AppLimitsViewModel by viewModels()
    private lateinit var appLimitsAdapter: AppLimitsAdapter
    private lateinit var appUsageManager: AppUsageManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_limits)
        
        // Initialize views
        toolbar = findViewById(R.id.toolbar)
        rvAppLimits = findViewById(R.id.rv_app_limits)
        btnAddLimit = findViewById(R.id.btn_add_limit)
        btnAddFirstLimit = findViewById(R.id.btn_add_first_limit)
        fabAddLimit = findViewById(R.id.fab_add_limit)
        btnBulkEdit = findViewById(R.id.btn_bulk_edit)
        btnFilter = findViewById(R.id.btn_filter)
        progressLoading = findViewById(R.id.progress_loading)
        layoutEmptyState = findViewById(R.id.layout_empty_state)
        tvActiveLimitsCount = findViewById(R.id.tv_active_limits_count)
        tvExceededCount = findViewById(R.id.tv_exceeded_count)
        tvTimeSaved = findViewById(R.id.tv_time_saved)
        
        appUsageManager = AppUsageManager(this)
        
        setupToolbar()
        setupRecyclerView()
        setupClickListeners()
        setupObservers()
        
        // Load data
        viewModel.loadAppLimits()
        loadUsageData()
    }
    
    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }
    
    private fun setupRecyclerView() {
        appLimitsAdapter = AppLimitsAdapter(
            onToggleEnabled = { limit, isEnabled ->
                viewModel.toggleLimitEnabled(limit, isEnabled)
            },
            onEditLimit = { limit ->
                showEditLimitDialog(limit)
            },
            onViewDetails = { limit ->
                // Show detailed usage analytics for this app
                viewModel.showAppUsageDetails(limit.limit.packageName)
            }
        )
        
        rvAppLimits.apply {
            layoutManager = LinearLayoutManager(this@AppLimitsActivity)
            adapter = appLimitsAdapter
        }
    }
    
    private fun setupClickListeners() {
        btnAddLimit.setOnClickListener {
            showAddLimitDialog()
        }
        
        btnAddFirstLimit.setOnClickListener {
            showAddLimitDialog()
        }
        
        fabAddLimit.setOnClickListener {
            showAddLimitDialog()
        }
        
        btnBulkEdit.setOnClickListener {
            // TODO: Implement bulk edit functionality
            // Could show a dialog with checkboxes for multiple apps
        }
        
        btnFilter.setOnClickListener {
            // TODO: Implement filter functionality
            // Could filter by: active/inactive, exceeded/not exceeded, app category
        }
    }
    
    private fun setupObservers() {
        viewModel.appLimits.observe(this) { limits ->
            progressLoading.visibility = View.GONE
            
            if (limits.isEmpty()) {
                layoutEmptyState.visibility = View.VISIBLE
                rvAppLimits.visibility = View.GONE
            } else {
                layoutEmptyState.visibility = View.GONE
                rvAppLimits.visibility = View.VISIBLE
                appLimitsAdapter.submitList(limits)
            }
            
            updateSummaryStats(limits)
        }
        
        viewModel.loading.observe(this) { isLoading ->
            progressLoading.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        
        viewModel.error.observe(this) { error ->
            if (error != null) {
                // Show error message
                // TODO: Implement proper error handling
            }
        }
    }
    
    private fun updateSummaryStats(limits: List<AppLimitWithUsage>) {
        val activeLimits = limits.count { it.limit.isEnabled }
        val exceededLimits = limits.count { it.isExceeded() }
        val totalTimeSavedMinutes = limits.sumOf { 
            if (it.limit.isEnabled && !it.isExceeded()) {
                maxOf(0, it.limit.dailyLimitMinutes - it.usageMinutes)
            } else 0
        }
        
        tvActiveLimitsCount.text = activeLimits.toString()
        tvExceededCount.text = exceededLimits.toString()
        
        val hours = totalTimeSavedMinutes / 60
        val minutes = totalTimeSavedMinutes % 60
        tvTimeSaved.text = if (hours > 0) {
            "${hours}h ${minutes}m"
        } else {
            "${minutes}m"
        }
    }
    
    private fun loadUsageData() {
        lifecycleScope.launch {
            try {
                val usageStats = appUsageManager.getTodayUsageStats()
                viewModel.updateUsageData(usageStats)
            } catch (e: Exception) {
                // Handle error
                e.printStackTrace()
            }
        }
    }
    
    private fun showAddLimitDialog() {
        val dialog = AddLimitDialogFragment { packageName: String, appName: String, limitMinutes: Int ->
            viewModel.addAppLimit(packageName, appName, limitMinutes)
        }
        dialog.show(supportFragmentManager, "AddLimitDialog")
    }
    
    private fun showEditLimitDialog(limitWithUsage: AppLimitWithUsage) {
        val dialog = EditLimitDialogFragment(limitWithUsage.limit) { updatedLimit: com.aryanvbw.focus.data.AppLimit ->
            viewModel.updateAppLimit(updatedLimit)
        }
        dialog.show(supportFragmentManager, "EditLimitDialog")
    }
    
    private fun getInstalledApps(): List<ApplicationInfo> {
        return packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
            .filter { it.flags and ApplicationInfo.FLAG_SYSTEM == 0 } // Only user apps
            .sortedBy { packageManager.getApplicationLabel(it).toString() }
    }
}
