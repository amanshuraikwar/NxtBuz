package io.github.amanshuraikwar.nxtbuz.common.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import io.github.amanshuraikwar.nxtbuz.common.R
import io.github.amanshuraikwar.nxtbuz.common.util.lerp

class BottomSheetBgView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val maxCornerRadius: Float

    private val paint: Paint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.colorSurface)
    }

    private var cornerRadius: Float

    init {
        val resources = context.resources
        maxCornerRadius = resources.getDimension(R.dimen.cornerSizeMediumComponent)
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

    override fun onSaveInstanceState(): Parcelable {
        super.onSaveInstanceState()
        return bundleOf("cornerRadius" to cornerRadius)
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        super.onRestoreInstanceState(state)
        cornerRadius = (state as? Bundle)?.getFloat("cornerRadius") ?: return
        invalidate()
    }
}
