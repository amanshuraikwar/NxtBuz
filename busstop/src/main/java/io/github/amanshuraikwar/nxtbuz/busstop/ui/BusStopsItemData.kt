package io.github.amanshuraikwar.nxtbuz.busstop.ui


sealed class BusStopsItemData {
    data class Header(val title: String) : BusStopsItemData()

    data class BusStop(
        val busStopDescription: String,
        val busStopInfo: String,
        val operatingBuses: String,
    ) : BusStopsItemData()
}