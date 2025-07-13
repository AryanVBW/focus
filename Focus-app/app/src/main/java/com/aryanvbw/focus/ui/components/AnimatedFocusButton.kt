package com.aryanvbw.focus.ui.components

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.content.ContextCompat
import com.aryanvbw.focus.R
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.PI
import kotlin.math.abs

class AnimatedFocusButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var isActive = false
    private var pulseScale1 = 1f
    private var pulseScale2 = 1f
    private var pulseAlpha1 = 0.6f
    private var pulseAlpha2 = 0.4f
    private var waveOffset1 = 0f
    private var waveOffset2 = 0f
    private var waveOffset3 = 0f
    
    private val buttonPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
    
    private val ringPaint1 = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 6f
    }
    
    private val ringPaint2 = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 4f
    }
    
    private val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#20000000")
        maskFilter = BlurMaskFilter(20f, BlurMaskFilter.Blur.NORMAL)
    }
    
    private var centerX = 0f
    private var centerY = 0f
    private var buttonRadius = 0f
    
    private val organicPath = Path()
    private val shadowPath = Path()
    private val wavePath1 = Path()
    private val wavePath2 = Path()
    private val wavePath3 = Path()
    
    private val wavePaint1 = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 3f
        strokeCap = Paint.Cap.ROUND
    }
    
    private val wavePaint2 = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 2f
        strokeCap = Paint.Cap.ROUND
    }
    
    private val wavePaint3 = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 1.5f
        strokeCap = Paint.Cap.ROUND
    }
    
    private var pulseAnimator1: AnimatorSet? = null
    private var pulseAnimator2: AnimatorSet? = null
    private var waveAnimator: AnimatorSet? = null
    
    var onClickListener: ((Boolean) -> Unit)? = null
    
    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
        updateColors()
        
        setOnClickListener {
            isActive = !isActive
            updateColors()
            onClickListener?.invoke(isActive)
            
            // Add click animation
            animate()
                .scaleX(0.95f)
                .scaleY(0.95f)
                .setDuration(100)
                .withEndAction {
                    animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(100)
                        .start()
                }
                .start()
        }
    }
    
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        centerX = w / 2f
        centerY = h / 2f
        buttonRadius = minOf(w, h) / 4f
        
        createOrganicShape()
        createWaveStructures()
        startPulseAnimation()
        startWaveAnimation()
    }
    
    private fun createOrganicShape() {
        organicPath.reset()
        shadowPath.reset()
        
        // Create an intricate loop-based focus icon design
        val centerRadius = buttonRadius * 0.6f
        
        // Create the main focus loop structure
        organicPath.addCircle(centerX, centerY, centerRadius, Path.Direction.CW)
        
        // Add interconnected loops around the center
        val loopRadius = centerRadius * 0.4f
        val loopDistance = centerRadius * 1.3f
        
        // Create 4 interconnected loops positioned around the center
        for (i in 0 until 4) {
            val angle = (i * PI / 2).toFloat()
            val loopX = centerX + cos(angle) * loopDistance
            val loopY = centerY + sin(angle) * loopDistance
            
            // Create flowing loop shapes
            val loopPath = Path()
            createFlowingLoop(loopPath, loopX, loopY, loopRadius, angle)
            organicPath.op(loopPath, Path.Op.UNION)
        }
        
        // Add connecting bridges between loops
        for (i in 0 until 4) {
            val angle1 = (i * PI / 2).toFloat()
            val angle2 = ((i + 1) * PI / 2).toFloat()
            
            val x1 = centerX + cos(angle1) * loopDistance * 0.7f
            val y1 = centerY + sin(angle1) * loopDistance * 0.7f
            val x2 = centerX + cos(angle2) * loopDistance * 0.7f
            val y2 = centerY + sin(angle2) * loopDistance * 0.7f
            
            val bridgePath = Path()
            createFlowingBridge(bridgePath, x1, y1, x2, y2, loopRadius * 0.3f)
            organicPath.op(bridgePath, Path.Op.UNION)
        }
        
        // Create shadow path (slightly offset)
        shadowPath.set(organicPath)
        val shadowMatrix = Matrix()
        shadowMatrix.setTranslate(0f, 8f)
        shadowPath.transform(shadowMatrix)
    }
    
    private fun createFlowingLoop(path: Path, centerX: Float, centerY: Float, radius: Float, rotation: Float) {
        val points = 12
        val pointsList = mutableListOf<PointF>()
        
        // Create organic loop shape with flowing variations
        for (i in 0 until points) {
            val angle = (i * 2 * PI / points).toFloat() + rotation
            val radiusVariation = 1.0f + 0.3f * sin(angle * 3) + 0.2f * cos(angle * 5)
            val currentRadius = radius * radiusVariation
            
            val x = centerX + cos(angle) * currentRadius
            val y = centerY + sin(angle) * currentRadius
            pointsList.add(PointF(x, y))
        }
        
        // Create smooth curves
        path.moveTo(pointsList[0].x, pointsList[0].y)
        for (i in 0 until points) {
            val current = pointsList[i]
            val next = pointsList[(i + 1) % points]
            val control1X = current.x + (next.x - current.x) * 0.5f
            val control1Y = current.y + (next.y - current.y) * 0.5f
            path.quadTo(control1X, control1Y, next.x, next.y)
        }
        path.close()
    }
    
    private fun createFlowingBridge(path: Path, x1: Float, y1: Float, x2: Float, y2: Float, width: Float) {
        val dx = x2 - x1
        val dy = y2 - y1
        val length = kotlin.math.sqrt(dx * dx + dy * dy)
        val normalX = -dy / length * width
        val normalY = dx / length * width
        
        // Create flowing bridge with organic curves
        val midX = (x1 + x2) / 2
        val midY = (y1 + y2) / 2
        val curve = width * 0.5f
        
        path.moveTo(x1 + normalX, y1 + normalY)
        path.quadTo(midX + normalX + curve, midY + normalY, x2 + normalX, y2 + normalY)
        path.lineTo(x2 - normalX, y2 - normalY)
        path.quadTo(midX - normalX - curve, midY - normalY, x1 - normalX, y1 - normalY)
        path.close()
    }
    
    private fun createWaveStructures() {
        wavePath1.reset()
        wavePath2.reset()
        wavePath3.reset()
        
        // Create flowing wave patterns that complement the loop structure
        createFlowingWave(wavePath1, buttonRadius * 1.6f, 12, 0.2f, waveOffset1, 0f)
        createFlowingWave(wavePath2, buttonRadius * 2.1f, 8, 0.25f, waveOffset2, PI.toFloat() / 3)
        createFlowingWave(wavePath3, buttonRadius * 2.6f, 6, 0.3f, waveOffset3, PI.toFloat() / 6)
    }
    
    private fun createFlowingWave(path: Path, baseRadius: Float, frequency: Int, amplitude: Float, offset: Float, phaseShift: Float) {
        path.reset()
        
        val points = 72
        val pointsList = mutableListOf<PointF>()
        
        // Generate wave points with organic flowing variations
        for (i in 0..points) {
            val angle = (i * 2 * PI / points).toFloat()
            
            // Create multiple wave harmonics for more complex patterns
            val primaryWave = sin(angle * frequency + offset + phaseShift) * amplitude
            val secondaryWave = sin(angle * frequency * 2.5f + offset * 1.3f) * amplitude * 0.4f
            val tertiaryWave = cos(angle * frequency * 0.7f + offset * 0.8f) * amplitude * 0.6f
            
            val totalWave = primaryWave + secondaryWave + tertiaryWave
            val radius = baseRadius + baseRadius * totalWave
            
            val x = centerX + cos(angle) * radius
            val y = centerY + sin(angle) * radius
            pointsList.add(PointF(x, y))
        }
        
        // Create smooth flowing curves between points
        if (pointsList.isNotEmpty()) {
            path.moveTo(pointsList[0].x, pointsList[0].y)
            
            for (i in 0 until pointsList.size - 1) {
                val current = pointsList[i]
                val next = pointsList[i + 1]
                
                // Use quadratic curves for smoother wave flow
                val controlX = (current.x + next.x) / 2 + sin((i * 2 * PI / pointsList.size + offset).toFloat()) * 5f
                val controlY = (current.y + next.y) / 2 + cos((i * 2 * PI / pointsList.size + offset).toFloat()) * 5f
                
                path.quadTo(controlX, controlY, next.x, next.y)
            }
            
            // Close the path smoothly
            val last = pointsList.last()
            val first = pointsList.first()
            val controlX = (last.x + first.x) / 2
            val controlY = (last.y + first.y) / 2
            path.quadTo(controlX, controlY, first.x, first.y)
        }
        
        path.close()
    }
    
    private fun updateColors() {
        val primaryColor = ContextCompat.getColor(context, R.color.primary)
        val primaryLight = ContextCompat.getColor(context, R.color.primary_light)
        val focusColor = ContextCompat.getColor(context, R.color.focus_mode)
        
        if (isActive) {
            buttonPaint.color = focusColor
            ringPaint1.color = focusColor
            ringPaint2.color = focusColor
            wavePaint1.color = focusColor
            wavePaint2.color = focusColor
            wavePaint3.color = primaryLight
        } else {
            buttonPaint.color = primaryColor
            ringPaint1.color = primaryColor
            ringPaint2.color = primaryLight
            wavePaint1.color = primaryColor
            wavePaint2.color = primaryLight
            wavePaint3.color = primaryColor
        }
    }
    
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        // Draw flowing wave structures around the button
        if (isActive) {
            // Update wave paths with current animation offsets
            createWaveStructures()
            
            // Draw waves with varying opacity for depth effect
            wavePaint3.alpha = 30
            canvas.drawPath(wavePath3, wavePaint3)
            
            wavePaint2.alpha = 50
            canvas.drawPath(wavePath2, wavePaint2)
            
            wavePaint1.alpha = 70
            canvas.drawPath(wavePath1, wavePaint1)
        }
        
        // Draw shadow
        canvas.drawPath(shadowPath, shadowPaint)
        
        // Draw pulsating rings
        if (isActive) {
            ringPaint1.alpha = (pulseAlpha1 * 255).toInt()
            ringPaint2.alpha = (pulseAlpha2 * 255).toInt()
            
            canvas.drawCircle(
                centerX, centerY,
                buttonRadius * 1.8f * pulseScale1,
                ringPaint1
            )
            
            canvas.drawCircle(
                centerX, centerY,
                buttonRadius * 2.2f * pulseScale2,
                ringPaint2
            )
        }
        
        // Draw main organic button
        canvas.drawPath(organicPath, buttonPaint)
        
        // Draw inner highlight
        val highlightPaint = Paint(buttonPaint).apply {
            color = Color.WHITE
            alpha = 40
        }
        
        val highlightPath = Path(organicPath)
        val matrix = Matrix()
        matrix.setScale(0.7f, 0.7f, centerX, centerY)
        highlightPath.transform(matrix)
        canvas.drawPath(highlightPath, highlightPaint)
    }
    
    private fun startPulseAnimation() {
        stopPulseAnimation()
        
        if (isActive) {
            // First pulse animation
            val scaleAnim1 = ObjectAnimator.ofFloat(this, "pulseScale1", 1f, 1.3f).apply {
                duration = 1500
                repeatCount = ValueAnimator.INFINITE
                repeatMode = ValueAnimator.REVERSE
                interpolator = AccelerateDecelerateInterpolator()
            }
            
            val alphaAnim1 = ObjectAnimator.ofFloat(this, "pulseAlpha1", 0.6f, 0.1f).apply {
                duration = 1500
                repeatCount = ValueAnimator.INFINITE
                repeatMode = ValueAnimator.REVERSE
                interpolator = AccelerateDecelerateInterpolator()
            }
            
            pulseAnimator1 = AnimatorSet().apply {
                playTogether(scaleAnim1, alphaAnim1)
                start()
            }
            
            // Second pulse animation (delayed)
            val scaleAnim2 = ObjectAnimator.ofFloat(this, "pulseScale2", 1f, 1.5f).apply {
                duration = 2000
                repeatCount = ValueAnimator.INFINITE
                repeatMode = ValueAnimator.REVERSE
                interpolator = AccelerateDecelerateInterpolator()
                startDelay = 750
            }
            
            val alphaAnim2 = ObjectAnimator.ofFloat(this, "pulseAlpha2", 0.4f, 0.05f).apply {
                duration = 2000
                repeatCount = ValueAnimator.INFINITE
                repeatMode = ValueAnimator.REVERSE
                interpolator = AccelerateDecelerateInterpolator()
                startDelay = 750
            }
            
            pulseAnimator2 = AnimatorSet().apply {
                playTogether(scaleAnim2, alphaAnim2)
                start()
            }
        }
    }
    
    private fun startWaveAnimation() {
        stopWaveAnimation()
        
        if (isActive) {
            // Wave animation for flowing effect
            val waveAnim1 = ObjectAnimator.ofFloat(this, "waveOffset1", 0f, (2 * PI).toFloat()).apply {
                duration = 3000
                repeatCount = ValueAnimator.INFINITE
                interpolator = AccelerateDecelerateInterpolator()
            }
            
            val waveAnim2 = ObjectAnimator.ofFloat(this, "waveOffset2", 0f, (2 * PI).toFloat()).apply {
                duration = 4500
                repeatCount = ValueAnimator.INFINITE
                interpolator = AccelerateDecelerateInterpolator()
                startDelay = 500
            }
            
            val waveAnim3 = ObjectAnimator.ofFloat(this, "waveOffset3", 0f, (2 * PI).toFloat()).apply {
                duration = 6000
                repeatCount = ValueAnimator.INFINITE
                interpolator = AccelerateDecelerateInterpolator()
                startDelay = 1000
            }
            
            waveAnimator = AnimatorSet().apply {
                playTogether(waveAnim1, waveAnim2, waveAnim3)
                start()
            }
        }
    }
    
    private fun stopPulseAnimation() {
        pulseAnimator1?.cancel()
        pulseAnimator2?.cancel()
        pulseAnimator1 = null
        pulseAnimator2 = null
    }
    
    private fun stopWaveAnimation() {
        waveAnimator?.cancel()
        waveAnimator = null
    }
    
    fun setActive(active: Boolean) {
        if (isActive != active) {
            isActive = active
            updateColors()
            if (isActive) {
                startPulseAnimation()
                startWaveAnimation()
            } else {
                stopPulseAnimation()
                stopWaveAnimation()
            }
            invalidate()
        }
    }
    
    fun setPulseScale1(scale: Float) {
        pulseScale1 = scale
        invalidate()
    }
    
    fun setPulseScale2(scale: Float) {
        pulseScale2 = scale
        invalidate()
    }
    
    fun setPulseAlpha1(alpha: Float) {
        pulseAlpha1 = alpha
        invalidate()
    }
    
    fun setPulseAlpha2(alpha: Float) {
        pulseAlpha2 = alpha
        invalidate()
    }
    
    fun setWaveOffset1(offset: Float) {
        waveOffset1 = offset
        invalidate()
    }
    
    fun setWaveOffset2(offset: Float) {
        waveOffset2 = offset
        invalidate()
    }
    
    fun setWaveOffset3(offset: Float) {
        waveOffset3 = offset
        invalidate()
    }
    
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopPulseAnimation()
        stopWaveAnimation()
    }
}