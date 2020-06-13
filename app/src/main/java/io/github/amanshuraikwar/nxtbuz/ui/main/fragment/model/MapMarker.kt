package io.github.amanshuraikwar.nxtbuz.ui.main.fragment.model

import androidx.annotation.DrawableRes

open class MapMarker(
    val id: String,
    val lat: Double,
    val lng: Double,
    @DrawableRes val iconDrawableRes: Int,
    val description: String
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

class ArrivingBusMapMarker(
    id: String,
    lat: Double,
    lng: Double,
    description: String,
    val busServiceNumber: String,
) : MapMarker(id, lat, lng, 0, description) {

    fun copy(lat: Double, lng: Double): ArrivingBusMapMarker {
        return ArrivingBusMapMarker(
            id, lat, lng, description, busServiceNumber
        )
    }
}