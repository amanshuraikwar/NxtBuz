package io.github.amanshuraikwar.nxtbuz.common.util.map

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import io.github.amanshuraikwar.nxtbuz.common.R
import io.github.amanshuraikwar.nxtbuz.common.util.isDarkTheme
import javax.inject.Inject
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class MapUtil @Inject constructor(
    private val activity: Activity,
) {

    fun bitmapDescriptorFromVector(vectorResId: Int): BitmapDescriptor {
        val context = activity.applicationContext
        val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)
            ?: throw IllegalArgumentException("Vector res id $vectorResId is not valid.")
        vectorDrawable.setBounds(
            0,
            0,
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight
        )
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)

        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    fun getLocationCircleOptions(lat: Double, lng: Double, radius: Double): CircleOptions {
        return CircleOptions()
            .center(LatLng(lat, lng))
            .radius(radius)
            .fillColor(ContextCompat.getColor(activity, R.color.blue))
            .strokeWidth(4f)
            .strokeColor(ContextCompat.getColor(activity, R.color.white))
    }

    fun updateMapStyle(googleMap: GoogleMap) {
        googleMap.setMapStyle(
            if (isDarkTheme(activity)) {
                MapStyleOptions.loadRawResourceStyle(activity, R.raw.map_style_dark)
            } else {
                MapStyleOptions.loadRawResourceStyle(activity, R.raw.map_style_light)
            }
        )
    }

    @ColorInt
    fun getRouteLineColor(): Int {
        return ContextCompat.getColor(activity, R.color.blue)
    }

    fun getRouteLineWidth(): Float {
        return activity.resources.getDimension(R.dimen.bus_route_line_width)
    }

    companion object {
        @JvmStatic
        fun measureDistanceMetres(
            lat1: Double, lng1: Double, lat2: Double, lng2: Double
        ): Double {
            val r = 6378.137 // Radius of earth in KM
            val dLat = lat2 * Math.PI / 180 - lat1 * Math.PI / 180
            val dLon = lng2 * Math.PI / 180 - lng1 * Math.PI / 180
            val a = sin(dLat / 2) * sin(dLat / 2) +
                    cos(lat1 * Math.PI / 180) * cos(lat2 * Math.PI / 180) *
                    sin(dLon / 2) * sin(dLon / 2)
            val c = 2 * atan2(sqrt(a), sqrt(1 - a))
            val d = r * c
            return d * 1000 // meters
        }
    }
}