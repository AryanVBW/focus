package com.aryanvbw.focus.ui.onboarding

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aryanvbw.focus.databinding.ItemOnboardingPageBinding

class OnboardingPagerAdapter(
    private val pages: List<OnboardingPage>
) : RecyclerView.Adapter<OnboardingPagerAdapter.OnboardingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardingViewHolder {
        val binding = ItemOnboardingPageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OnboardingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OnboardingViewHolder, position: Int) {
        holder.bind(pages[position])
    }

    override fun getItemCount(): Int = pages.size

    class OnboardingViewHolder(
        private val binding: ItemOnboardingPageBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(page: OnboardingPage) {
            binding.apply {
                tvTitle.text = page.title
                tvSubtitle.text = page.subtitle
                tvDescription.text = page.description
                ivIllustration.setImageResource(page.imageRes)
                
                // Add entrance animations
                tvTitle.alpha = 0f
                tvSubtitle.alpha = 0f
                tvDescription.alpha = 0f
                ivIllustration.alpha = 0f
                
                tvTitle.translationY = 50f
                tvSubtitle.translationY = 50f
                tvDescription.translationY = 50f
                ivIllustration.translationY = 100f
                
                // Animate elements in sequence
                ivIllustration.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(600)
                    .setStartDelay(100)
                    .start()
                    
                tvTitle.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(500)
                    .setStartDelay(200)
                    .start()
                    
                tvSubtitle.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(500)
                    .setStartDelay(300)
                    .start()
                    
                tvDescription.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(500)
                    .setStartDelay(400)
                    .start()
            }
        }
    }
}