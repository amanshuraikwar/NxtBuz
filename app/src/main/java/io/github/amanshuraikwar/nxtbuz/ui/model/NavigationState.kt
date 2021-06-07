package io.github.amanshuraikwar.nxtbuz.ui.model

import io.github.amanshuraikwar.nxtbuz.common.model.BusStop

sealed class NavigationState {
    object BusStops : NavigationState()

    data class BusStopArrivals(
        val busStop: BusStop
    ) : NavigationState()

    data class BusRoute(
        val busStopCode: String,
        val busServiceNumber: String,
    ) : NavigationState()

    object Search : NavigationState()
}