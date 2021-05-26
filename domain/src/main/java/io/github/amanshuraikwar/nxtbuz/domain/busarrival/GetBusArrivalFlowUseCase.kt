package io.github.amanshuraikwar.nxtbuz.domain.busarrival

import io.github.amanshuraikwar.nxtbuz.common.model.arrival.BusStopArrival
import io.github.amanshuraikwar.nxtbuz.data.busarrival.BusArrivalRepository
import io.github.amanshuraikwar.nxtbuz.data.busarrival.service.BusArrivalService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBusArrivalFlowUseCase @Inject constructor(
    private val busArrivalRepository: BusArrivalRepository,
    private val helper: BusArrivalService.Helper
) {

//    operator fun invoke(busStopCode: String): Flow<List<BusStopArrival>> {
//        helper.start(busStopCode)
//        return busArrivalRepository.getBusArrivalsFlow(busStopCode)
//    }
}