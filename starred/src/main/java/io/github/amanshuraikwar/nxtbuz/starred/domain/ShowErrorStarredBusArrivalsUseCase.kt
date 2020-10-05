package io.github.amanshuraikwar.nxtbuz.starred.domain

import io.github.amanshuraikwar.nxtbuz.starred.data.StarredBusArrivalRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
class ShowErrorStarredBusArrivalsUseCase @Inject constructor(
    private val repo: io.github.amanshuraikwar.nxtbuz.starred.data.StarredBusArrivalRepository
) {
    suspend operator fun invoke(): Boolean {
        return repo.shouldShowErrorStarredBusArrivals()
    }

    suspend operator fun invoke(shouldShow: Boolean) {
        repo.setShouldShowErrorStarredBusArrivals(shouldShow)
    }
}