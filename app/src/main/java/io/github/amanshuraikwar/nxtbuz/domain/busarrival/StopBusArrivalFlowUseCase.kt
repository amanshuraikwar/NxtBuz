package io.github.amanshuraikwar.nxtbuz.domain.busarrival

import io.github.amanshuraikwar.nxtbuz.data.busarrival.service.BusArrivalService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
class StopBusArrivalFlowUseCase @Inject constructor(
    private val helper: BusArrivalService.Helper
) {
    operator fun invoke() {
        helper.stop()
    }
}