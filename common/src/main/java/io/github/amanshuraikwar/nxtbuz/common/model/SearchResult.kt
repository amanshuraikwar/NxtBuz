package io.github.amanshuraikwar.nxtbuz.common.model

data class SearchResult(
    val busStopList: List<BusStop>,
    val busServiceList: List<BusService>,
)