package io.github.amanshuraikwar.howmuch.domain.busstop

import kotlinx.coroutines.delay
import javax.inject.Inject

class GetBusStopsLimitUseCase @Inject constructor() {

    suspend operator fun invoke(): Int {
        delay(1000)
        return 30
    }
}
