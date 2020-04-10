package io.github.amanshuraikwar.nxtbuz.data.room.busstops

import androidx.room.Entity

@Entity(primaryKeys = ["code"])
data class BusStopEntity(
    val code: String,
    val roadName: String,
    val description: String,
    val latitude: Double,
    val longitude: Double
)