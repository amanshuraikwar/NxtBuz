package io.github.amanshuraikwar.nxtbuz.starreddata

import io.github.amanshuraikwar.nxtbuz.commonkmm.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.commonkmm.starred.StarredBusService
import io.github.amanshuraikwar.nxtbuz.commonkmm.starred.ToggleStarUpdate
import io.github.amanshuraikwar.nxtbuz.localdatasource.LocalDataSource
import io.github.amanshuraikwar.nxtbuz.localdatasource.StarredBusStopEntity
import io.github.amanshuraikwar.nxtbuz.preferencestorage.PreferenceStorage
import io.github.amanshuraikwar.nxtbuz.repository.StarredBusArrivalRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class StarredBusArrivalRepositoryImpl constructor(
    private val localDataSource: LocalDataSource,
    private val preferenceStorage: PreferenceStorage,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) : StarredBusArrivalRepository {

    private val coroutineScope: CoroutineScope by lazy {
        // We use supervisor scope because we don't want
        // the child coroutines to cancel all the parent coroutines
        CoroutineScope(SupervisorJob() + dispatcherProvider.arrivalService)
    }

    private val _toggleStarUpdate = MutableSharedFlow<ToggleStarUpdate>()
    override val toggleStarUpdate: SharedFlow<ToggleStarUpdate> = _toggleStarUpdate

    private val _toggleShouldShowErrorArrivals = MutableSharedFlow<Boolean>()
    override val toggleShouldShowErrorArrivals: SharedFlow<Boolean> = _toggleShouldShowErrorArrivals

    override suspend fun shouldShowErrorStarredBusArrivals(): Boolean =
        withContext(dispatcherProvider.io) {
            preferenceStorage.showErrorStarredBusArrivals
        }

    override suspend fun setShouldShowErrorStarredBusArrivals(shouldShow: Boolean) {
        withContext(dispatcherProvider.io) {
            if (shouldShow != preferenceStorage.showErrorStarredBusArrivals) {
                preferenceStorage.showErrorStarredBusArrivals = shouldShow
                coroutineScope.launch {
                    _toggleShouldShowErrorArrivals.emit(shouldShow)
                }
            }
        }
    }

    override suspend fun getStarredBusServices(): List<StarredBusService> {
        return withContext(dispatcherProvider.computation) {
            localDataSource
                .findAllStarredBuses()
                .map { starredBusStopEntity ->
                    StarredBusService(
                        busStopCode = starredBusStopEntity.busStopCode,
                        busServiceNumber = starredBusStopEntity.busServiceNumber
                    )
                }
        }
    }

    override suspend fun toggleBusStopStar(busStopCode: String, busServiceNumber: String) {
        withContext(dispatcherProvider.io) {
            val isAlreadyStarred = localDataSource.findStarredBus(
                busStopCode = busStopCode,
                busServiceNumber = busServiceNumber,
            ) != null

            if (isAlreadyStarred) {
                localDataSource.deleteStarredBus(
                    busStopCode = busStopCode,
                    busServiceNumber = busServiceNumber
                )

            } else {
                localDataSource.insertStarredBuses(
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

    override suspend fun toggleBusStopStar(
        busStopCode: String,
        busServiceNumber: String,
        toggleTo: Boolean
    ) {
        withContext(dispatcherProvider.io) {
            val isAlreadyStarred =
                localDataSource.findStarredBus(
                    busStopCode = busStopCode,
                    busServiceNumber = busServiceNumber,
                ) != null

            if (toggleTo != isAlreadyStarred) {
                if (toggleTo) {
                    localDataSource.insertStarredBuses(
                        listOf(
                            StarredBusStopEntity(
                                busStopCode,
                                busServiceNumber
                            )
                        )
                    )
                } else {
                    localDataSource.deleteStarredBus(
                        busStopCode = busStopCode,
                        busServiceNumber = busServiceNumber,
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

    override suspend fun isStarred(
        busStopCode: String,
        busServiceNumber: String,
    ): Boolean = withContext(dispatcherProvider.io) {
        return@withContext localDataSource
            .findStarredBus(
                busStopCode = busStopCode,
                busServiceNumber = busServiceNumber
            ) != null
    }
}