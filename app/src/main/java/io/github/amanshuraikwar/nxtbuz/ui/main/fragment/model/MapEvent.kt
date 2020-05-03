package io.github.amanshuraikwar.nxtbuz.ui.main.fragment.model

import androidx.annotation.ColorInt

sealed class MapEvent {
    object ClearMap : MapEvent()
    data class MoveCenter(val lat: Double, val lng: Double) : MapEvent()
    data class MapCircle(val lat: Double, val lng: Double, val radius: Double) : MapEvent()
    data class AddMapMarkers(val markerList: List<MapMarker>) : MapEvent()
    data class UpdateMapMarkers(val mapUpdateList: List<MapUpdate>) : MapEvent()
    data class DeleteMarker(val mapMarkerIdList: List<String>) : MapEvent()
    data class AddRoute(
        @ColorInt val lineColor: Int,
        val lineWidth: Float,
        val latLngList: List<Pair<Double, Double>>
    ) : MapEvent()
}