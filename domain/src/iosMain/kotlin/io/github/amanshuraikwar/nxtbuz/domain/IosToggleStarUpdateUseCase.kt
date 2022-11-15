package io.github.amanshuraikwar.nxtbuz.domain

import io.github.amanshuraikwar.nxtbuz.commonkmm.starred.ToggleBusServiceStarUpdate
import io.github.amanshuraikwar.nxtbuz.domain.model.IosResult
import io.github.amanshuraikwar.nxtbuz.domain.starred.ToggleStarUpdateUseCase
import io.github.amanshuraikwar.nxtbuz.repository.StarredBusArrivalRepository

class IosToggleStarUpdateUseCase constructor(
    repo: StarredBusArrivalRepository
) : ToggleStarUpdateUseCase(
    repo = repo
) {
    operator fun invoke(
        callback: (IosResult<ToggleBusServiceStarUpdate>) -> Unit
    ) {
        callback fromFlow {
            invoke()
        }
    }
}