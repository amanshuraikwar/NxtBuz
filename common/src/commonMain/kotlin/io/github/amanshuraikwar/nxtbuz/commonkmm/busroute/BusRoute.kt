package io.github.amanshuraikwar.nxtbuz.commonkmm.busroute

data class BusRoute(
    val busServiceNumber: String,
    val direction: Int,
    val originBusStopDescription: String,
    val destinationBusStopDescription: String,
    val starred: Boolean? = null,
    val busRouteNodeList: List<BusRouteNode>
)