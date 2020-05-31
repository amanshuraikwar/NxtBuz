package io.github.amanshuraikwar.nxtbuz.data.busarrival.delegates

import io.github.amanshuraikwar.nxtbuz.data.busarrival.model.BusArrival
import kotlinx.coroutines.flow.Flow

interface BusArrivalStateFlowDelegate {
    fun getBusArrivalsFlow(
        busStopCode: String
    ): Flow<List<BusArrival>>
}