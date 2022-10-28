package io.github.amanshuraikwar.nxtbuz.repository

import io.github.amanshuraikwar.nxtbuz.commonkmm.train.TrainDeparture
import io.github.amanshuraikwar.nxtbuz.commonkmm.train.TrainDetails
import io.github.amanshuraikwar.nxtbuz.commonkmm.train.TrainStop

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

    suspend fun containsStop(code: String): Boolean

    suspend fun getTrainDepartures(trainStopCode: String): List<TrainDeparture>

    suspend fun getTrainStop(code: String): TrainStop?

    suspend fun supportsTrain(trainCode: String): Boolean

    suspend fun getTrainDetails(trainCode: String): TrainDetails

    suspend fun getTrainsBetween(
        fromTrainStopCode: String,
        toTrainStopCode: String
    ): List<TrainDetails>

    suspend fun searchTrainStops(trainStopName: String): List<TrainStop>

    interface Factory {
        fun create(): TrainStopRepository
    }
}