package io.github.amanshuraikwar.nxtbuz.localdatasource

data class BusStopEntity(
    val code: String,
    val roadName: String,
    val description: String,
    val latitude: Double,
    val longitude: Double
)