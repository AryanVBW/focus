package com.aryanvbw.focus.ui.onboarding

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.aryanvbw.focus.databinding.ItemOnboardingPageEnhancedBinding

/**
 * Enhanced onboarding adapter with Lottie animation support
 * Falls back gracefully to static images when Lottie is not available
 */
class EnhancedOnboardingPagerAdapter(
    private val pages: List<OnboardingPage>,
    private val useLottieAnimations: Boolean = true
) : RecyclerView.Adapter<EnhancedOnboardingPagerAdapter.OnboardingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardingViewHolder {
        val binding = ItemOnboardingPageEnhancedBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OnboardingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OnboardingViewHolder, position: Int) {
        holder.bind(pages[position], position)
    }

    override fun getItemCount(): Int = pages.size

    inner class OnboardingViewHolder(
        private val binding: ItemOnboardingPageEnhancedBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(page: OnboardingPage, position: Int) {
            binding.apply {
                // Set text content
                tvTitle.text = page.title
                tvSubtitle.text = page.subtitle
                tvDescription.text = page.description
                
                // Setup animations based on accessibility settings
                val shouldReduceAnimations = AnimationUtils.shouldReduceAnimations(binding.root.context)
                
                // Setup illustration (Lottie or static)
                setupIllustration(position, shouldReduceAnimations)
                
                // Setup feature highlights for specific pages
                setupFeatureHighlights(position)
                
                // Create entrance animations
                if (!shouldReduceAnimations) {
                    createEntranceAnimations()
                } else {
                    // Show all elements immediately for accessibility
                    showAllElementsImmediately()
                }
            }
        }
        
        private fun setupIllustration(position: Int, shouldReduceAnimations: Boolean) {
            binding.apply {
                if (useLottieAnimations && !shouldReduceAnimations) {
                    // Use Lottie animation
                    lottieIllustration.visibility = View.VISIBLE
                    ivIllustration.visibility = View.GONE
                    
                    LottieAnimationHelper.setupAnimation(
                        context = binding.root.context,
                        imageView = lottieIllustration,
                        position = position,
                        shouldReduceAnimations = shouldReduceAnimations
                    )
                    
                    // Apply recommended Lottie properties
                    val properties = LottieAnimationHelper.getRecommendedLottieProperties()
                    lottieIllustration.applyOnboardingProperties(properties)
                    
                } else {
                    // Use static image
                    lottieIllustration.visibility = View.GONE
                    ivIllustration.visibility = View.VISIBLE
                    
                    LottieAnimationHelper.setupAnimation(
                        context = binding.root.context,
                        imageView = ivIllustration,
                        position = position,
                        shouldReduceAnimations = true // Force static for fallback
                    )
                }
            }
        }
        
        private fun setupFeatureHighlights(position: Int) {
            binding.apply {
                // Show feature highlights only for the last page (privacy/features page)
                if (position == 3) { // Privacy page
                    featureHighlights.visibility = View.VISIBLE
                } else {
                    featureHighlights.visibility = View.GONE
                }
            }
        }
        
        private fun createEntranceAnimations() {
            binding.apply {
                val views = mutableListOf<View>()
                
                // Add the appropriate illustration view
                if (lottieIllustration.visibility == View.VISIBLE) {
                    views.add(lottieIllustration)
                } else {
                    views.add(ivIllustration)
                }
                
                views.addAll(listOf(tvTitle, tvSubtitle, tvDescription))
                
                // Add feature highlights if visible
                if (featureHighlights.visibility == View.VISIBLE) {
                    views.add(featureHighlights)
                }
                
                // Create staggered entrance animation
                AnimationUtils.createStaggeredEntranceAnimation(
                    views = views,
                    staggerDelay = 100L
                ) {
                    // Start Lottie animation after entrance if using Lottie
                    if (lottieIllustration.visibility == View.VISIBLE) {
                        LottieAnimationHelper.createEntranceAnimation(lottieIllustration)
                    } else if (ivIllustration.visibility == View.VISIBLE) {
                        // Start breathing animation for static image
                        AnimationUtils.createBreathingAnimation(ivIllustration).start()
                    }
                }.start()
            }
        }
        
        private fun showAllElementsImmediately() {
            binding.apply {
                // Make all elements visible immediately for accessibility
                val views = listOf(
                    lottieIllustration, ivIllustration, tvTitle, 
                    tvSubtitle, tvDescription, featureHighlights
                )
                
                views.forEach { view ->
                    if (view.visibility == View.VISIBLE) {
                        view.alpha = 1f
                        view.translationY = 0f
                        view.scaleX = 1f
                        view.scaleY = 1f
                    }
                }
            }
        }
    }
    
    /**
     * Pauses all Lottie animations (useful when page is not visible)
     */
    fun pauseAnimations() {
        // This would be called by the parent activity/fragment when needed
        // Implementation depends on keeping track of active ViewHolders
    }
    
    /**
     * Resumes all Lottie animations
     */
    fun resumeAnimations() {
        // This would be called by the parent activity/fragment when needed
        // Implementation depends on keeping track of active ViewHolders
    }
}

/**
 * Enhanced OnboardingPage data class with additional properties
 */
data class EnhancedOnboardingPage(
    val title: String,
    val subtitle: String,
    val description: String,
    val imageRes: Int,
    val backgroundColor: Int,
    val lottieAnimationFile: String? = null,
    val features: List<String> = emptyList(),
    val showFeatureHighlights: Boolean = false
)
