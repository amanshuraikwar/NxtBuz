package io.github.amanshuraikwar.nxtbuz.domain.starred

import io.github.amanshuraikwar.nxtbuz.commonkmm.starred.ToggleStarUpdate
import io.github.amanshuraikwar.nxtbuz.repository.StarredBusArrivalRepository
import kotlinx.coroutines.flow.SharedFlow

open class ToggleStarUpdateUseCase constructor(
    private val repo: StarredBusArrivalRepository
) {
    operator fun invoke(): SharedFlow<ToggleStarUpdate> {
        return repo.toggleStarUpdate
    }
}