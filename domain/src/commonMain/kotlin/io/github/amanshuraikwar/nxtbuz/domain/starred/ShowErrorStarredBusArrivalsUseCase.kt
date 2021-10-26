package io.github.amanshuraikwar.nxtbuz.domain.starred

import io.github.amanshuraikwar.nxtbuz.repository.StarredBusArrivalRepository
import kotlinx.coroutines.flow.SharedFlow

class ShowErrorStarredBusArrivalsUseCase constructor(
    private val repo: StarredBusArrivalRepository
) {
    suspend operator fun invoke(): Boolean {
        return repo.shouldShowErrorStarredBusArrivals()
    }

    suspend operator fun invoke(shouldShow: Boolean) {
        repo.setShouldShowErrorStarredBusArrivals(shouldShow)
    }

    fun updates(): SharedFlow<Boolean> {
        return repo.toggleShouldShowErrorArrivals
    }
}