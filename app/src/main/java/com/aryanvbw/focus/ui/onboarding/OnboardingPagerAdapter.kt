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

                // Create staggered entrance animation for all elements
                val views = listOf(ivIllustration, tvTitle, tvSubtitle, tvDescription)
                AnimationUtils.createStaggeredEntranceAnimation(
                    views = views,
                    staggerDelay = 100L
                ) {
                    // Start breathing animation for illustration after entrance
                    if (!AnimationUtils.shouldReduceAnimations(binding.root.context)) {
                        AnimationUtils.createBreathingAnimation(ivIllustration).start()
                    }
                }.start()
            }
        }
    }
}