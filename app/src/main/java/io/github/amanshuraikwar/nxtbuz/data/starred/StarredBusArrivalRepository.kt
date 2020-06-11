package io.github.amanshuraikwar.nxtbuz.data.starred

import android.util.Log
import io.github.amanshuraikwar.nxtbuz.data.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.data.busarrival.model.StarredBusArrival
import io.github.amanshuraikwar.nxtbuz.data.room.busstops.BusStopDao
import io.github.amanshuraikwar.nxtbuz.data.room.starredbusstops.StarredBusStopEntity
import io.github.amanshuraikwar.nxtbuz.data.room.starredbusstops.StarredBusStopsDao
import io.github.amanshuraikwar.nxtbuz.data.starred.delegate.BusArrivalsDelegate
import io.github.amanshuraikwar.nxtbuz.data.starred.model.StarToggleState
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
@ExperimentalCoroutinesApi
class StarredBusArrivalRepository @Inject constructor(
    private val starredBusStopsDao: StarredBusStopsDao,
    private val busStopDao: BusStopDao,
    private val busArrivalsDelegate: BusArrivalsDelegate,
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

    suspend fun attach(id: String): Flow<List<StarredBusArrival>> {

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
                .onCompletion {
                    detach(id)
                }
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

    private suspend fun getArrivalsAndEmit() = coroutineScope {

        val starredBusStops = starredBusStopsDao.findAll()

        val starredBusArrivalList = starredBusStops
            .map { (busStopCode, busServiceNumber) ->
                async(dispatcherProvider.pool8) {
                    busArrivalsDelegate
                        .getBusArrivals(busStopCode)
                        .find { it.serviceNumber == busServiceNumber }
                        ?.let { busArrival ->
                            StarredBusArrival(
                                busStopCode,
                                busServiceNumber,
                                busStopDao
                                    .findByCode(busStopCode)
                                    .takeIf { it.isNotEmpty() }
                                    ?.get(0)
                                    ?.description
                                    ?: throw Exception(
                                        "No bus stop row found for stop code " +
                                                "$busStopCode in local DB."
                                    ),
                                busArrival.arrivals
                            )
                        }
                        ?: throw Exception(
                            "Bus arrival for bus stop " +
                                    "$busStopCode and service number " +
                                    "$busServiceNumber not fetched."
                        )
                }
            }.awaitAll()

        starredBusArrivalStateFlow.value = starredBusArrivalList
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
                        StarredBusStopEntity(busStopCode, busServiceNumber)
                    )
                )
            }

            starToggleState.value = StarToggleState(
                busStopCode, busServiceNumber, !isAlreadyStarred
            )

            launch { getArrivalsAndEmit() }
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
                        StarredBusStopEntity(busStopCode, busServiceNumber)
                    )
                )

            } else {
                starredBusStopsDao.deleteByBusStopCodeAndBusServiceNumber(
                    busStopCode, busServiceNumber
                )
            }

            starToggleState.value = StarToggleState(busStopCode, busServiceNumber, toggleTo)
        }

        launch { getArrivalsAndEmit() }
    }

    companion object {
        private const val TAG = "StarredBusArrivalRepo"
        private const val DELAY_TIME_MILLIS = 10000L
    }
}