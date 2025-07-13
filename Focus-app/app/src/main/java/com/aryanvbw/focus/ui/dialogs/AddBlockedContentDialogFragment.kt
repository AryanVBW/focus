package com.aryanvbw.focus.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.aryanvbw.focus.R
import android.widget.EditText
import android.widget.Spinner

class AddBlockedContentDialogFragment(
    private val onAddContent: (type: String, identifier: String, name: String) -> Unit
) : DialogFragment() {
    
    private lateinit var spinnerContentType: Spinner
    private lateinit var etIdentifier: EditText
    private lateinit var etDisplayName: EditText
    
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = layoutInflater.inflate(R.layout.dialog_add_blocked_content, null)
        spinnerContentType = view.findViewById(R.id.spinner_content_type)
        etIdentifier = view.findViewById(R.id.et_identifier)
        etDisplayName = view.findViewById(R.id.et_display_name)
        
        setupSpinner()
        
        return AlertDialog.Builder(requireContext())
            .setTitle("Add Blocked Content")
            .setView(view)
            .setPositiveButton("Add") { _, _ ->
                val type = if (spinnerContentType.selectedItemPosition == 0) "app" else "website"
                val identifier = etIdentifier.text.toString().trim()
                val name = etDisplayName.text.toString().trim()
                
                if (identifier.isNotEmpty() && name.isNotEmpty()) {
                    onAddContent(type, identifier, name)
                }
            }
            .setNegativeButton("Cancel", null)
            .create()
    }
    
    private fun setupSpinner() {
        val contentTypes = arrayOf("App", "Website")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, contentTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerContentType.adapter = adapter
    }
    

}
