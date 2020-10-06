package io.github.amanshuraikwar.nxtbuz.common.util.map

import android.graphics.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.google.android.gms.maps.model.*
import io.github.amanshuraikwar.nxtbuz.common.R
import javax.inject.Inject
import kotlin.math.*


class MarkerUtil @Inject constructor(private val activity: AppCompatActivity) {

    private val arrivingBusTextSize: Float by lazy {
        activity.resources.getDimension(R.dimen.bus_service_number_map_marker_text_size)
    }

    private val arrivingBusTextColor: Int by lazy {
        ContextCompat.getColor(activity, R.color.orange)
    }

    private val arrivingBusTextPaint: Paint by lazy {
        Paint().apply {
            color = arrivingBusTextColor
            textSize = arrivingBusTextSize
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
            typeface = ResourcesCompat.getFont(activity, R.font.rubik_medium)
        }
    }

    private val arrivingBusTextRectColor: Int by lazy {
        ContextCompat.getColor(activity, R.color.white)
    }

    private val arrivingBusTextRectStrokeColor: Int by lazy {
        ContextCompat.getColor(activity, R.color.orange)
    }

    private val arrivingBusTextRectPaint: Paint by lazy {
        Paint().apply {
            color = arrivingBusTextRectColor
            isAntiAlias = true
            style = Paint.Style.FILL
        }
    }

    private val arrivingBusTextRectStrokeWidth: Float by lazy {
        activity.resources.getDimension(R.dimen.bus_service_number_map_marker_text_rect_stroke_width)
    }

    private val arrivingBusTextRectStrokePaint: Paint by lazy {
        Paint().apply {
            color = arrivingBusTextRectStrokeColor
            isAntiAlias = true
            style = Paint.Style.STROKE
            strokeWidth = arrivingBusTextRectStrokeWidth
        }
    }

    private val arrivingBusTextRectRadius: Float by lazy {
        activity.resources.getDimension(R.dimen.bus_service_number_map_marker_text_rect_radius)
    }

    private val arrivingBusTextPadding: Float by lazy {
        activity.resources.getDimension(R.dimen.bus_service_number_map_marker_text_padding)
    }

    fun arrivingBusBitmapDescriptor(busServiceNumber: String): BitmapDescriptor {

        val context = activity.applicationContext

        val vectorDrawable =
            ContextCompat.getDrawable(context, R.drawable.ic_marker_arriving_bus_48)
                ?: throw IllegalArgumentException("Vector res id is not valid.")

        val vectorHeight = vectorDrawable.intrinsicHeight
        val vectorWidth = vectorDrawable.intrinsicWidth

        val textSize = arrivingBusTextSize
        val textWidth = arrivingBusTextPaint.measureText(busServiceNumber)

        val textRectHeight = textSize + arrivingBusTextPadding * 2
        var textRectWidth = textWidth + arrivingBusTextPadding * 2

        val bitmapWidth = ceil(max(vectorWidth.toFloat(), textRectWidth)).toInt()
        val bitmapHeight =
            ceil(vectorHeight.toFloat() + textRectHeight).toInt()

        textRectWidth = max(textRectWidth, bitmapWidth.toFloat())

        val bitmap = Bitmap.createBitmap(
            bitmapWidth,
            bitmapHeight,
            Bitmap.Config.ARGB_8888
        )

        val canvas = Canvas(bitmap)

        // bitmapWidth >= vectorWidth
        val gap = (bitmapWidth - vectorWidth) / 2f

        @Suppress("UnnecessaryVariable")
        val vectorLeftX = gap
        // bitmapWidth > gap
        val vectorRightX = bitmapWidth - gap

        canvas.drawRoundRect(
            0f,
            0f,
            textRectWidth,
            textRectHeight,
            arrivingBusTextRectRadius,
            arrivingBusTextRectRadius,
            arrivingBusTextRectPaint
        )

        canvas.drawRoundRect(
            0f + arrivingBusTextRectStrokeWidth / 2,
            0f + arrivingBusTextRectStrokeWidth / 2,
            textRectWidth - arrivingBusTextRectStrokeWidth / 2,
            textRectHeight - arrivingBusTextRectStrokeWidth / 2,
            arrivingBusTextRectRadius,
            arrivingBusTextRectRadius,
            arrivingBusTextRectStrokePaint
        )

        vectorDrawable.setBounds(
            vectorLeftX.toInt(),
            textRectHeight.toInt(),
            vectorRightX.toInt(),
            bitmapHeight
        )
        vectorDrawable.draw(canvas)

        val textBound = Rect()
        arrivingBusTextPaint.getTextBounds(
            busServiceNumber, 0, busServiceNumber.length, textBound
        )

        val textY = textRectHeight - (textRectHeight - textBound.height()) / 2
        val textCenterX = textRectWidth - (textRectWidth - textBound.width()) / 2 - textBound.width() / 2

        canvas.drawText(
            busServiceNumber,
            textCenterX,
            textY,
            arrivingBusTextPaint
        )

        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }
}