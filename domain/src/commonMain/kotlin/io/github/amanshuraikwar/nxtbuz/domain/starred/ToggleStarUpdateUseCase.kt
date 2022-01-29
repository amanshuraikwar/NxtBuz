package io.github.amanshuraikwar.nxtbuz.domain.starred

import io.github.amanshuraikwar.nxtbuz.commonkmm.starred.ToggleBusServiceStarUpdate
import io.github.amanshuraikwar.nxtbuz.repository.StarredBusArrivalRepository
import kotlinx.coroutines.flow.SharedFlow

open class ToggleStarUpdateUseCase constructor(
    private val repo: StarredBusArrivalRepository
) {
    operator fun invoke(): SharedFlow<ToggleBusServiceStarUpdate> {
        return repo.toggleBusServiceStarUpdate
    }
}