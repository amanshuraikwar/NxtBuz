package io.github.amanshuraikwar.nxtbuz.domain.starred

import io.github.amanshuraikwar.nxtbuz.commonkmm.starred.ToggleStarUpdate
import io.github.amanshuraikwar.nxtbuz.data.starred.StarredBusArrivalRepository
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject

class ToggleStarUpdateUseCase @Inject constructor(
    private val repo: StarredBusArrivalRepository
) {
    operator fun invoke(): SharedFlow<ToggleStarUpdate> {
        return repo.toggleStarUpdate
    }
}