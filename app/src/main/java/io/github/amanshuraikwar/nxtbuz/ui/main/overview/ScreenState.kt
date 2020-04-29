package io.github.amanshuraikwar.nxtbuz.ui.main.overview

import io.github.amanshuraikwar.nxtbuz.data.busstop.model.BusStop

sealed class ScreenState {

    class BusStopsState(
        val lat: Double,
        val lng: Double
    ) : ScreenState()

    class BusStopState(
        val busStop: BusStop
    ) : ScreenState()

    class BusServiceState(
        val busStop: BusStop,
        val busServiceNumber: String
    ) : ScreenState()
}