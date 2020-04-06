package io.github.amanshuraikwar.howmuch.ui.main.overview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import io.github.amanshuraikwar.howmuch.R
import io.github.amanshuraikwar.howmuch.util.lerp

class BottomSheetBgView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val maxCornerRadius: Float

    private val paint: Paint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.color_surface)
    }

    private var cornerRadius: Float

    init {
        val resources = context.resources
        maxCornerRadius = resources.getDimension(R.dimen.shape_corner_size_medium_component)
        cornerRadius = maxCornerRadius
    }

    fun update(slideOffset: Float) {
        cornerRadius = lerp(
            maxCornerRadius, 0f, 0f, 1f, slideOffset
        )
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawRoundRect(
            0f, 0f, width.toFloat(), height.toFloat(), cornerRadius, cornerRadius, paint
        )
    }
}
