package io.github.amanshuraikwar.nxtbuz.domain.starred

import io.github.amanshuraikwar.nxtbuz.data.starred.StarredBusArrivalRepository
import javax.inject.Inject

class IsStarredUseCase @Inject constructor(
    private val repo: StarredBusArrivalRepository
) {
    suspend operator fun invoke(busStopCode: String, busServiceNumber: String): Boolean {
        return repo.isStarred(
            busStopCode = busStopCode, busServiceNumber = busServiceNumber
        )
    }
}