package io.github.amanshuraikwar.nxtbuz.ui.model

import io.github.amanshuraikwar.nxtbuz.common.model.BusStop

sealed class MainScreenState {
    object BusStops : MainScreenState()

    class BusStopArrivals(
        val busStop: BusStop
    ) : MainScreenState()

    class BusRoute(
        val busStopCode: String,
        val busServiceNumber: String,
    ) : MainScreenState()

    object Search : MainScreenState()
}