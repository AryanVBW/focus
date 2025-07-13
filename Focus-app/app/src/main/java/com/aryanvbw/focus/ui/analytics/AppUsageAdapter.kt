package com.aryanvbw.focus.ui.analytics

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aryanvbw.focus.databinding.ItemAppUsageBinding
import java.text.SimpleDateFormat
import java.util.*

class AppUsageAdapter(
    private val onItemClick: (AppUsageData) -> Unit
) : RecyclerView.Adapter<AppUsageAdapter.AppUsageViewHolder>() {

    private var appUsageList = emptyList<AppUsageData>()

    fun updateData(newList: List<AppUsageData>) {
        appUsageList = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppUsageViewHolder {
        val binding = ItemAppUsageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AppUsageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AppUsageViewHolder, position: Int) {
        holder.bind(appUsageList[position])
    }

    override fun getItemCount(): Int = appUsageList.size

    inner class AppUsageViewHolder(
        private val binding: ItemAppUsageBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(appUsage: AppUsageData) {
            binding.apply {
                imageAppIcon.setImageDrawable(appUsage.appIcon)
                textAppName.text = appUsage.appName
                textAppCategory.text = appUsage.category
                textUsageTime.text = formatUsageTime(appUsage.usageTimeMinutes)
                textOpenCount.text = "${appUsage.openCount} opens"

                // Set usage time color based on duration
                val usageColor = when {
                    appUsage.usageTimeMinutes > 120 -> android.graphics.Color.parseColor("#EF4444") // Red
                    appUsage.usageTimeMinutes > 60 -> android.graphics.Color.parseColor("#F59E0B") // Orange
                    else -> android.graphics.Color.parseColor("#10B981") // Green
                }
                textUsageTime.setTextColor(usageColor)

                root.setOnClickListener {
                    onItemClick(appUsage)
                }
            }
        }

        private fun formatUsageTime(minutes: Long): String {
            return when {
                minutes < 60 -> "${minutes}m"
                minutes < 1440 -> "${minutes / 60}h ${minutes % 60}m"
                else -> "${minutes / 1440}d ${(minutes % 1440) / 60}h"
            }
        }

        private fun formatLastUsed(timestamp: Long): String {
            val now = System.currentTimeMillis()
            val diff = now - timestamp
            
            return when {
                diff < 60000 -> "Just now"
                diff < 3600000 -> "${diff / 60000}m ago"
                diff < 86400000 -> "${diff / 3600000}h ago"
                diff < 604800000 -> "${diff / 86400000}d ago"
                else -> SimpleDateFormat("MMM dd", Locale.getDefault()).format(Date(timestamp))
            }
        }
    }
}
