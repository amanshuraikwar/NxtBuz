package io.github.amanshuraikwar.nxtbuz.starreddata

import io.github.amanshuraikwar.nxtbuz.commonkmm.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.commonkmm.starred.StarredBusService
import io.github.amanshuraikwar.nxtbuz.localdatasource.LocalDataSource
import io.github.amanshuraikwar.nxtbuz.preferencestorage.PreferenceStorage

class StarredBusArrivalRepositoryAndroidImpl constructor(
    sqlDelightLocalDataSource: LocalDataSource,
    private val roomLocalDataSource: LocalDataSource,
    preferenceStorage: PreferenceStorage,
    dispatcherProvider: CoroutinesDispatcherProvider
) : StarredBusArrivalRepositoryImpl(
    sqlDelightLocalDataSource,
    preferenceStorage,
    dispatcherProvider
) {
    override suspend fun getStarredBusServices(atBusStopCode: String?): List<StarredBusService> {
        return when {
            preferenceStorage.onboardingCompleted
                    && !preferenceStorage.sqlDelightAndroidMigrationComplete -> {
                getStarredBusServices(
                    roomLocalDataSource,
                    atBusStopCode
                )
            }
            else -> {
                super.getStarredBusServices(atBusStopCode)
            }
        }
    }
}