package io.github.amanshuraikwar.nxtbuz.data.busroute.model

data class BusRoute(
    val busServiceNumber: String,
    val direction: Int,
    val busRouteNodeList: List<BusRouteNode>
)