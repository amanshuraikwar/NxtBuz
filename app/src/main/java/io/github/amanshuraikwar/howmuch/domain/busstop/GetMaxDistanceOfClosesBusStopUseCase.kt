package io.github.amanshuraikwar.howmuch.domain.busstop

import kotlinx.coroutines.delay
import javax.inject.Inject

class GetMaxDistanceOfClosesBusStopUseCase @Inject constructor() {

    suspend operator fun invoke(): Int {
        delay(300)
        return 50_000
    }
}