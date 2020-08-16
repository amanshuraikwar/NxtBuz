package io.github.amanshuraikwar.nxtbuz.data.search.model

import io.github.amanshuraikwar.nxtbuz.common.model.BusStop

data class SearchResult(
    val busStopList: List<BusStop>,
    val busServiceList: List<BusService>,
)