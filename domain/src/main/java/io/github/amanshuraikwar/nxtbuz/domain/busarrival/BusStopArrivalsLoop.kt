package io.github.amanshuraikwar.nxtbuz.domain.busarrival

import io.github.amanshuraikwar.nxtbuz.commonkmm.arrival.BusStopArrival
import io.github.amanshuraikwar.nxtbuz.commonkmm.loop.Loop
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope

class BusStopArrivalsLoop(
    val busStopCode: String,
    private val getBusArrivalsUseCase: GetBusArrivalsUseCase,
    dispatcher: CoroutineDispatcher,
    coroutineScope: CoroutineScope,
) : Loop<List<BusStopArrival>>(
    dispatcher = dispatcher,
    coroutineScope = coroutineScope,
) {
    override suspend fun getData(): List<BusStopArrival> {
        return getBusArrivalsUseCase(busStopCode)
    }
}