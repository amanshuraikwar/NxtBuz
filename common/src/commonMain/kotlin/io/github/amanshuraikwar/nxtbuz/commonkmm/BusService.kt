package io.github.amanshuraikwar.nxtbuz.commonkmm

data class BusService(
    val busServiceNumber: String,
    val originBusStopDescription: String,
    val destinationBusStopDescription: String,
    val numberOfBusStops: Int,
    val distance: Double
)