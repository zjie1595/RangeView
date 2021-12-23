package com.zj.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class TestView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val yellowPaint = Paint().apply {
        color = Color.YELLOW
    }

    private val yellowRectF = RectF(0F, 0F, 400F, 300F)

    private val greenPaint = Paint().apply {
        color = Color.GREEN
        xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OVER)
    }

    private val greenRectF = RectF(100F, 100F, 800F, 200F)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        setLayerType(LAYER_TYPE_SOFTWARE, null)

        canvas.drawRect(yellowRectF, yellowPaint)
        canvas.drawRect(greenRectF, greenPaint)
    }
}