package io.github.amanshuraikwar.nxtbuz.repository

import io.github.amanshuraikwar.nxtbuz.commonkmm.TrainStop

/**
 * Every regional api provider must implement this interface to support providing train stops
 * @author amanshuraikwar
 * @since 11 Sep 2022 12:52:28 PM
 */
interface TrainStopRepository {
    suspend fun supportsLocation(lat: Double, lng: Double): Boolean

    suspend fun getCloseTrainStops(
        lat: Double,
        lng: Double,
        maxStops: Int,
        maxDistanceMetres: Int? = null
    ): List<TrainStop>

    interface Factory {
        fun create(): TrainStopRepository
    }
}