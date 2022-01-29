package io.github.amanshuraikwar.nxtbuz.busstop.busstops.model

sealed class BusStopsItemData {
    data class Header(
        val id: String,
        val title: String
    ) : BusStopsItemData()

    data class BusStop(
        val id: String,
        val busStopCode: String,
        val busStopDescription: String,
        val busStopInfo: String,
        val operatingBuses: String,
        val isStarred: Boolean,
    ) : BusStopsItemData()
}