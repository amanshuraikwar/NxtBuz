package io.github.amanshuraikwar.nxtbuz.busroute.ui.model

import io.github.amanshuraikwar.nxtbuz.commonkmm.arrival.BusArrivals

data class CurrentBusStopArrivalsData(
    val busArrivals: BusArrivals? = null,
)