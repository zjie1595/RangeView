package com.zj.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class RangeView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }

    private var rangeListener: RangeListener? = null

    private val startThumbBitmap = BitmapFactory.decodeResource(resources, R.drawable.thumb_start)
    private val endThumbBitmap = BitmapFactory.decodeResource(resources, R.drawable.thumb_end)

    private val touchThreshold = context.resources.getDimension(R.dimen.touch_threshold)
    private val thumbWidth = context.resources.getDimension(R.dimen.thumb_width)

    private val maxValue = 100F
    private val minValue = 20F
    private val startValue = 30F
    private val endValue = 80F

    private val startRectF = RectF()
    private val endRectF = RectF()

    private var actionDownFlag = -1

    private val selectedRectF = RectF()

    private val selectedRectFPaint = Paint().apply {
        color = Color.TRANSPARENT
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }

    private val backgroundRectF = RectF()

    private val backgroundPaint = Paint().apply {
        color = Color.parseColor("#80000000")
        xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OVER)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val range = maxValue - minValue
        val startPosition = (startValue - minValue) / range * measuredWidth
        val endPosition = (endValue - minValue) / range * measuredWidth
        startRectF.set(startPosition, 0F, startPosition + thumbWidth, measuredHeight * 1F)
        endRectF.set(endPosition, 0F, endPosition + thumbWidth, measuredHeight * 1F)
        backgroundRectF.set(0F, 0F, measuredWidth * 1F, measuredHeight * 1F)
        selectedRectF.set(startRectF.right, 0F, endRectF.left, measuredWidth * 1F)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawThumb(canvas)
        drawBackground(canvas)
        drawSelectedRegion(canvas)
    }

    private fun drawSelectedRegion(canvas: Canvas) {
        canvas.drawRect(selectedRectF, selectedRectFPaint)
    }

    fun setRangeListener(listener: RangeListener) {
        rangeListener = listener
    }

    private fun drawThumb(canvas: Canvas) {
        canvas.drawBitmap(startThumbBitmap, null, startRectF, null)
        canvas.drawBitmap(endThumbBitmap, null, endRectF, null)
    }

    private fun drawBackground(canvas: Canvas) {
        canvas.drawRect(backgroundRectF, backgroundPaint)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                onActionDown(event)
            }
            MotionEvent.ACTION_MOVE -> {
                onActionMove(event)
            }
            MotionEvent.ACTION_UP -> {
                actionDownFlag = -1
                rangeListener?.onStopTrackingTouch(this)
                true
            }
            else -> {
                false
            }
        }
    }

    private fun onActionDown(event: MotionEvent): Boolean {
        actionDownFlag = when {
            startRectF.isTouchThumb(event) -> {
                0
            }
            endRectF.isTouchThumb(event) -> {
                1
            }
            else -> {
                -1
            }
        }
        if (actionDownFlag != -1) {
            rangeListener?.onStartTrackingTouch(this)
        }
        return actionDownFlag != -1
    }

    private fun onActionMove(event: MotionEvent): Boolean {
        if (actionDownFlag == 0) {
            updateThumb(startRectF, event)
        } else if (actionDownFlag == 1) {
            updateThumb(endRectF, event)
        }
        return true
    }

    private fun updateThumb(rectF: RectF, event: MotionEvent) {
        if (event.x < 0 || event.x > measuredWidth - thumbWidth) return
        rectF.left = event.x
        rectF.right = rectF.left + thumbWidth
        selectedRectF.set(startRectF.right, 0F, endRectF.left, measuredWidth * 1F)
        val changingValue = if (rectF == startRectF) calculateStartValue() else calculateEndValue()
        rangeListener?.onRangeChangeListener(
            this,
            calculateStartValue(),
            calculateEndValue(),
            changingValue,
            rectF == startRectF,
            true
        )
        invalidate()
    }

    private fun calculateStartValue(): Float {
        return (startRectF.left / measuredWidth) * maxValue
    }

    private fun calculateEndValue(): Float {
        return (endRectF.right / measuredWidth) * maxValue
    }

    private fun RectF.isTouchThumb(event: MotionEvent): Boolean {
        return event.x in (left - touchThreshold)..(right + touchThreshold)
    }
}