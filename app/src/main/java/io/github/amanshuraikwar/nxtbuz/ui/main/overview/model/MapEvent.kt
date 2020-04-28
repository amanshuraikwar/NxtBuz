package io.github.amanshuraikwar.nxtbuz.ui.main.overview.model

sealed class MapEvent {
    object ClearMap : MapEvent()
    data class MoveCenter(val lat: Double, val lng: Double) : MapEvent()
    data class MapCircle(val lat: Double, val lng: Double, val radius: Double) : MapEvent()
    data class AddMapMarkers(val markerList: List<MapMarker>) : MapEvent()
    data class UpdateMapMarkers(val mapUpdateList: List<MapUpdate>) : MapEvent()
    data class DeleteMarker(val mapMarkerIdList: List<String>) : MapEvent()
}