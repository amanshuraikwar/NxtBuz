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

    // TODO-amanshuraikwar (12 Sep 2022 04:23:20 PM):
    //  add support for stop type
    data class TrainStop(
        val id: String,
        val code: String,
        val name: String,
        val hasDepartureTimes: Boolean,
        val hasTravelAssistance: Boolean,
        val isStarred: Boolean,
    ) : BusStopsItemData()
}