package com.aryanvbw.focus.ui.block

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.aryanvbw.focus.databinding.FragmentBlockBinding

class BlockFragment : Fragment() {

    private var _binding: FragmentBlockBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var blockViewModel: BlockViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        blockViewModel = ViewModelProvider(this).get(BlockViewModel::class.java)
        _binding = FragmentBlockBinding.inflate(inflater, container, false)
        
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupUI()
        observeViewModel()
    }
    
    private fun setupUI() {
        // Setup click listeners
        binding.ivBack.setOnClickListener {
            // Navigate back
            requireActivity().onBackPressed()
        }
        
        binding.btnBlockNow.setOnClickListener {
            val customUrl = binding.etCustomUrl.text.toString()
            blockViewModel.blockNow(customUrl)
        }
        
        // Setup toggle listeners
        binding.switchSocialMedia.setOnCheckedChangeListener { _, isChecked ->
            blockViewModel.toggleSocialMedia(isChecked)
        }
        
        binding.switchNews.setOnCheckedChangeListener { _, isChecked ->
            blockViewModel.toggleNews(isChecked)
        }
        
        binding.switchEntertainment.setOnCheckedChangeListener { _, isChecked ->
            blockViewModel.toggleEntertainment(isChecked)
        }
        
        binding.switchShopping.setOnCheckedChangeListener { _, isChecked ->
            blockViewModel.toggleShopping(isChecked)
        }
        
        binding.switchSchedule.setOnCheckedChangeListener { _, isChecked ->
            blockViewModel.toggleScheduledBlocking(isChecked)
        }
    }
    
    private fun observeViewModel() {
        blockViewModel.socialMediaBlocked.observe(viewLifecycleOwner) { isBlocked ->
            binding.switchSocialMedia.isChecked = isBlocked
        }
        
        blockViewModel.newsBlocked.observe(viewLifecycleOwner) { isBlocked ->
            binding.switchNews.isChecked = isBlocked
        }
        
        blockViewModel.entertainmentBlocked.observe(viewLifecycleOwner) { isBlocked ->
            binding.switchEntertainment.isChecked = isBlocked
        }
        
        blockViewModel.shoppingBlocked.observe(viewLifecycleOwner) { isBlocked ->
            binding.switchShopping.isChecked = isBlocked
        }
        
        blockViewModel.scheduledBlockingEnabled.observe(viewLifecycleOwner) { isEnabled ->
            binding.switchSchedule.isChecked = isEnabled
        }
        
        blockViewModel.blockedAttempts.observe(viewLifecycleOwner) { attempts ->
            binding.tvBlockedAttemptsCount.text = attempts.toString()
        }
        
        blockViewModel.blockingMessage.observe(viewLifecycleOwner) { message ->
            if (message.isNotEmpty()) {
                // Show toast or snackbar with message
                // Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}