package io.github.amanshuraikwar.nxtbuz.ui.main.overview.map

import com.google.android.gms.maps.model.LatLng

data class AnimateUpdate(
    val latLng: LatLng,
    val duration: Long
)