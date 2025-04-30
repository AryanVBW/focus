package com.focus.app.ui

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.accessibility.AccessibilityManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.focus.app.R
import com.focus.app.databinding.ActivityMainBinding
import com.focus.app.service.FocusAccessibilityService
import com.focus.app.util.AppSettings
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import android.util.Log

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var settings: AppSettings

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        settings = AppSettings(this)

        setupNavigation()
        
        // Check for permissions and navigate accordingly
        if (isAccessibilityServiceEnabled()) {
            // If permission is already granted, navigate to home and enable service
            settings.setServiceEnabled(true)
            findNavController(R.id.nav_host_fragment).navigate(R.id.navigation_home)
        } else {
            // If permission is not granted, navigate to permission screen
            findNavController(R.id.nav_host_fragment).navigate(R.id.navigation_permission_accessibility)
        }
    }

    private fun setupNavigation() {
        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment)

        // Define top-level destinations for AppBarConfiguration
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_stats, R.id.navigation_settings
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)

        // Custom listener to handle navigation with state saving
        navView.setOnItemSelectedListener { item ->
            val currentDestinationId = navController.currentDestination?.id
            if (item.itemId == currentDestinationId) {
                // Item is already selected, do nothing or handle reselection below
                false // Event not consumed (allows reselection listener to trigger if needed)
            } else {
                val builder = NavOptions.Builder()
                    // Pop up to the start destination saving state
                    .setPopUpTo(navController.graph.startDestinationId, saveState = true, inclusive = false)
                    // Restore state when navigating back
                    .setRestoreState(true)
                    // Avoid multiple copies of the same destination
                    .setLaunchSingleTop(true)

                val options = builder.build()

                try {
                    navController.navigate(item.itemId, null, options)
                    true // Handled
                } catch (e: IllegalArgumentException) {
                    Log.e("MainActivity", "Navigation failed for item ${item.title}", e)
                    false // Not handled
                }
            }
        }

        // Optional: Handle reselection (tapping the already active item)
        navView.setOnItemReselectedListener { item ->
            // When reselecting, pop the back stack associated with that tab to its start destination (the item itself)
            val builder = NavOptions.Builder()
                .setPopUpTo(item.itemId, inclusive = true) // Pop including the item itself
                .setLaunchSingleTop(true)
            val options = builder.build()
            try {
                // Navigate again to clear the stack above it
                navController.navigate(item.itemId, null, options)
            } catch (e: IllegalArgumentException) {
                 // Can happen if the item ID isn't a NavDestination - ignore
                 Log.w("MainActivity", "Reselection failed for item ${item.title}", e)
            }
        }

        // Keep NavView selection in sync with NavController state
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val menu = navView.menu
            for (i in 0 until menu.size()) {
                val menuItem = menu.getItem(i)
                if (menuItem.itemId == destination.id) {
                    menuItem.isChecked = true
                    break
                }
            }
        }
    }

    private fun checkPermissions() {
        // Check if accessibility service is enabled
        if (!isAccessibilityServiceEnabled()) {
            // Navigate to dedicated permission screen
            findNavController(R.id.nav_host_fragment).navigate(R.id.navigation_permission_accessibility)
            return
        }

        // Check if usage stats permission is granted
        if (!isUsageStatsPermissionGranted()) {
            showPermissionSnackbar(
                getString(R.string.onboarding_usage_stats),
                View.OnClickListener {
                    openUsageStatsSettings()
                }
            )
            return
        }

        // If all permissions are granted, enable the service
        settings.setServiceEnabled(true)
    }

    private fun showPermissionSnackbar(message: String, clickListener: View.OnClickListener) {
        Snackbar.make(
            binding.root,
            message,
            Snackbar.LENGTH_INDEFINITE
        )
            .setAction(R.string.action_grant_permission, clickListener)
            .setActionTextColor(ContextCompat.getColor(this, R.color.primary))
            .show()
    }

    private fun isAccessibilityServiceEnabled(): Boolean {
        val accessibilityManager = getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        val enabledServices = accessibilityManager.getEnabledAccessibilityServiceList(
            AccessibilityServiceInfo.FEEDBACK_ALL_MASK
        )
        
        return enabledServices.any {
            it.resolveInfo.serviceInfo.packageName == packageName &&
            it.resolveInfo.serviceInfo.name == FocusAccessibilityService::class.java.name
        }
    }

    private fun isUsageStatsPermissionGranted(): Boolean {
        val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            appOps.unsafeCheckOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(),
                packageName
            )
        } else {
            @Suppress("DEPRECATION")
            appOps.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(),
                packageName
            )
        }
        
        return mode == AppOpsManager.MODE_ALLOWED
    }

    private fun openAccessibilitySettings() {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        startActivity(intent)
    }

    private fun openUsageStatsSettings() {
        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                findNavController(R.id.nav_host_fragment).navigate(R.id.navigation_settings)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
