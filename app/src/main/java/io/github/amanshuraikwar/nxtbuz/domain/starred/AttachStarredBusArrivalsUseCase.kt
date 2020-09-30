package io.github.amanshuraikwar.nxtbuz.domain.starred

import io.github.amanshuraikwar.nxtbuz.common.model.StarredBusArrival
import io.github.amanshuraikwar.nxtbuz.starred.data.StarredBusArrivalRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ExperimentalCoroutinesApi
class AttachStarredBusArrivalsUseCase @Inject constructor(
    private val starredBusArrivalRepository: io.github.amanshuraikwar.nxtbuz.starred.data.StarredBusArrivalRepository
) {
    suspend operator fun invoke(
        id: String,
        considerFilteringError: Boolean = false
    ): Flow<List<StarredBusArrival>> {
        return starredBusArrivalRepository.attach(id, considerFilteringError)
    }
}