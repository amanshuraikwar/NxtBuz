package io.github.amanshuraikwar.nxtbuz.domain.starred

import io.github.amanshuraikwar.nxtbuz.data.starred.StarredBusArrivalRepository
import javax.inject.Inject

class ShowErrorStarredBusArrivalsUseCase @Inject constructor(
    private val repo: StarredBusArrivalRepository
) {
    suspend operator fun invoke(): Boolean {
        return repo.shouldShowErrorStarredBusArrivals()
    }

    suspend operator fun invoke(shouldShow: Boolean) {
        repo.setShouldShowErrorStarredBusArrivals(shouldShow)
    }
}