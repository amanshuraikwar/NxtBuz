package io.github.amanshuraikwar.nxtbuz.common.model.busroute

import io.github.amanshuraikwar.nxtbuz.common.model.BusStop

data class BusRouteNavigationParams(
    val busServiceNumber: String,
    val busStop: BusStop?
)