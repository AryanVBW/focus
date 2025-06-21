package com.aryanvbw.focus.ui.settings

import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.TimePicker
import androidx.appcompat.app.AlertDialog
import com.aryanvbw.focus.R
import com.aryanvbw.focus.util.AppSettings
import java.util.*

class ScheduleConfigDialog(private val context: Context) {
    
    private lateinit var settings: AppSettings
    private lateinit var dialog: AlertDialog
    private lateinit var startTimeText: TextView
    private lateinit var endTimeText: TextView
    private lateinit var daysContainer: LinearLayout
    private val dayCheckboxes = mutableListOf<CheckBox>()
    
    private var startHour = 9
    private var startMinute = 0
    private var endHour = 17
    private var endMinute = 0
    
    fun show() {
        settings = AppSettings(context)
        
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_schedule_config, null)
        setupViews(view)
        loadCurrentSettings()
        
        dialog = AlertDialog.Builder(context)
            .setTitle("Configure Focus Schedule")
            .setView(view)
            .setPositiveButton("Save") { _, _ ->
                saveSettings()
            }
            .setNegativeButton("Cancel", null)
            .create()
            
        dialog.show()
    }
    
    private fun setupViews(view: android.view.View) {
        startTimeText = view.findViewById(R.id.text_start_time)
        endTimeText = view.findViewById(R.id.text_end_time)
        daysContainer = view.findViewById(R.id.container_days)
        
        // Set up time pickers
        startTimeText.setOnClickListener {
            showTimePicker(true)
        }
        
        endTimeText.setOnClickListener {
            showTimePicker(false)
        }
        
        // Set up day checkboxes
        setupDayCheckboxes()
    }
    
    private fun setupDayCheckboxes() {
        val days = arrayOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
        
        for (day in days) {
            val checkbox = CheckBox(context)
            checkbox.text = day
            checkbox.tag = day
            dayCheckboxes.add(checkbox)
            daysContainer.addView(checkbox)
        }
    }
    
    private fun loadCurrentSettings() {
        val prefs = settings.getPreferences()
        
        // Load start time
        startHour = prefs.getInt("schedule_start_hour", 9)
        startMinute = prefs.getInt("schedule_start_minute", 0)
        updateTimeDisplay(true)
        
        // Load end time
        endHour = prefs.getInt("schedule_end_hour", 17)
        endMinute = prefs.getInt("schedule_end_minute", 0)
        updateTimeDisplay(false)
        
        // Load selected days
        val selectedDays = prefs.getStringSet("schedule_days", setOf("Mon", "Tue", "Wed", "Thu", "Fri"))
        for (checkbox in dayCheckboxes) {
            checkbox.isChecked = selectedDays?.contains(checkbox.tag as String) == true
        }
    }
    
    private fun showTimePicker(isStartTime: Boolean) {
        val currentHour = if (isStartTime) startHour else endHour
        val currentMinute = if (isStartTime) startMinute else endMinute
        
        val picker = TimePickerDialog(context, { _, hour, minute ->
            if (isStartTime) {
                startHour = hour
                startMinute = minute
            } else {
                endHour = hour
                endMinute = minute
            }
            updateTimeDisplay(isStartTime)
        }, currentHour, currentMinute, false)
        
        picker.show()
    }
    
    private fun updateTimeDisplay(isStartTime: Boolean) {
        val hour = if (isStartTime) startHour else endHour
        val minute = if (isStartTime) startMinute else endMinute
        
        val timeString = String.format("%02d:%02d", hour, minute)
        
        if (isStartTime) {
            startTimeText.text = timeString
        } else {
            endTimeText.text = timeString
        }
    }
    
    private fun saveSettings() {
        val prefs = settings.getPreferences()
        val editor = prefs.edit()
        
        // Save times
        editor.putInt("schedule_start_hour", startHour)
        editor.putInt("schedule_start_minute", startMinute)
        editor.putInt("schedule_end_hour", endHour)
        editor.putInt("schedule_end_minute", endMinute)
        
        // Save selected days
        val selectedDays = mutableSetOf<String>()
        for (checkbox in dayCheckboxes) {
            if (checkbox.isChecked) {
                selectedDays.add(checkbox.tag as String)
            }
        }
        editor.putStringSet("schedule_days", selectedDays)
        
        editor.apply()
    }
}
