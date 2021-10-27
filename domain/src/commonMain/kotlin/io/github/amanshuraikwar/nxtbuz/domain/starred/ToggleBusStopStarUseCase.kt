package io.github.amanshuraikwar.nxtbuz.domain.starred

import io.github.amanshuraikwar.nxtbuz.repository.StarredBusArrivalRepository

open class ToggleBusStopStarUseCase constructor(
    private val repo: StarredBusArrivalRepository
) {
    suspend operator fun invoke(busStopCode: String, busServiceNumber: String) {
        repo.toggleBusStopStar(
            busStopCode, busServiceNumber
        )
    }

    suspend operator fun invoke(busStopCode: String, busServiceNumber: String, toggleTo: Boolean) {
        repo.toggleBusStopStar(
            busStopCode, busServiceNumber, toggleTo
        )
    }
}