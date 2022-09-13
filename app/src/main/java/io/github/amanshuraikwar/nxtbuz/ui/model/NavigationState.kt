package io.github.amanshuraikwar.nxtbuz.ui.model

import io.github.amanshuraikwar.nxtbuz.commonkmm.BusStop

sealed class NavigationState {
    object BusStops : NavigationState()

    data class BusStopArrivals(
        val busStop: BusStop
    ) : NavigationState()

    data class TrainStopDepartures(
        val trainStopCode: String
    ) : NavigationState()

    data class BusRoute(
        val busStopCode: String,
        val busServiceNumber: String,
    ) : NavigationState()

    object Search : NavigationState()
}