package com.aryanvbw.focus.ui.dialogs

import android.app.Dialog
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.aryanvbw.focus.R
import android.widget.EditText
import android.widget.Spinner

/**
 * Dialog for adding new app limits
 */
class AddLimitDialogFragment(
    private val onAddLimit: (packageName: String, appName: String, limitMinutes: Int) -> Unit
) : DialogFragment() {
    
    private lateinit var installedApps: List<ApplicationInfo>
    private lateinit var spinnerApp: Spinner
    private lateinit var etLimitMinutes: EditText
    
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = layoutInflater.inflate(R.layout.dialog_add_limit, null)
        spinnerApp = view.findViewById(R.id.spinner_app)
        etLimitMinutes = view.findViewById(R.id.et_limit_minutes)
        
        loadInstalledApps()
        setupAppSpinner()
        setupLimitInput()
        
        return AlertDialog.Builder(requireContext())
            .setTitle("Add App Limit")
            .setView(view)
            .setPositiveButton("Add") { _, _ ->
                addLimit()
            }
            .setNegativeButton("Cancel", null)
            .create()
    }
    

    
    private fun loadInstalledApps() {
        val packageManager = requireContext().packageManager
        installedApps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
            .filter { it.flags and ApplicationInfo.FLAG_SYSTEM == 0 } // Only user apps
            .sortedBy { packageManager.getApplicationLabel(it).toString() }
    }
    
    private fun setupAppSpinner() {
        val appNames = installedApps.map { app ->
            requireContext().packageManager.getApplicationLabel(app).toString()
        }
        
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            appNames
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerApp.adapter = adapter
    }
    
    private fun setupLimitInput() {
        // Set default limit to 60 minutes
        etLimitMinutes.setText("60")
    }
    
    private fun addLimit() {
        val selectedAppIndex = spinnerApp.selectedItemPosition
        if (selectedAppIndex >= 0 && selectedAppIndex < installedApps.size) {
            val selectedApp = installedApps[selectedAppIndex]
            val packageName = selectedApp.packageName
            val appName = requireContext().packageManager.getApplicationLabel(selectedApp).toString()
            
            val limitText = etLimitMinutes.text.toString()
            val limitMinutes = limitText.toIntOrNull() ?: 60
            
            if (limitMinutes > 0) {
                onAddLimit(packageName, appName, limitMinutes)
            }
        }
    }
}