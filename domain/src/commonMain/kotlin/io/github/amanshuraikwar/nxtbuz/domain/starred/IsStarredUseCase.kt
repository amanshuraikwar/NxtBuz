package io.github.amanshuraikwar.nxtbuz.domain.starred

import io.github.amanshuraikwar.nxtbuz.repository.StarredBusArrivalRepository

class IsStarredUseCase constructor(
    private val repo: StarredBusArrivalRepository
) {
    suspend operator fun invoke(busStopCode: String, busServiceNumber: String): Boolean {
        return repo.isStarred(
            busStopCode = busStopCode, busServiceNumber = busServiceNumber
        )
    }
}