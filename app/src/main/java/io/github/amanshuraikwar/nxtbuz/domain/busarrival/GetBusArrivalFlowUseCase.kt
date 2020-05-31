package io.github.amanshuraikwar.nxtbuz.domain.busarrival

import io.github.amanshuraikwar.nxtbuz.data.busarrival.BusArrivalRepository
import io.github.amanshuraikwar.nxtbuz.data.busarrival.model.BusArrival
import io.github.amanshuraikwar.nxtbuz.data.busarrival.service.BusArrivalService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
class GetBusArrivalFlowUseCase @Inject constructor(
    private val busArrivalRepository: BusArrivalRepository,
    private val helper: BusArrivalService.Helper
) {

    operator fun invoke(busStopCode: String): Flow<List<BusArrival>> {
        helper.start(busStopCode)
        return busArrivalRepository.getBusArrivalsFlow(busStopCode)
    }
}