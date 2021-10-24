package io.github.amanshuraikwar.nxtbuz.localdatasource

data class DirectBusEntity(
    val sourceBusStopCode: String,
    val destinationBusStopCode: String,
    val hasDirectBus: Boolean,
    val busServiceNumber: String,
    val stops: Int,
    val distance: Double
)