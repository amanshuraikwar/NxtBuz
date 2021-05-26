package io.github.amanshuraikwar.nxtbuz.domain.busarrival

import io.github.amanshuraikwar.nxtbuz.data.busarrival.service.BusArrivalService
import javax.inject.Inject

class StopBusArrivalFlowUseCase @Inject constructor(
    private val helper: BusArrivalService.Helper
) {
    operator fun invoke() {
        helper.stop()
    }
}