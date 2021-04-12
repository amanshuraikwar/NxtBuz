package io.github.amanshuraikwar.nxtbuz.common.model.map

import androidx.annotation.ColorInt
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker

sealed class MapEvent {
    object ClearMap : MapEvent()
    data class AddMarker(val marker: MapMarker) : MapEvent()
    data class DeleteMarker(val markerId: String) : MapEvent()
    data class MoveMarker(val markerId: String, val newPosition: LatLng) : MapEvent()
    data class AddMarkers(val markerList: List<MapMarker>) : MapEvent()
    data class MoveCenter(val lat: Double, val lng: Double) : MapEvent()
    data class DeleteMarkers(val markerList: List<Marker>) : MapEvent()
    data class AddCircle(val lat: Double, val lng: Double, val radius: Double) : MapEvent()
    data class AddRoute(
        val routeId: String,
        @ColorInt val lineColor: Int,
        val lineWidth: Float,
        val latLngList: List<Pair<Double, Double>>
    ) : MapEvent()
    data class DeleteRoute(
        val routeId: String,
    ) : MapEvent()
}