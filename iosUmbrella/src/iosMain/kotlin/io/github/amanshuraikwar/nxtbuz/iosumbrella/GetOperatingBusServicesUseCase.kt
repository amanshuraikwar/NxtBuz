package io.github.amanshuraikwar.nxtbuz.iosumbrella

import co.touchlab.stately.freeze
import io.github.amanshuraikwar.nxtbuz.busarrivaldata.BusArrivalRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class GetOperatingBusServicesUseCase constructor(
    private val busArrivalRepository: BusArrivalRepository,
) {
    suspend operator fun invoke(busStopCode: String): List<String> {
        return busArrivalRepository.getOperatingBusServices(busStopCode)
    }

    operator fun invoke(busStopCode: String, callback: (List<String>) -> Unit) {
        IosDataCoroutineScopeProvider.coroutineScope.launch(
            CoroutineExceptionHandler { _, th ->
            }
        ) {
            callback(
                invoke(busStopCode).freeze()
            )
        }
    }
}