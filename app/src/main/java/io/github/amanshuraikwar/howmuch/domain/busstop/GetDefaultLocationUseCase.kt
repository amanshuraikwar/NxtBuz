package io.github.amanshuraikwar.howmuch.domain.busstop

import kotlinx.coroutines.delay
import javax.inject.Inject

class GetDefaultLocationUseCase @Inject constructor() {

    suspend operator fun invoke(): Pair<Double, Double> {
        delay(1000)
        return 1.3416 to 103.7757
    }
}