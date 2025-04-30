package com.vanphong.foodnfit.component

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.OverScroller
import kotlin.math.abs
import kotlin.math.roundToInt

class RulerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private val shortMarkPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.GRAY
        strokeWidth = 4f
    }

    private val centerMarkPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.RED
        strokeWidth = 6f
    }

    private var scrollOffset = 0f
    private var lastTouchX = 0f

    private val markSpacing = 30f // spacing between marks
    private val totalMarks = 200

    private val shortMarkHeight = 30f
    private val longMarkHeight = 60f

    var onValueChanged: ((Int) -> Unit)? = null

    private var scroller = OverScroller(context)
    private var velocityTracker: VelocityTracker? = null
    private val flingRunner = object : Runnable {
        override fun run() {
            if (scroller.computeScrollOffset()) {
                scrollOffset = scroller.currX.toFloat()
                invalidate()
                postOnAnimation(this)
            } else {
                snapToNearestMark()
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerX = width / 2f
        val indexAtCenter = ((scrollOffset + centerX) / markSpacing).roundToInt()

        for (i in 0..totalMarks) {
            val x = i * markSpacing - scrollOffset
            if (x in -100f..width + 100f) {
                val isCenter = i == indexAtCenter
                val paint = if (isCenter) centerMarkPaint else shortMarkPaint
                val markHeight = if (isCenter) longMarkHeight else shortMarkHeight
                canvas.drawLine(x, 0f, x, markHeight, paint)
            }
        }

        // Gửi callback
        onValueChanged?.invoke(indexAtCenter)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                lastTouchX = event.x
                velocityTracker = VelocityTracker.obtain()
                velocityTracker?.addMovement(event)
                scroller.forceFinished(true)
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = lastTouchX - event.x
                scrollOffset = (scrollOffset + dx).coerceIn(0f, (totalMarks * markSpacing - width).toFloat())
                lastTouchX = event.x
                velocityTracker?.addMovement(event)
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                velocityTracker?.addMovement(event)
                velocityTracker?.computeCurrentVelocity(1000)
                val vx = velocityTracker?.xVelocity ?: 0f
                velocityTracker?.recycle()
                velocityTracker = null

                if (abs(vx) > 500) {
                    scroller.fling(
                        scrollOffset.toInt(), 0,
                        -vx.toInt(), 0,
                        0, (totalMarks * markSpacing - width).toInt(),
                        0, 0
                    )
                    postOnAnimation(flingRunner)
                } else {
                    snapToNearestMark()
                }
            }
        }
        return true
    }

    private fun snapToNearestMark() {
        val centerX = width / 2f
        val index = ((scrollOffset + centerX) / markSpacing).roundToInt()
        val targetOffset = index * markSpacing - centerX
        val animator = ValueAnimator.ofFloat(scrollOffset, targetOffset).apply {
            duration = 200
            interpolator = DecelerateInterpolator()
            addUpdateListener {
                scrollOffset = it.animatedValue as Float
                invalidate()
            }
        }
        animator.start()
    }
}
