package io.github.amanshuraikwar.nxtbuz.busroute.loop

import io.github.amanshuraikwar.nxtbuz.domain.busarrival.GetBusArrivalsUseCase
import io.github.amanshuraikwar.nxtbuz.domain.loop.Loop
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope

class BusServiceArrivalsLoop(
    private val busServiceNumber: String,
    val busStopCode: String,
    private val getBusBusArrivalsUseCase: GetBusArrivalsUseCase,
    coroutineScope: CoroutineScope,
    dispatcher: CoroutineDispatcher,
) : Loop<ArrivalsLoopData>(
    coroutineScope = coroutineScope,
    dispatcher = dispatcher
) {
    override suspend fun getData(): ArrivalsLoopData {
        return ArrivalsLoopData(
            busStopCode = busStopCode,
            busServiceNumber = busServiceNumber,
            getBusBusArrivalsUseCase(busStopCode, busServiceNumber)
        )
    }
}