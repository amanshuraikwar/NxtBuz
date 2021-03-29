package io.github.amanshuraikwar.nxtbuz.search.ui.model

import io.github.amanshuraikwar.nxtbuz.common.model.BusStop

sealed class SearchResult {
    data class BusStopResult(
        val busStopDescription: String,
        val busStopInfo: String,
        val operatingBuses: String,
        val busStop: BusStop,
    ) : SearchResult()
}