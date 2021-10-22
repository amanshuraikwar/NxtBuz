package io.github.amanshuraikwar.nxtbuz.repository

import io.github.amanshuraikwar.nxtbuz.commonkmm.Bus
import io.github.amanshuraikwar.nxtbuz.commonkmm.arrival.BusStopArrival

interface BusArrivalRepository {
    suspend fun getOperatingBusServices(busStopCode: String): List<Bus>

    suspend fun getBusArrivals(busStopCode: String): List<BusStopArrival>

    suspend fun getBusArrivals(busStopCode: String, busServiceNumber: String): BusStopArrival
}