package io.github.amanshuraikwar.nxtbuz.common.model.map

import androidx.annotation.DrawableRes

data class MapUpdate(
    val id: String,
    val newLat: Double? = null,
    val newLng: Double? = null,
    @DrawableRes val newIconDrawableRes: Int? = null,
    val newDescription: String? = null,
    val animatePosition: AnimateMarker = AnimateMarker.NoAnimate
)