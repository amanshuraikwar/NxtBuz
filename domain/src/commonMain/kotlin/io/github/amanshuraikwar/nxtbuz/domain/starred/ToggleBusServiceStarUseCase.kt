package io.github.amanshuraikwar.nxtbuz.domain.starred

import io.github.amanshuraikwar.nxtbuz.repository.StarredBusArrivalRepository

open class ToggleBusServiceStarUseCase constructor(
    private val repo: StarredBusArrivalRepository
) {
    suspend operator fun invoke(busStopCode: String, busServiceNumber: String) {
        repo.toggleBusServiceStar(
            busStopCode, busServiceNumber
        )
    }

    suspend operator fun invoke(busStopCode: String, busServiceNumber: String, toggleTo: Boolean) {
        repo.toggleBusServiceStar(
            busStopCode, busServiceNumber, toggleTo
        )
    }
}