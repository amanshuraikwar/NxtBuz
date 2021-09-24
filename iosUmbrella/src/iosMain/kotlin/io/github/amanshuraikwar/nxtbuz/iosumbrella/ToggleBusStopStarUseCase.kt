package io.github.amanshuraikwar.nxtbuz.iosumbrella

import io.github.amanshuraikwar.nxtbuz.starreddata.StarredBusArrivalRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class ToggleBusStopStarUseCase constructor(
    private val repo: StarredBusArrivalRepository
) {
    operator fun invoke(busStopCode: String, busServiceNumber: String) {
        IosDataCoroutineScopeProvider.coroutineScope.launch(
            CoroutineExceptionHandler { _, th ->
                // TODO-amanshuraikwar (24 Sep 2021 11:49:55 AM): gracefully handle error
            }
        ) {
            repo.toggleBusStopStar(
                busStopCode, busServiceNumber
            )
        }
    }

    operator fun invoke(busStopCode: String, busServiceNumber: String, toggleTo: Boolean) {
        IosDataCoroutineScopeProvider.coroutineScope.launch(
            CoroutineExceptionHandler { _, th ->
                // TODO-amanshuraikwar (24 Sep 2021 11:49:55 AM): gracefully handle error
            }
        ) {
            repo.toggleBusStopStar(
                busStopCode, busServiceNumber, toggleTo
            )
        }
    }
}