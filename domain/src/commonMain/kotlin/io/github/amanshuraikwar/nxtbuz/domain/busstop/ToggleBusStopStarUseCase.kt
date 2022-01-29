package io.github.amanshuraikwar.nxtbuz.domain.busstop

import io.github.amanshuraikwar.nxtbuz.commonkmm.BusStop
import io.github.amanshuraikwar.nxtbuz.repository.BusStopRepository
import kotlinx.coroutines.flow.SharedFlow

open class ToggleBusStopStarUseCase constructor(
    private val repo: BusStopRepository
) {
    suspend operator fun invoke(busStopCode: String) {
        repo.toggleBusStopStar(busStopCode)
    }

    suspend operator fun invoke(busStopCode: String, toggleTo: Boolean) {
        repo.toggleBusStopStar(busStopCode, toggleTo)
    }

    suspend fun updates(): SharedFlow<BusStop> {
        return repo.busStopUpdates()
    }
}