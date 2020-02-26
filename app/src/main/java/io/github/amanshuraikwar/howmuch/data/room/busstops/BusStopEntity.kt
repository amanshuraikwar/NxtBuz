package io.github.amanshuraikwar.howmuch.data.room.busstops

import androidx.room.Entity
import io.github.amanshuraikwar.howmuch.data.BusStopCode

@Entity(primaryKeys = ["code"])
data class BusStopEntity(
    val code: String,
    val roadName: String,
    val description: String,
    val latitude: Double,
    val longitude: Double
)