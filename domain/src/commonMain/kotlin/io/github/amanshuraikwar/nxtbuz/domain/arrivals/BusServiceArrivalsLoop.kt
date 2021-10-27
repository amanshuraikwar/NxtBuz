package io.github.amanshuraikwar.nxtbuz.domain.arrivals

import io.github.amanshuraikwar.nxtbuz.commonkmm.arrival.BusServiceArrivalsLoopData
import io.github.amanshuraikwar.nxtbuz.commonkmm.loop.Loop
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope

class BusServiceArrivalsLoop(
    private val busServiceNumber: String,
    val busStopCode: String,
    private val getBusBusArrivalsUseCase: GetBusArrivalsUseCase,
    coroutineScope: CoroutineScope,
    dispatcher: CoroutineDispatcher,
) : Loop<BusServiceArrivalsLoopData>(
    coroutineScope = coroutineScope,
    dispatcher = dispatcher
) {
    override suspend fun getData(): BusServiceArrivalsLoopData {
        return BusServiceArrivalsLoopData(
            busStopCode = busStopCode,
            busServiceNumber = busServiceNumber,
            busStopArrival = getBusBusArrivalsUseCase(busStopCode, busServiceNumber).busStopArrival
        )
    }
}