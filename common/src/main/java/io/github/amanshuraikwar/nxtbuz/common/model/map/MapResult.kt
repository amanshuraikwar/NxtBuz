package io.github.amanshuraikwar.nxtbuz.common.model.map

import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.Polyline

sealed class MapResult {
    object EmptyResult : MapResult()
    data class AddMapMarkersResult(val markerList: List<Marker>) : MapResult()
    data class AddCircleResult(val circle: Circle) : MapResult()
    data class AddRouteResult(val polyline: Polyline, val markerList: List<Marker>) : MapResult()
    data class ErrorResult(val msg: String) : MapResult()
}