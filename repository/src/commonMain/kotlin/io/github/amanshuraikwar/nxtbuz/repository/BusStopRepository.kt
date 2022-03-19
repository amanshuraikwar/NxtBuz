package io.github.amanshuraikwar.nxtbuz.repository

import io.github.amanshuraikwar.nxtbuz.commonkmm.BusStop
import io.github.amanshuraikwar.nxtbuz.commonkmm.goinghome.DirectBus
import io.github.amanshuraikwar.nxtbuz.commonkmm.goinghome.DirectBusesResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

interface BusStopRepository {
    fun setup(): Flow<Double>

    suspend fun getCloseBusStops(
        latitude: Double,
        longitude: Double,
        limit: Int,
        metres: Int? = null
    ): List<BusStop>

    suspend fun getBusStopQueryLimit(): Int

    suspend fun setBusStopQueryLimit(
        newLimit: Int
    )

    suspend fun getMaxDistanceOfClosesBusStop(): Int

    suspend fun setMaxDistanceOfClosesBusStop(
        newMaxDistance: Int
    )

    suspend fun getBusStop(busStopCode: String): BusStop?

    suspend fun getStarredBusStops(): List<BusStop>

    /**
     * @return true if a bus stop exists with [busStopCode], false if it doesn't
     */
    suspend fun toggleBusStopStar(
        busStopCode: String,
        toggleTo: Boolean? = null
    ): Boolean

    suspend fun isBusStopStarred(
        busStopCode: String,
    ): Boolean

    suspend fun busStopUpdates(): SharedFlow<BusStop>

    suspend fun getDirectBuses(
        sourceBusStopCode: String,
        destinationBusStopCode: String
    ): DirectBusesResult

    suspend fun setDirectBuses(directBusList: List<DirectBus>)

    suspend fun setNoDirectBusesFor(sourceBusStopCode: String, destinationBusStopCode: String)

    suspend fun getCachedDirectBusesStopPermutationsCount(): Int
}