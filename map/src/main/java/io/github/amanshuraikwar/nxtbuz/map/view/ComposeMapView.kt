package io.github.amanshuraikwar.nxtbuz.map.view

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.MapView
import io.github.amanshuraikwar.nxtbuz.map.utils.rememberMapViewWithLifecycle

@Composable
fun ComposeMapView(
    modifier: Modifier = Modifier,
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