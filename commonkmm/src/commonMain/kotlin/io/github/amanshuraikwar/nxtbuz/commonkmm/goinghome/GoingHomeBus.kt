package io.github.amanshuraikwar.nxtbuz.commonkmm.goinghome

data class GoingHomeBus(
    val sourceBusStopDescription: String,
    val sourceBusStopCode: String,
    val destinationBusStopDescription: String,
    val destinationBusStopCode: String,
    val busServiceNumber: String,
    val stops: Int,
    val distance: Double
)