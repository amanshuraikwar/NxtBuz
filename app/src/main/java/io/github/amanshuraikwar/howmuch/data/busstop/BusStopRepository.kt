package io.github.amanshuraikwar.howmuch.data.busstop

import io.github.amanshuraikwar.howmuch.data.di.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.howmuch.data.prefs.PreferenceStorage
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "BusStopRepository"

@Singleton
class BusStopRepository @Inject constructor(
    private val preferenceStorage: PreferenceStorage,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) {

    suspend fun getBusStopQueryLimit(): Int = withContext(dispatcherProvider.io) {
        preferenceStorage.busStopsQueryLimit
    }

    suspend fun setBusStopQueryLimit(newLimit: Int) = withContext(dispatcherProvider.io) {
        preferenceStorage.busStopsQueryLimit = newLimit
    }

    suspend fun getDefaultLocation(): Pair<Double, Double> = withContext(dispatcherProvider.io) {
        preferenceStorage.defaultLocation
    }

    suspend fun getMaxDistanceOfClosesBusStop(): Int = withContext(dispatcherProvider.io) {
        preferenceStorage.maxDistanceOfClosestBusStop
    }

    suspend fun setMaxDistanceOfClosesBusStop(newMaxDistance: Int) =
        withContext(dispatcherProvider.io) {
            preferenceStorage.maxDistanceOfClosestBusStop = newMaxDistance
        }
}