package com.vanphong.foodnfit.component

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class ScanOverlayView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private val borderPaint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 6f
        isAntiAlias = true
    }

    private val cornerPaint = Paint().apply {
        color = Color.WHITE
        strokeWidth = 8f
        isAntiAlias = true
    }

    private val maskPaint = Paint().apply {
        color = Color.parseColor("#88000000")
    }

    private val linePaint = Paint().apply {
        color = Color.WHITE
        strokeWidth = 2f
        isAntiAlias = true
    }

    private var frameRect: RectF? = null
    private var scanLineY: Float = 0f
    private var direction = 1

    private val animator = ValueAnimator.ofFloat(0f, 1f).apply {
        duration = 2000
        repeatMode = ValueAnimator.RESTART
        repeatCount = ValueAnimator.INFINITE
        addUpdateListener {
            frameRect?.let {
                scanLineY = it.top + (it.height() * animatedFraction)
                invalidate()
            }
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        animator.start()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        animator.cancel()
    }

    override fun onDraw(canvas: Canvas) {
        val width = width
        val height = height

        val frameWidth = (width * 0.7f)
        val frameHeight = frameWidth
        val left = (width - frameWidth) / 2
        val top = (height - frameHeight) / 2
        val right = left + frameWidth
        val bottom = top + frameHeight
        frameRect = RectF(left, top, right, bottom)

        frameRect?.let { rect ->
            canvas.drawRect(0f, 0f, width.toFloat(), rect.top, maskPaint)
            canvas.drawRect(0f, rect.bottom, width.toFloat(), height.toFloat(), maskPaint)
            canvas.drawRect(0f, rect.top, rect.left, rect.bottom, maskPaint)
            canvas.drawRect(rect.right, rect.top, width.toFloat(), rect.bottom, maskPaint)

            canvas.drawRoundRect(rect, 30f, 30f, borderPaint)
            drawCorners(canvas, rect)

            // Line quét
            canvas.drawLine(rect.left, scanLineY, rect.right, scanLineY, linePaint)
        }
    }

    private fun drawCorners(canvas: Canvas, rect: RectF) {
        val cl = 50f
        // TL
        canvas.drawLine(rect.left, rect.top, rect.left + cl, rect.top, cornerPaint)
        canvas.drawLine(rect.left, rect.top, rect.left, rect.top + cl, cornerPaint)
        // TR
        canvas.drawLine(rect.right, rect.top, rect.right - cl, rect.top, cornerPaint)
        canvas.drawLine(rect.right, rect.top, rect.right, rect.top + cl, cornerPaint)
        // BL
        canvas.drawLine(rect.left, rect.bottom, rect.left + cl, rect.bottom, cornerPaint)
        canvas.drawLine(rect.left, rect.bottom, rect.left, rect.bottom - cl, cornerPaint)
        // BR
        canvas.drawLine(rect.right, rect.bottom, rect.right - cl, rect.bottom, cornerPaint)
        canvas.drawLine(rect.right, rect.bottom, rect.right, rect.bottom - cl, cornerPaint)
    }
}
