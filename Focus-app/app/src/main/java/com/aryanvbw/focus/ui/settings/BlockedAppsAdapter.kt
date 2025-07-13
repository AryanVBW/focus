package com.aryanvbw.focus.ui.settings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView
import com.aryanvbw.focus.R

class BlockedAppsAdapter(
    private val apps: List<BlockedAppInfo>,
    private val onAppBlockChanged: (BlockedAppInfo, Boolean) -> Unit
) : RecyclerView.Adapter<BlockedAppsAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val appIcon: ImageView = view.findViewById(R.id.appIcon)
        val appName: TextView = view.findViewById(R.id.appName)
        val blockSwitch: SwitchCompat = view.findViewById(R.id.blockSwitch)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_blocked_app, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val appInfo = apps[position]
        holder.appName.text = appInfo.appName
        holder.appIcon.setImageDrawable(appInfo.icon)
        
        // Set switch state without triggering listener initially
        holder.blockSwitch.setOnCheckedChangeListener(null)
        holder.blockSwitch.isChecked = appInfo.isBlocked
        
        // Set listener to handle user interaction
        holder.blockSwitch.setOnCheckedChangeListener { _, isChecked ->
            appInfo.isBlocked = isChecked // Update data model
            onAppBlockChanged(appInfo, isChecked) // Notify fragment/activity
        }
    }

    override fun getItemCount(): Int = apps.size
}
