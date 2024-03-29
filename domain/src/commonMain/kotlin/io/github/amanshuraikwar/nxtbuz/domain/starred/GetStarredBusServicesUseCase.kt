package io.github.amanshuraikwar.nxtbuz.domain.starred

import io.github.amanshuraikwar.nxtbuz.commonkmm.starred.StarredBusService
import io.github.amanshuraikwar.nxtbuz.repository.StarredBusArrivalRepository

class GetStarredBusServicesUseCase constructor(
    private val repo: StarredBusArrivalRepository
) {
    suspend operator fun invoke(): List<StarredBusService> {
        return repo.getStarredBusServices()
    }
}