package io.github.amanshuraikwar.nxtbuz.map.view

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import io.github.amanshuraikwar.nxtbuz.map.utils.rememberMapViewWithLifecycle

class ComposeMapState(
    private var centerState: MutableState<LatLng>,
    private var zoomState: MutableState<Float>,
) {
    val center by centerState
    val zoom by zoomState

    fun updateCenter(newCenter: LatLng) {
        if (center != newCenter) {
            centerState.value = newCenter
        }
    }

    fun updateZoom(newZoom: Float) {
        if (zoom != newZoom) {
            zoomState.value = newZoom
        }
    }

    infix fun updateWith(googleMap: GoogleMap) {
        updateCenter(googleMap.cameraPosition.target)
        updateZoom(googleMap.cameraPosition.zoom)
    }
}

@Composable
fun rememberMapState(
    initCenter: LatLng,
    initZoom: Float,
): ComposeMapState {
    val center: MutableState<LatLng> = rememberSaveable {
        mutableStateOf(initCenter)
    }

    val zoom: MutableState<Float> = rememberSaveable {
        mutableStateOf(initZoom)
    }

    return ComposeMapState(center, zoom)
}

@Composable
fun ComposeMapView(
    modifier: Modifier = Modifier,
    state: ComposeMapState,
    googleMapOptions: GoogleMapOptions = GoogleMapOptions(),
    mapView: MapView = rememberMapViewWithLifecycle(googleMapOptions),
    onMapInit: (GoogleMap) -> Unit = {}
) {
    AndroidView(
        {
            mapView
        },
        modifier
    ) { view ->
        view.getMapAsync(onMapInit)
    }
}