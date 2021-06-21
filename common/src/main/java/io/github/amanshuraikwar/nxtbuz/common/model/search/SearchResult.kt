package io.github.amanshuraikwar.nxtbuz.common.model.search

import io.github.amanshuraikwar.nxtbuz.common.model.BusService
import io.github.amanshuraikwar.nxtbuz.common.model.BusStop

data class SearchResult(
    val busStopList: List<BusStop>,
    val busServiceList: List<BusService>,
)