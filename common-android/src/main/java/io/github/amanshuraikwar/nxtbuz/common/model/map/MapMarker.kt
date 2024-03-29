package io.github.amanshuraikwar.nxtbuz.common.model.map

import androidx.annotation.DrawableRes

open class MapMarker(
    val id: String,
    val lat: Double,
    val lng: Double,
    @DrawableRes val iconDrawableRes: Int,
    val description: String,
    val isFlat: Boolean = false,
) {
    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MapMarker

        if (id != other.id) return false

        return true
    }
}