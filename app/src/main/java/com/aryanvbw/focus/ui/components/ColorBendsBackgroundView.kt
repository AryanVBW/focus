package com.aryanvbw.focus.ui.components

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.content.ContextCompat
import com.aryanvbw.focus.R
import kotlin.math.*

/**
 * Custom view that creates an animated color-bends background effect
 * with flowing gradients and smooth color transitions
 */
class ColorBendsBackgroundView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var animator: ValueAnimator? = null
    private var animationProgress = 0f
    
    // Color sets for different gradient states
    private val colorSets = listOf(
        intArrayOf(
            Color.parseColor("#667EEA"),
            Color.parseColor("#764BA2"),
            Color.parseColor("#F093FB")
        ),
        intArrayOf(
            Color.parseColor("#4FACFE"),
            Color.parseColor("#00F2FE"),
            Color.parseColor("#43E97B")
        ),
        intArrayOf(
            Color.parseColor("#FA709A"),
            Color.parseColor("#FEE140"),
            Color.parseColor("#FA8BFF")
        ),
        intArrayOf(
            Color.parseColor("#A8EDEA"),
            Color.parseColor("#FED6E3"),
            Color.parseColor("#D299C2")
        )
    )
    
    // Animation parameters
    private var gradientAngle = 45f
    private var waveOffset = 0f
    private val waveAmplitude = 0.3f
    private val waveFrequency = 2f
    
    init {
        startAnimation()
    }
    
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        val width = width.toFloat()
        val height = height.toFloat()
        
        if (width <= 0 || height <= 0) return
        
        // Create multiple gradient layers with different angles and colors
        drawGradientLayer(canvas, width, height, 0, 0.7f)
        drawGradientLayer(canvas, width, height, 1, 0.4f)
        drawGradientLayer(canvas, width, height, 2, 0.3f)
        drawGradientLayer(canvas, width, height, 3, 0.2f)
    }
    
    private fun drawGradientLayer(
        canvas: Canvas,
        width: Float,
        height: Float,
        colorSetIndex: Int,
        alpha: Float
    ) {
        val colors = colorSets[colorSetIndex % colorSets.size]
        val layerOffset = colorSetIndex * 90f
        val currentAngle = gradientAngle + layerOffset + (animationProgress * 360f)
        
        // Calculate gradient positions with wave effect
        val waveInfluence = sin(animationProgress * waveFrequency * 2 * PI + colorSetIndex).toFloat()
        val dynamicOffset = waveOffset + waveInfluence * waveAmplitude
        
        // Convert angle to radians and calculate gradient vector
        val angleRad = Math.toRadians(currentAngle.toDouble())
        val diagonal = sqrt(width * width + height * height)
        
        val centerX = width / 2
        val centerY = height / 2
        val offsetDistance = diagonal * dynamicOffset
        
        val startX = centerX - cos(angleRad).toFloat() * offsetDistance
        val startY = centerY - sin(angleRad).toFloat() * offsetDistance
        val endX = centerX + cos(angleRad).toFloat() * offsetDistance
        val endY = centerY + sin(angleRad).toFloat() * offsetDistance
        
        // Create animated colors by interpolating between color sets
        val animatedColors = createAnimatedColors(colors, animationProgress)
        
        // Create linear gradient
        val gradient = LinearGradient(
            startX, startY, endX, endY,
            animatedColors,
            null,
            Shader.TileMode.CLAMP
        )
        
        paint.shader = gradient
        paint.alpha = (alpha * 255).toInt()
        
        canvas.drawRect(0f, 0f, width, height, paint)
    }
    
    private fun createAnimatedColors(baseColors: IntArray, progress: Float): IntArray {
        val nextColorSetIndex = (colorSets.indexOf(baseColors) + 1) % colorSets.size
        val nextColors = colorSets[nextColorSetIndex]
        
        // Smooth interpolation between color sets
        val interpolationFactor = (sin(progress * PI * 2) + 1) / 2
        
        return baseColors.mapIndexed { index, color ->
            val nextColor = nextColors[index % nextColors.size]
            interpolateColor(color, nextColor, interpolationFactor.toFloat())
        }.toIntArray()
    }
    
    private fun interpolateColor(color1: Int, color2: Int, factor: Float): Int {
        val r1 = Color.red(color1)
        val g1 = Color.green(color1)
        val b1 = Color.blue(color1)
        val a1 = Color.alpha(color1)
        
        val r2 = Color.red(color2)
        val g2 = Color.green(color2)
        val b2 = Color.blue(color2)
        val a2 = Color.alpha(color2)
        
        val r = (r1 + (r2 - r1) * factor).toInt().coerceIn(0, 255)
        val g = (g1 + (g2 - g1) * factor).toInt().coerceIn(0, 255)
        val b = (b1 + (b2 - b1) * factor).toInt().coerceIn(0, 255)
        val a = (a1 + (a2 - a1) * factor).toInt().coerceIn(0, 255)
        
        return Color.argb(a, r, g, b)
    }
    
    private fun startAnimation() {
        animator?.cancel()
        
        animator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 15000L // 15 seconds for full cycle
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
            interpolator = LinearInterpolator()
            
            addUpdateListener { animation ->
                animationProgress = animation.animatedValue as Float
                
                // Update gradient angle and wave offset
                gradientAngle = animationProgress * 360f
                waveOffset = sin(animationProgress * PI * 4).toFloat() * 0.2f
                
                invalidate()
            }
        }
        
        animator?.start()
    }
    
    fun pauseAnimation() {
        animator?.pause()
    }
    
    fun resumeAnimation() {
        animator?.resume()
    }
    
    fun stopAnimation() {
        animator?.cancel()
        animator = null
    }
    
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopAnimation()
    }
    
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (animator == null) {
            startAnimation()
        }
    }
}
