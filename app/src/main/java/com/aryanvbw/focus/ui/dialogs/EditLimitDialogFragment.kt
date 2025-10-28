package com.aryanvbw.focus.ui.dialogs

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.aryanvbw.focus.R
import com.aryanvbw.focus.data.AppLimit
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import java.util.*

/**
 * Dialog for editing existing app limits
 */
class EditLimitDialogFragment(
    private val appLimit: AppLimit,
    private val onUpdateLimit: (AppLimit) -> Unit
) : DialogFragment() {
    
    private lateinit var tvAppName: TextView
    private lateinit var tvPackageName: TextView
    private lateinit var etLimitMinutes: EditText
    private lateinit var switchEnabled: Switch
    
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = layoutInflater.inflate(R.layout.dialog_edit_limit, null)
        tvAppName = view.findViewById(R.id.tv_app_name)
        tvPackageName = view.findViewById(R.id.tv_package_name)
        etLimitMinutes = view.findViewById(R.id.et_limit_minutes)
        switchEnabled = view.findViewById(R.id.switch_enabled)
        
        setupViews()
        
        return AlertDialog.Builder(requireContext())
            .setTitle("Edit App Limit")
            .setView(view as View)
            .setPositiveButton("Update") { dialog: DialogInterface, which: Int ->
                updateLimit()
            }
            .setNegativeButton("Cancel", null)
            .setNeutralButton("Remove") { dialog: DialogInterface, which: Int ->
                removeLimit()
            }
            .create()
    }
    

    
    private fun setupViews() {
        tvAppName.text = appLimit.appName
        tvPackageName.text = appLimit.packageName
        etLimitMinutes.setText(appLimit.dailyLimitMinutes.toString())
        switchEnabled.isChecked = appLimit.isEnabled
    }
    
    private fun updateLimit() {
        val limitText = etLimitMinutes.text.toString()
        val limitMinutes = limitText.toIntOrNull() ?: appLimit.dailyLimitMinutes
        val isEnabled = switchEnabled.isChecked
        
        if (limitMinutes > 0) {
            val updatedLimit = appLimit.copy(
                dailyLimitMinutes = limitMinutes,
                isEnabled = isEnabled,
                updatedAt = Date()
            )
            onUpdateLimit(updatedLimit)
        }
    }
    
    private fun removeLimit() {
        // To remove, we can set the limit to 0 or use a special flag
        val removedLimit = appLimit.copy(
            isEnabled = false,
            updatedAt = Date()
        )
        onUpdateLimit(removedLimit)
    }
}