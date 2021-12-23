package com.zj.rangeview.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.zj.rangeview.R

class RangeView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val thumbPaint = Paint().apply {
        color = Color.RED
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private val thumbLinePaint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.FILL
        isAntiAlias = true
        strokeWidth = 10f
        strokeCap = Paint.Cap.ROUND
    }

    private val startThumbRectF = RectF()
    private val endThumbRectF = RectF()
    private val startThumbLine = Line()
    private val endThumbLine = Line()

    private val touchThreshold = context.resources.getDimension(R.dimen.touch_threshold)
    private val thumbWidth = context.resources.getDimension(R.dimen.thumb_width)

    private val maxRange = 10F
    private val minRange = 3F
    private val startValue = 0F
    private val endValue = 5F

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val startPosition = measuredWidth / maxRange * startValue
        val endPosition = measuredWidth / maxRange * endValue
        startThumbRectF.set(startPosition, 0f, startPosition + thumbWidth, measuredHeight * 1f)
        endThumbRectF.set(endPosition, 0f, endPosition + thumbWidth, measuredHeight * 1f)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawThumb(canvas)
    }

    private fun drawThumb(canvas: Canvas) {
        canvas.drawRoundRect(startThumbRectF, 10f, 10f, thumbPaint)
        canvas.drawRoundRect(endThumbRectF, 10f, 10f, thumbPaint)
        with(startThumbLine) {
            canvas.drawLine(startX, startY, endX, endY, thumbLinePaint)
        }
        with(endThumbLine) {
            canvas.drawLine(startX, startY, endX, endY, thumbLinePaint)
        }
    }

    private fun updateThumb(event: MotionEvent): Boolean {
        return when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                startThumbRectF.isInThumb(event) || endThumbRectF.isInThumb(event)
            }
            MotionEvent.ACTION_MOVE -> {
                when {
                    startThumbRectF.isInThumb(event) -> {
                        startThumbRectF.update(event)
                        true
                    }
                    endThumbRectF.isInThumb(event) -> {
                        endThumbRectF.update(event)
                        true
                    }
                    else -> {
                        false
                    }
                }
            }
            else -> false
        }
    }

    /**
     * 不能滑到屏幕外面，开始的滑块要比结束的滑块小
     */
    private fun isUpdateValid(): Boolean {
        return startThumbRectF.left > 0 && endThumbRectF.right < measuredWidth && (startThumbRectF.right + getMinDistance()) < endThumbRectF.left
    }

    /**
     * 滑块的最小间隔距离
     */
    private fun getMinDistance(): Float {
        return measuredWidth * minRange / maxRange
    }

    /**
     * 触摸了哪个滑块
     */
    private fun RectF.isInThumb(event: MotionEvent): Boolean {
        return (left - touchThreshold) < event.x && (right + touchThreshold) > event.x
    }

    /**
     * 滑动重绘滑块的位置，通知range变化
     */
    private fun RectF.update(event: MotionEvent) {
        if (isUpdateValid()) {
            val thumbWidth = (right - left)
            left = event.x - thumbWidth / 2
            right = event.x + thumbWidth / 2
            invalidate()
        }
    }

    data class Line(
        var startX: Float = 0f,
        var startY: Float = 0f,
        var endX: Float = 0f,
        var endY: Float = 0f
    )

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return updateThumb(event)
    }
}