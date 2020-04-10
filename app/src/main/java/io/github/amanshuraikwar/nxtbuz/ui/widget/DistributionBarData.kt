package io.github.amanshuraikwar.nxtbuz.ui.widget

import android.content.Context
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange

data class DistributionBarPortion(
    val name: String,
    val value: Float,
    val colorInt: Int
)

data class DistributionBarData(
    val portions: List<DistributionBarPortion>,
    val maxValue: Float
)

fun List<DistributionBarPortion>.toRenderData(maxValue: Float, context: Context): List<DistributionBarRenderData> {
    val renderDataList = mutableListOf<DistributionBarRenderData>()
    forEachIndexed { _, it ->

        val startX = if (renderDataList.isEmpty()) {
            0f
        } else {
            renderDataList.last().endX
        }

        val endX = startX + it.value / maxValue

        renderDataList.add(DistributionBarRenderData(it.name, startX, endX, it.colorInt))
    }

    return renderDataList
}

data class DistributionBarRenderData(
    val name: String,
    @FloatRange(from = 0.0, to = 1.0) val startX: Float,
    @FloatRange(from = 0.0, to = 1.0) val endX: Float,
    @ColorInt val color: Int
)