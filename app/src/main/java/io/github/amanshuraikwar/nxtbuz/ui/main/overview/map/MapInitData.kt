package io.github.amanshuraikwar.nxtbuz.ui.main.overview.map

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng

data class MapInitData(
    val latLng: LatLng,
    val zoom: Float,
    val onMapReady: (map: GoogleMap?) -> Unit
)