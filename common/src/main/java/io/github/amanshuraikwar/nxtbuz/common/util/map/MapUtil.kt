package io.github.amanshuraikwar.nxtbuz.common.util.map

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import io.github.amanshuraikwar.nxtbuz.common.R
import io.github.amanshuraikwar.nxtbuz.common.model.arrival.BusArrivals
import io.github.amanshuraikwar.nxtbuz.common.model.arrival.BusStopArrival
import io.github.amanshuraikwar.nxtbuz.common.model.BusStop
import io.github.amanshuraikwar.nxtbuz.common.util.isDarkTheme
import javax.inject.Inject
import kotlin.math.*


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

    fun busStopsToMarkers(busStopList: List<BusStop>): List<MarkerOptions> {
        return busStopList.map { busStop ->
            MarkerOptions()
                .position(
                    LatLng(
                        busStop.latitude,
                        busStop.longitude
                    )
                )
                .icon(
                    bitmapDescriptorFromVector(R.drawable.ic_marker_bus_stop_48)
                )
                .title(busStop.description)
        }
    }

    fun busArrivalsToMarkers(busStopArrivals: List<BusStopArrival>): List<MarkerOptions> {
        return busStopArrivals
            .filter { it.busArrivals is BusArrivals.Arriving }
            .map { busArrival ->
                MarkerOptions()
                    .position(
                        LatLng(
                            // TODO: 6/4/20 Make more safe
                            (busArrival.busArrivals as BusArrivals.Arriving).nextArrivingBus.latitude,
                            (busArrival.busArrivals as BusArrivals.Arriving).nextArrivingBus.longitude
                        )
                    )
                    .icon(
                        bitmapDescriptorFromVector(R.drawable.ic_marker_arriving_bus_48)
                    )
                    .title(busArrival.busServiceNumber)
                    .snippet(
                        if ((busArrival.busArrivals).nextArrivingBus.arrival == 0) {
                            "ARRIVING"
                        } else {
                            "${(busArrival.busArrivals).nextArrivingBus.arrival} MINS"
                        }
                    )
            }
    }

    companion object {
        @JvmStatic
        fun measureDistanceMetres(
            lat1: Double, lng1: Double, lat2: Double, lng2: Double
        ): Double {
            val r = 6378.137; // Radius of earth in KM
            val dLat = lat2 * Math.PI / 180 - lat1 * Math.PI / 180;
            val dLon = lng2 * Math.PI / 180 - lng1 * Math.PI / 180;
            val a = sin(dLat / 2) * sin(dLat / 2) +
                    cos(lat1 * Math.PI / 180) * cos(lat2 * Math.PI / 180) *
                    sin(dLon / 2) * sin(dLon / 2);
            val c = 2 * atan2(sqrt(a), sqrt(1 - a));
            val d = r * c;
            return d * 1000; // meters
        }
    }

    fun getCircleOptions(lat: Double, lng: Double, radius: Double): CircleOptions {
        return CircleOptions()
            .center(LatLng(lat, lng))
            .radius(radius)
            // todo get from settings
            .fillColor(ContextCompat.getColor(activity, R.color.orange_transparent))
            // todo get from settings
            .strokeWidth(4f)
            // todo get from settings
            .strokeColor(ContextCompat.getColor(activity, R.color.orange))
    }

    fun getLocationCircleOptions(lat: Double, lng: Double, radius: Double): CircleOptions {
        return CircleOptions()
            .center(LatLng(lat, lng))
            .radius(radius)
            // todo get from settings
            .fillColor(ContextCompat.getColor(activity, R.color.blue))
            // todo get from settings
            .strokeWidth(4f)
            // todo get from settings
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
        return ContextCompat.getColor(activity, R.color.orange)
    }

    @ColorInt
    fun getRouteLineColorLight(): Int {
        return ContextCompat.getColor(activity, R.color.orange_light)
    }

    fun getRouteLineWidth(): Float {
        return activity.resources.getDimension(R.dimen.bus_route_line_width)
    }

    fun getRouteLineWidthSmall(): Float {
        return activity.resources.getDimension(R.dimen.bus_route_line_width_small)
    }
}