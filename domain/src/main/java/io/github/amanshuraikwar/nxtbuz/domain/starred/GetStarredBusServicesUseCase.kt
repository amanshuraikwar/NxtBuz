package io.github.amanshuraikwar.nxtbuz.domain.starred

import io.github.amanshuraikwar.nxtbuz.common.model.starred.StarredBusService
import io.github.amanshuraikwar.nxtbuz.data.starred.StarredBusArrivalRepository
import javax.inject.Inject

class GetStarredBusServicesUseCase @Inject constructor(
    private val repo: StarredBusArrivalRepository
) {
    suspend operator fun invoke(): List<StarredBusService> {
        return repo.getStarredBusServices()
    }
}