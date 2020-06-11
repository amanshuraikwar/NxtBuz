package io.github.amanshuraikwar.nxtbuz.domain.starred

import io.github.amanshuraikwar.nxtbuz.data.busarrival.model.StarredBusArrival
import io.github.amanshuraikwar.nxtbuz.data.starred.StarredBusArrivalRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ExperimentalCoroutinesApi
class AttachStarredBusArrivalsUseCase @Inject constructor(
    private val starredBusArrivalRepository: StarredBusArrivalRepository
) {
    suspend operator fun invoke(id: String): Flow<List<StarredBusArrival>> {
        return starredBusArrivalRepository.attach(id)
    }
}