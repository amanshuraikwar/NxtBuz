package io.github.amanshuraikwar.nxtbuz.busroute.ui.model

import kotlinx.coroutines.flow.StateFlow

data class BusRouteHeaderData(
    val busServiceNumber: String,
    val destinationBusStopDescription: String,
    val originBusStopDescription: String,
    val busStopCode: String,
    val starred: StateFlow<Boolean>,
)