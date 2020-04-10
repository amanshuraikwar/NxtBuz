package io.github.amanshuraikwar.nxtbuz.ui.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class RoundedDistributionBar: View {

    constructor(context: Context): super(context) {
    }

    constructor(context: Context, attrs: AttributeSet?)
            : super(context, attrs) {
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr) {
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int)
            : super(context, attrs, defStyleAttr, defStyleRes) {
    }

    var distributionBarRenderData = listOf<DistributionBarRenderData>()

    var distributionBarData = DistributionBarData(listOf(), 100f)
        set(value) {
            field = value
            distributionBarRenderData = field.portions.toRenderData(field.maxValue, context)
            invalidate()
        }

    private val contentRect = RectF()

    private val linePaint by lazy {
        Paint().apply {
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
            isAntiAlias = true
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        linePaint.strokeWidth = contentRect.height()

        distributionBarRenderData.forEach {
            canvas.drawLine(
                contentRect.left + contentRect.width() * it.startX,
                contentRect.height() / 2f,
                contentRect.left + contentRect.width() * it.endX,
                contentRect.height() / 2f,
                linePaint.apply { color = it.color }
            )
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldW: Int, oldH: Int) {
        super.onSizeChanged(w, h, oldW, oldH)
        updateContentRect()
        invalidate()
    }

    private fun updateContentRect() {
        contentRect.set(
            paddingStart.toFloat(),
            paddingTop.toFloat(),
            (width - paddingEnd).toFloat(),
            (height - paddingBottom).toFloat()
        )
    }
}
