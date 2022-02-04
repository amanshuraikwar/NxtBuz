package io.github.amanshuraikwar.nxtbuz.starreddata

import io.github.amanshuraikwar.nxtbuz.commonkmm.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.commonkmm.starred.StarredBusService
import io.github.amanshuraikwar.nxtbuz.commonkmm.starred.ToggleBusServiceStarUpdate
import io.github.amanshuraikwar.nxtbuz.localdatasource.LocalDataSource
import io.github.amanshuraikwar.nxtbuz.localdatasource.StarredBusServiceEntity
import io.github.amanshuraikwar.nxtbuz.preferencestorage.PreferenceStorage
import io.github.amanshuraikwar.nxtbuz.repository.StarredBusArrivalRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

open class StarredBusArrivalRepositoryImpl constructor(
    private val localDataSource: LocalDataSource,
    protected val preferenceStorage: PreferenceStorage,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) : StarredBusArrivalRepository {
    private val coroutineScope: CoroutineScope by lazy {
        // We use supervisor scope because we don't want
        // the child coroutines to cancel all the parent coroutines
        CoroutineScope(SupervisorJob() + dispatcherProvider.computation)
    }

    private val _toggleBusServiceStarUpdate = MutableSharedFlow<ToggleBusServiceStarUpdate>()
    override val toggleBusServiceStarUpdate = _toggleBusServiceStarUpdate

    private val _toggleShouldShowErrorArrivals = MutableSharedFlow<Boolean>()
    override val toggleShouldShowErrorArrivals = _toggleShouldShowErrorArrivals

    override suspend fun shouldShowErrorStarredBusArrivals(): Boolean {
        return withContext(dispatcherProvider.io) {
            preferenceStorage.showErrorStarredBusArrivals
        }
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

    override suspend fun getStarredBusServices(
        atBusStopCode: String?
    ): List<StarredBusService> {
        return getStarredBusServices(localDataSource, atBusStopCode)
    }

    protected suspend fun getStarredBusServices(
        localDataSource: LocalDataSource,
        atBusStopCode: String?
    ): List<StarredBusService> {
        return withContext(dispatcherProvider.computation) {
            localDataSource
                .run {
                    if (atBusStopCode == null) {
                        findAllStarredBuses()
                    } else {
                        findStarredBuses(busStopCode = atBusStopCode)
                    }
                }
                .map { starredBusStopEntity ->
                    StarredBusService(
                        busStopCode = starredBusStopEntity.busStopCode,
                        busServiceNumber = starredBusStopEntity.busServiceNumber
                    )
                }
        }
    }

    override suspend fun toggleBusServiceStar(
        busStopCode: String,
        busServiceNumber: String,
        toggleTo: Boolean?
    ) {
        withContext<Unit>(dispatcherProvider.computation) {
            val isAlreadyStarred = isBusServiceStarred(
                busStopCode = busStopCode,
                busServiceNumber = busServiceNumber,
            )

            if (toggleTo != null) {
                if (toggleTo != isAlreadyStarred) {
                    if (toggleTo) {
                        localDataSource.insertStarredBuses(
                            listOf(
                                StarredBusServiceEntity(
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
                        _toggleBusServiceStarUpdate.emit(
                            ToggleBusServiceStarUpdate(
                                busStopCode,
                                busServiceNumber,
                                toggleTo
                            )
                        )
                    }
                }
            } else {
                if (isAlreadyStarred) {
                    localDataSource.deleteStarredBus(
                        busStopCode = busStopCode,
                        busServiceNumber = busServiceNumber
                    )

                } else {
                    localDataSource.insertStarredBuses(
                        listOf(
                            StarredBusServiceEntity(
                                busStopCode,
                                busServiceNumber
                            )
                        )
                    )
                }

                coroutineScope.launch {
                    _toggleBusServiceStarUpdate.emit(
                        ToggleBusServiceStarUpdate(
                            busStopCode,
                            busServiceNumber,
                            !isAlreadyStarred
                        )
                    )
                }
            }
        }
    }

    override suspend fun isBusServiceStarred(
        busStopCode: String,
        busServiceNumber: String,
    ): Boolean {
        return withContext(dispatcherProvider.computation) {
            localDataSource.findStarredBus(
                busStopCode = busStopCode,
                busServiceNumber = busServiceNumber
            ) != null
        }
    }
}