package io.github.amanshuraikwar.nxtbuz.remotedatasource

data class BusStopItemDto(
    val code: String,
    val roadName: String,
    val description: String,
    val lat: Double,
    val lng: Double
)