package io.github.amanshuraikwar.nxtbuz.busstop.ui

import io.github.amanshuraikwar.nxtbuz.common.model.BusStop as BusStopData

sealed class BusStopsItemData {
    data class Header(val title: String) : BusStopsItemData()

    data class BusStop(
        val busStopDescription: String,
        val busStopInfo: String,
        val operatingBuses: String,
        val busStop: BusStopData,
    ) : BusStopsItemData()
}