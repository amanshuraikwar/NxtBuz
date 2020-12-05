package io.github.amanshuraikwar.nxtbuz.common.model.screenstate

import io.github.amanshuraikwar.nxtbuz.common.model.BusRoute
import io.github.amanshuraikwar.nxtbuz.common.model.BusStop

sealed class ScreenState {

    class BusStopsState(
        val lat: Double,
        val lng: Double
    ) : ScreenState()

    class BusStopState(
        val busStop: BusStop
    ) : ScreenState()

    class BusRouteState(
        var busStop: BusStop? = null,
        val busServiceNumber: String,
    ) : ScreenState() {
        lateinit var busRoute: BusRoute
    }
}