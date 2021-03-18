package io.github.amanshuraikwar.nxtbuz.map.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.MapStyleOptions
import io.github.amanshuraikwar.nxtbuz.common.R
import io.github.amanshuraikwar.nxtbuz.map.view.ComposeMapView

@Composable
fun NxtBuzMap(
    modifier: Modifier = Modifier,
    viewModel: NxtBuzMapViewModel,
) {
    val mapInitData by viewModel.initMapFlow.collectAsState(null)
    val isLight = MaterialTheme.colors.isLight
    val context = LocalContext.current

    Surface(modifier) {
        mapInitData?.let {
            ComposeMapView(
                Modifier.fillMaxSize(),
                GoogleMapOptions()
                    .camera(
                        CameraPosition.Builder().target(it.latLng)
                            .zoom(it.zoom)
                            .build()
                    )
                    .compassEnabled(false)
                    .mapToolbarEnabled(false)
                    .minZoomPreference(it.zoom)
            ) { googleMap ->
                googleMap.setMapStyle(
                    if (isLight) {
                        MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style_light)
                    } else {
                        MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style_dark)
                    }
                )

                it.onMapReady(googleMap)
            }
        }
    }
}