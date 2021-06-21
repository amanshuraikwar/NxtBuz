package io.github.amanshuraikwar.nxtbuz.domain.starred

import io.github.amanshuraikwar.nxtbuz.common.model.starred.StarredBusArrival
import io.github.amanshuraikwar.nxtbuz.data.starred.StarredBusArrivalRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AttachStarredBusArrivalsUseCase @Inject constructor(
    private val starredBusArrivalRepository: StarredBusArrivalRepository
) {
    suspend operator fun invoke(
        id: String,
        considerFilteringError: Boolean = false
    ): Flow<List<StarredBusArrival>> {
        TODO()
    }
}