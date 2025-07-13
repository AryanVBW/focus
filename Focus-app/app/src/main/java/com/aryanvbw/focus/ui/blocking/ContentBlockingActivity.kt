package com.aryanvbw.focus.ui.blocking

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.aryanvbw.focus.databinding.ActivityContentBlockingBinding
import com.aryanvbw.focus.ui.dialogs.AddBlockedContentDialogFragment

class ContentBlockingActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityContentBlockingBinding
    private val viewModel: ContentBlockingViewModel by viewModels()
    private lateinit var blockedContentAdapter: BlockedContentAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContentBlockingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupToolbar()
        setupRecyclerView()
        setupClickListeners()
        setupObservers()
        
        // Load data
        viewModel.loadBlockedContent()
        viewModel.loadBlockingStats()
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }
    
    private fun setupRecyclerView() {
        blockedContentAdapter = BlockedContentAdapter { blockedItem ->
            viewModel.removeBlockedContent(blockedItem)
        }
        
        binding.rvCustomBlocked.apply {
            layoutManager = LinearLayoutManager(this@ContentBlockingActivity)
            adapter = blockedContentAdapter
        }
    }
    
    private fun setupClickListeners() {
        // Master toggle
        binding.switchMasterBlocking.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setMasterBlockingEnabled(isChecked)
        }
        
        // Category toggles
        binding.switchSocialMedia.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setCategoryBlockingEnabled("social_media", isChecked)
        }
        
        binding.switchEntertainment.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setCategoryBlockingEnabled("entertainment", isChecked)
        }
        
        binding.switchNewsMedia.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setCategoryBlockingEnabled("news_media", isChecked)
        }
        
        binding.switchAdultContent.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setCategoryBlockingEnabled("adult_content", isChecked)
        }
        
        // Add custom block
        binding.btnAddCustomBlock.setOnClickListener {
            showAddBlockedContentDialog()
        }
    }
    
    private fun setupObservers() {
        viewModel.blockingStats.observe(this) { stats ->
            binding.tvBlockedAppsCount.text = stats.blockedAppsCount.toString()
            binding.tvBlockedWebsitesCount.text = stats.blockedWebsitesCount.toString()
            
            val hours = stats.focusTimeMinutes / 60
            val minutes = stats.focusTimeMinutes % 60
            binding.tvFocusTime.text = if (hours > 0) {
                "${hours}h ${minutes}m"
            } else {
                "${minutes}m"
            }
        }
        
        viewModel.blockedContent.observe(this) { blockedItems ->
            blockedContentAdapter.submitList(blockedItems)
        }
        
        viewModel.categorySettings.observe(this) { settings ->
            binding.switchMasterBlocking.isChecked = settings.masterEnabled
            binding.switchSocialMedia.isChecked = settings.socialMediaEnabled
            binding.switchEntertainment.isChecked = settings.entertainmentEnabled
            binding.switchNewsMedia.isChecked = settings.newsMediaEnabled
            binding.switchAdultContent.isChecked = settings.adultContentEnabled
        }
    }
    
    private fun showAddBlockedContentDialog() {
        val dialog = AddBlockedContentDialogFragment { contentType, identifier, name ->
            viewModel.addBlockedContent(contentType, identifier, name)
        }
        dialog.show(supportFragmentManager, "AddBlockedContentDialog")
    }
}
