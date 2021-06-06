package io.github.amanshuraikwar.nxtbuz.map.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import io.github.amanshuraikwar.nxtbuz.common.R
import io.github.amanshuraikwar.nxtbuz.map.view.ComposeMapView
import io.github.amanshuraikwar.nxtbuz.map.view.rememberMapState

@Composable
fun NxtBuzMap(
    modifier: Modifier = Modifier,
    viewModel: NxtBuzMapViewModel,
    onClick: (LatLng) -> Unit = {},
) {
    val mapInitData by viewModel.initMapFlow.collectAsState(null)
    val isLight = MaterialTheme.colors.isLight
    val context = LocalContext.current
    val mapState = rememberMapState(initCenter = LatLng(0.0, 0.0), initZoom = 0f)

    Surface(modifier) {
        mapInitData?.let { mapInitData ->
            // do not update map state again if it has already moved/set
            if (mapState.center == LatLng(0.0, 0.0)) {
                mapState.updateCenter(mapInitData.latLng)
            }
            if (mapState.zoom == 0f) {
                mapState.updateZoom(mapInitData.zoom)
            }

            ComposeMapView(
                modifier = Modifier.fillMaxSize(),
                googleMapOptions = GoogleMapOptions()
                    .camera(
                        CameraPosition.Builder()
                            .target(mapState.center)
                            .zoom(mapState.zoom)
                            .build()
                    )
                    .compassEnabled(false)
                    .mapToolbarEnabled(false)
                    .minZoomPreference(mapInitData.zoom),
                onMapInit = { googleMap ->
                    googleMap.setMapStyle(
                        if (isLight) {
                            MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style_light)
                        } else {
                            MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style_dark)
                        }
                    )

                    googleMap.setOnCameraIdleListener {
                        mapState updateWith googleMap
                    }

                    googleMap.setOnMapClickListener { latLng ->
                        onClick(latLng)
                    }

                    mapInitData.onMapReady(googleMap)
                },
            )
        }
    }
}