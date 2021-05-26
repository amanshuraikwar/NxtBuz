package io.github.amanshuraikwar.nxtbuz.data.starred

import android.util.Log
import io.github.amanshuraikwar.nxtbuz.common.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.common.model.arrival.BusArrivals
import io.github.amanshuraikwar.nxtbuz.common.model.StarredBusArrival
import io.github.amanshuraikwar.nxtbuz.data.prefs.PreferenceStorage
import io.github.amanshuraikwar.nxtbuz.data.room.dao.BusStopDao
import io.github.amanshuraikwar.nxtbuz.common.model.room.StarredBusStopEntity
import io.github.amanshuraikwar.nxtbuz.data.room.dao.StarredBusStopsDao
//import io.github.amanshuraikwar.nxtbuz.data.starred.delegate.BusArrivalsDelegate
import io.github.amanshuraikwar.nxtbuz.common.model.StarToggleState
import io.github.amanshuraikwar.nxtbuz.common.model.starred.ToggleStarUpdate
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class StarredBusArrivalRepository @Inject constructor(
    private val starredBusStopsDao: StarredBusStopsDao,
    private val busStopDao: BusStopDao,
    private val preferenceStorage: PreferenceStorage,
//    private val busArrivalsDelegate: BusArrivalsDelegate,
    @Named("starToggleState") private val starToggleState: MutableStateFlow<StarToggleState>,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) {

    private val coroutineScope: CoroutineScope by lazy {
        // We use supervisor scope because we don't want
        // the child coroutines to cancel all the parent coroutines
        CoroutineScope(SupervisorJob() + dispatcherProvider.arrivalService)
    }

    private val loopErrorHandler = CoroutineExceptionHandler { _, throwable ->
        Log.e(TAG, "loopErrorHandler: ${throwable.message}", throwable)
        if (throwable !is CancellationException) {
            startLoopDelayed()
        }
    }

    private val starredBusArrivalStateFlow = MutableStateFlow<List<StarredBusArrival>>(emptyList())
    private var attachedComponentSet = mutableSetOf<String>()
    private val mutex = Mutex()

    private val _toggleStarUpdate = MutableSharedFlow<ToggleStarUpdate>()
    val toggleStarUpdate: SharedFlow<ToggleStarUpdate> = _toggleStarUpdate

    suspend fun shouldShowErrorStarredBusArrivals(): Boolean = withContext(dispatcherProvider.io) {
        preferenceStorage.showErrorStarredBusArrivals
    }

    suspend fun setShouldShowErrorStarredBusArrivals(shouldShow: Boolean) =
        withContext(dispatcherProvider.io) {
            if (shouldShow != preferenceStorage.showErrorStarredBusArrivals) {
                preferenceStorage.showErrorStarredBusArrivals = shouldShow
                coroutineScope.launch { getArrivalsAndEmit() }
            }
        }

    suspend fun attach(id: String, considerFilteringError: Boolean): Flow<List<StarredBusArrival>> {

        mutex.withLock {

            if (attachedComponentSet.contains(id)) {
                Log.w(
                    TAG,
                    "attach: Component with id $id already attached. " +
                            "This will cause issues in future because detach(...) does not " +
                            "take into account the number of components attached with same id."
                )
            } else {
                attachedComponentSet.add(id)
                if (attachedComponentSet.size == 1) {
                    startLoop()
                }
            }

            // we return a new instance of flow to the client
            // regardless of if it was already attached or not
            return starredBusArrivalStateFlow
                .map { arrivalList ->
                    // if we should consider filtering error bus arrivals
                    // and
                    // if we should not show error bus arrivals
                    // then
                    // filter items for which arrivals is Arrivals.Arriving
                    if (considerFilteringError && !shouldShowErrorStarredBusArrivals()) {
                        arrivalList.filter { it.busArrivals is BusArrivals.Arriving }
                    } else {
                        arrivalList
                    }
                }
//                .onCompletion {
//                    detach(id)
//                }
        }
    }

    /**
     * Calling this function does not confirm that the flow will not emit any more elements.
     * That must be ensured by the coroutine in which the corresponding flows are being observed.
     */
    private fun detach(id: String) = coroutineScope.launch {
        mutex.withLock {
            attachedComponentSet.remove(id)
            if (attachedComponentSet.isEmpty()) {
                stopLoop()
            }
        }
    }

    private fun startLoop() {
        arrivalLoop()
    }

    private fun startLoopDelayed() {
        arrivalLoop(DELAY_TIME_MILLIS)
    }

    private fun stopLoop() {
        coroutineScope.cancel()
    }

    private fun arrivalLoop(
        initialDelay: Long = 0L
    ) = coroutineScope.launch(loopErrorHandler) {

        delay(initialDelay)

        while (isActive) {
            getArrivalsAndEmit()
            delay(DELAY_TIME_MILLIS)
        }
    }

    private suspend fun getArrivalsAndEmit() {

        val starredBusStops = starredBusStopsDao.findAll()

//        val starredBusArrivalList = starredBusStops
//            .map { (busStopCode, busServiceNumber) ->
//                //async(dispatcherProvider.pool8) {
//                busArrivalsDelegate
//                    .getBusArrivals(busStopCode)
//                    .find { it.busServiceNumber == busServiceNumber }
//                    ?.let { busArrival ->
//                        StarredBusArrival(
//                            busStopCode,
//                            busServiceNumber,
//                            busStopDao
//                                .findByCode(busStopCode)
//                                .takeIf { it.isNotEmpty() }
//                                ?.get(0)
//                                ?.description
//                                ?: throw Exception(
//                                    "No bus stop row found for stop code " +
//                                            "$busStopCode in local DB."
//                                ),
//                            busArrival.busArrivals
//                        )
//                    }
//                    ?: throw Exception(
//                        "Bus arrival for bus stop " +
//                                "$busStopCode and service number " +
//                                "$busServiceNumber not fetched."
//                    )
//                //}
//            }/*.awaitAll()*/

//        starredBusArrivalStateFlow.value = starredBusArrivalList
    }

    suspend fun toggleBusStopStar(busStopCode: String, busServiceNumber: String): Unit =
        withContext(dispatcherProvider.io) {

            val isAlreadyStarred =
                starredBusStopsDao.findByBusStopCodeAndBusServiceNumber(
                    busStopCode,
                    busServiceNumber
                ).isNotEmpty()

            if (isAlreadyStarred) {

                starredBusStopsDao.deleteByBusStopCodeAndBusServiceNumber(
                    busStopCode, busServiceNumber
                )

            } else {
                starredBusStopsDao.insertAll(
                    listOf(
                        StarredBusStopEntity(
                            busStopCode,
                            busServiceNumber
                        )
                    )
                )
            }

            coroutineScope.launch {
                _toggleStarUpdate.emit(
                    ToggleStarUpdate(
                        busStopCode,
                        busServiceNumber,
                        !isAlreadyStarred
                    )
                )
            }

            coroutineScope.launch { getArrivalsAndEmit() }
        }

    suspend fun toggleBusStopStar(
        busStopCode: String,
        busServiceNumber: String,
        toggleTo: Boolean
    ): Unit = withContext(dispatcherProvider.io) {

        val isAlreadyStarred =
            starredBusStopsDao.findByBusStopCodeAndBusServiceNumber(
                busStopCode,
                busServiceNumber
            ).isNotEmpty()

        if (toggleTo != isAlreadyStarred) {

            if (toggleTo) {

                starredBusStopsDao.insertAll(
                    listOf(
                        StarredBusStopEntity(
                            busStopCode,
                            busServiceNumber
                        )
                    )
                )

            } else {
                starredBusStopsDao.deleteByBusStopCodeAndBusServiceNumber(
                    busStopCode, busServiceNumber
                )
            }

            coroutineScope.launch {
                _toggleStarUpdate.emit(
                    ToggleStarUpdate(
                        busStopCode,
                        busServiceNumber,
                        toggleTo
                    )
                )
            }
        }

        coroutineScope.launch {
            getArrivalsAndEmit()
        }
    }

    suspend fun isStarred(
        busStopCode: String,
        busServiceNumber: String,
    ): Boolean = withContext(dispatcherProvider.io) {
        return@withContext starredBusStopsDao
            .findByBusStopCodeAndBusServiceNumber(
                busStopCode,
                busServiceNumber
            )
            .isNotEmpty()
    }

    companion object {
        private const val TAG = "StarredBusArrivalRepo"
        private const val DELAY_TIME_MILLIS = 10000L
    }
}