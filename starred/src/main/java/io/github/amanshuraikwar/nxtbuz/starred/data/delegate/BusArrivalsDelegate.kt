package io.github.amanshuraikwar.nxtbuz.starred.data.delegate

import io.github.amanshuraikwar.nxtbuz.common.model.BusArrival

interface BusArrivalsDelegate {
    suspend fun getBusArrivals(busStopCode: String): List<BusArrival>
}