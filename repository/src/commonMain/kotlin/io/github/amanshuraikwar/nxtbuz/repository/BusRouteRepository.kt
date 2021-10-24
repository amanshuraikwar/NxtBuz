package io.github.amanshuraikwar.nxtbuz.repository

import io.github.amanshuraikwar.nxtbuz.commonkmm.busroute.BusRoute
import kotlinx.coroutines.flow.Flow

interface BusRouteRepository {
    fun setup(): Flow<Double>

    suspend fun getBusRoute(
        busServiceNumber: String,
        direction: Int? = null,
        busStopCode: String? = null
    ): BusRoute
}