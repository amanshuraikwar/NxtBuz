package io.github.amanshuraikwar.howmuch.domain.busstop

import io.github.amanshuraikwar.howmuch.data.busstop.BusStopRepository
import io.github.amanshuraikwar.howmuch.data.prefs.PreferenceStorage
import kotlinx.coroutines.delay
import javax.inject.Inject

class BusStopsQueryLimitUseCase @Inject constructor(
    private val busStopRepository: BusStopRepository
) {

    suspend operator fun invoke(): Int {
        delay(500)
        return busStopRepository.getBusStopQueryLimit()
    }

    suspend operator fun invoke(newLimit: Int) {
        busStopRepository.setBusStopQueryLimit(newLimit)
    }
}
