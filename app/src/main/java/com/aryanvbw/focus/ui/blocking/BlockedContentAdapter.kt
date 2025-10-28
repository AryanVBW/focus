package com.aryanvbw.focus.ui.blocking

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.aryanvbw.focus.R
import com.aryanvbw.focus.data.model.BlockedContent
import com.aryanvbw.focus.databinding.ItemBlockedContentBinding

class BlockedContentAdapter(
    private val onRemoveClick: (BlockedContent) -> Unit
) : ListAdapter<BlockedContent, BlockedContentAdapter.BlockedContentViewHolder>(BlockedContentDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlockedContentViewHolder {
        val binding = ItemBlockedContentBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return BlockedContentViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: BlockedContentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    inner class BlockedContentViewHolder(
        private val binding: ItemBlockedContentBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(blockedContent: BlockedContent) {
            binding.apply {
                tvContentName.text = blockedContent.displayName
                tvContentDescription.text = if (blockedContent.type == "app") "App" else "Website"
                
                // Set icon based on type
                val iconRes = if (blockedContent.type == "app") {
                    R.drawable.ic_app_blocking
                } else {
                    R.drawable.ic_website_blocking
                }
                ivTypeIcon.setImageResource(iconRes)
                
                // Set block count (placeholder for now)
                tvBlockCount.text = "0"
                
                // Handle remove button click
                btnRemove.setOnClickListener {
                    onRemoveClick(blockedContent)
                }
                
                // Handle toggle click
                root.setOnClickListener {
                    // Toggle functionality could be added here
                }
            }
        }
    }
    
    private class BlockedContentDiffCallback : DiffUtil.ItemCallback<BlockedContent>() {
        override fun areItemsTheSame(oldItem: BlockedContent, newItem: BlockedContent): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: BlockedContent, newItem: BlockedContent): Boolean {
            return oldItem == newItem
        }
    }
}
