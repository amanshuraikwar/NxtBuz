package io.github.amanshuraikwar.nxtbuz.data.starred

import io.github.amanshuraikwar.nxtbuz.common.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.common.model.room.StarredBusStopEntity
import io.github.amanshuraikwar.nxtbuz.common.model.starred.StarredBusService
import io.github.amanshuraikwar.nxtbuz.common.model.starred.ToggleStarUpdate
import io.github.amanshuraikwar.nxtbuz.data.prefs.PreferenceStorage
import io.github.amanshuraikwar.nxtbuz.data.room.dao.StarredBusStopsDao
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StarredBusArrivalRepository @Inject constructor(
    private val starredBusStopsDao: StarredBusStopsDao,
    private val preferenceStorage: PreferenceStorage,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) {

    private val coroutineScope: CoroutineScope by lazy {
        // We use supervisor scope because we don't want
        // the child coroutines to cancel all the parent coroutines
        CoroutineScope(SupervisorJob() + dispatcherProvider.arrivalService)
    }

    private val _toggleStarUpdate = MutableSharedFlow<ToggleStarUpdate>()
    val toggleStarUpdate: SharedFlow<ToggleStarUpdate> = _toggleStarUpdate

    private val _toggleShouldShowErrorArrivals = MutableSharedFlow<Boolean>()
    val toggleShouldShowErrorArrivals: SharedFlow<Boolean> = _toggleShouldShowErrorArrivals

    suspend fun shouldShowErrorStarredBusArrivals(): Boolean = withContext(dispatcherProvider.io) {
        preferenceStorage.showErrorStarredBusArrivals
    }

    suspend fun setShouldShowErrorStarredBusArrivals(shouldShow: Boolean) {
        withContext(dispatcherProvider.io) {
            if (shouldShow != preferenceStorage.showErrorStarredBusArrivals) {
                preferenceStorage.showErrorStarredBusArrivals = shouldShow
                coroutineScope.launch {
                    _toggleShouldShowErrorArrivals.emit(shouldShow)
                }
            }
        }
    }

    suspend fun getStarredBusServices(): List<StarredBusService> {
        return withContext(dispatcherProvider.computation) {
            starredBusStopsDao
                .findAll()
                .map { starredBusStopEntity ->
                    StarredBusService(
                        busStopCode = starredBusStopEntity.busStopCode,
                        busServiceNumber = starredBusStopEntity.busServiceNumber
                    )
                }
        }
    }

    suspend fun toggleBusStopStar(busStopCode: String, busServiceNumber: String) {
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
        }
    }

    suspend fun toggleBusStopStar(
        busStopCode: String,
        busServiceNumber: String,
        toggleTo: Boolean
    ) {
        withContext(dispatcherProvider.io) {
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
}