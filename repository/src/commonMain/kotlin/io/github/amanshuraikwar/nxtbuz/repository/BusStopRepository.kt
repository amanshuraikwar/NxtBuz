package io.github.amanshuraikwar.nxtbuz.repository

import io.github.amanshuraikwar.nxtbuz.commonkmm.BusStop
import kotlinx.coroutines.flow.Flow

interface BusStopRepository {
    fun setup(): Flow<Double>

    suspend fun getCloseBusStops(
        latitude: Double,
        longitude: Double,
        limit: Int
    ): List<BusStop>

    suspend fun getBusStopQueryLimit(): Int

    suspend fun setBusStopQueryLimit(
        newLimit: Int
    )

    suspend fun getMaxDistanceOfClosesBusStop(): Int

    suspend fun setMaxDistanceOfClosesBusStop(
        newMaxDistance: Int
    )

    suspend fun searchBusStops(query: String, limit: Int): List<BusStop>

    suspend fun getBusStop(busStopCode: String): BusStop

    suspend fun getCloseBusStops(
        lat: Double,
        lng: Double,
        max: Int,
        maxDistanceMetres: Int,
    ): List<BusStop>
}