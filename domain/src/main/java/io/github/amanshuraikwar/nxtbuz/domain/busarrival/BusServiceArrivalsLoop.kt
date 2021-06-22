package io.github.amanshuraikwar.nxtbuz.domain.busarrival

import io.github.amanshuraikwar.nxtbuz.common.model.arrival.BusServiceArrivalsLoopData
import io.github.amanshuraikwar.nxtbuz.domain.loop.Loop
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
            getBusBusArrivalsUseCase(busStopCode, busServiceNumber)
        )
    }
}