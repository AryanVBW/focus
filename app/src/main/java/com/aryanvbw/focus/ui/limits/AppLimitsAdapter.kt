package com.aryanvbw.focus.ui.limits

import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.aryanvbw.focus.R
import com.aryanvbw.focus.databinding.ItemAppLimitBinding

class AppLimitsAdapter(
    private val onToggleEnabled: (AppLimitWithUsage, Boolean) -> Unit,
    private val onEditLimit: (AppLimitWithUsage) -> Unit,
    private val onViewDetails: (AppLimitWithUsage) -> Unit
) : ListAdapter<AppLimitWithUsage, AppLimitsAdapter.AppLimitViewHolder>(AppLimitDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppLimitViewHolder {
        val binding = ItemAppLimitBinding.inflate(
            LayoutInflater.from(parent.context), 
            parent, 
            false
        )
        return AppLimitViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: AppLimitViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    inner class AppLimitViewHolder(
        private val binding: ItemAppLimitBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(limitWithUsage: AppLimitWithUsage) {
            val limit = limitWithUsage.limit
            val context = binding.root.context
            
            // Set app info
            binding.tvAppName.text = limit.appName
            binding.tvPackageName.text = limit.packageName
            
            // Set app icon
            try {
                val appIcon = context.packageManager.getApplicationIcon(limit.packageName)
                binding.ivAppIcon.setImageDrawable(appIcon)
            } catch (e: PackageManager.NameNotFoundException) {
                binding.ivAppIcon.setImageResource(R.drawable.ic_analytics)
            }
            
            // Set status and toggle
            binding.switchEnabled.isChecked = limit.isEnabled
            binding.switchEnabled.setOnCheckedChangeListener { _, isChecked ->
                onToggleEnabled(limitWithUsage, isChecked)
            }
            
            // Update status badge
            updateStatusBadge(limitWithUsage)
            
            // Set usage information
            val hours = limitWithUsage.usageMinutes / 60
            val minutes = limitWithUsage.usageMinutes % 60
            val limitHours = limit.dailyLimitMinutes / 60
            val limitMinutes = limit.dailyLimitMinutes % 60
            
            val usageText = if (hours > 0) "${hours}h ${minutes}m" else "${minutes}m"
            val limitText = if (limitHours > 0) "${limitHours}h ${limitMinutes}m" else "${limitMinutes}m"
            
            binding.tvUsageTime.text = "$usageText / $limitText"
            
            // Set progress
            val percentage = limitWithUsage.getUsagePercentage()
            binding.progressUsage.progress = percentage
            
            // Set progress color based on usage
            val progressColor = when {
                percentage >= 100 -> ContextCompat.getColor(context, R.color.error)
                percentage >= 80 -> ContextCompat.getColor(context, R.color.warning)
                else -> ContextCompat.getColor(context, R.color.success)
            }
            binding.progressUsage.progressTintList = android.content.res.ColorStateList.valueOf(progressColor)
            
            // Set usage percentage text
            val usagePercentageText = when {
                percentage >= 100 -> "Limit exceeded!"
                percentage >= 80 -> "$percentage% of limit used"
                else -> "$percentage% of limit used"
            }
            binding.tvUsagePercentage.text = usagePercentageText
            binding.tvUsagePercentage.setTextColor(progressColor)
            
            // Set click listeners
            binding.btnEditLimit.setOnClickListener {
                onEditLimit(limitWithUsage)
            }
            
            binding.btnViewDetails.setOnClickListener {
                onViewDetails(limitWithUsage)
            }
        }
        
        private fun updateStatusBadge(limitWithUsage: AppLimitWithUsage) {
            val context = binding.root.context
            val limit = limitWithUsage.limit
            
            when {
                !limit.isEnabled -> {
                    binding.tvStatus.text = "Disabled"
                    binding.cvStatusBadge.setCardBackgroundColor(
                        ContextCompat.getColor(context, R.color.gray_400)
                    )
                }
                limitWithUsage.isExceeded() -> {
                    binding.tvStatus.text = "Exceeded"
                    binding.cvStatusBadge.setCardBackgroundColor(
                        ContextCompat.getColor(context, R.color.error)
                    )
                }
                limitWithUsage.getUsagePercentage() >= 80 -> {
                    binding.tvStatus.text = "Warning"
                    binding.cvStatusBadge.setCardBackgroundColor(
                        ContextCompat.getColor(context, R.color.warning)
                    )
                }
                else -> {
                    binding.tvStatus.text = "Active"
                    binding.cvStatusBadge.setCardBackgroundColor(
                        ContextCompat.getColor(context, R.color.success)
                    )
                }
            }
        }
    }
}

class AppLimitDiffCallback : DiffUtil.ItemCallback<AppLimitWithUsage>() {
    override fun areItemsTheSame(oldItem: AppLimitWithUsage, newItem: AppLimitWithUsage): Boolean {
        return oldItem.limit.packageName == newItem.limit.packageName
    }
    
    override fun areContentsTheSame(oldItem: AppLimitWithUsage, newItem: AppLimitWithUsage): Boolean {
        return oldItem == newItem
    }
}
