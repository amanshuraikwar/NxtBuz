package io.github.amanshuraikwar.nxtbuz.ui.model

import io.github.amanshuraikwar.nxtbuz.common.model.BusStop

sealed class MainScreenState {
    object BusStops : MainScreenState()

    data class BusStopArrivals(
        val busStop: BusStop
    ) : MainScreenState()

    data class BusRoute(
        val busStopCode: String,
        val busServiceNumber: String,
    ) : MainScreenState()
}