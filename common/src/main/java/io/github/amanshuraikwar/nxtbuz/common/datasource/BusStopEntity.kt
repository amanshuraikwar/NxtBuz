package io.github.amanshuraikwar.nxtbuz.common.datasource

data class BusStopEntity(
    val code: String,
    val roadName: String,
    val description: String,
    val latitude: Double,
    val longitude: Double
)