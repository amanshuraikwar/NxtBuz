package io.github.amanshuraikwar.nxtbuz.busstop.busstops.model

import io.github.amanshuraikwar.nxtbuz.commonkmm.BusStop as BusStopData

sealed class BusStopsItemData {
    data class Header(
        val id: String,
        val title: String
    ) : BusStopsItemData()

    data class BusStop(
        val id: String,
        val busStopDescription: String,
        val busStopInfo: String,
        val operatingBuses: String,
        val busStop: BusStopData,
    ) : BusStopsItemData()
}