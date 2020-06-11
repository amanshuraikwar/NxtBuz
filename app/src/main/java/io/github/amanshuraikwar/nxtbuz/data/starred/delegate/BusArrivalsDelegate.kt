package io.github.amanshuraikwar.nxtbuz.data.starred.delegate

import io.github.amanshuraikwar.nxtbuz.data.busarrival.model.BusArrival

interface BusArrivalsDelegate {
    suspend fun getBusArrivals(busStopCode: String): List<BusArrival>
}