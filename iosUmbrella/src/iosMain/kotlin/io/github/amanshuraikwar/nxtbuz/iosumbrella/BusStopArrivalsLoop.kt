package io.github.amanshuraikwar.nxtbuz.iosumbrella

import io.github.amanshuraikwar.nxtbuz.commonkmm.arrival.BusStopArrival
import io.github.amanshuraikwar.nxtbuz.commonkmm.loop.Loop
import kotlinx.coroutines.CoroutineDispatcher

class BusStopArrivalsLoop(
    private val busStopCode: String,
    private val getBusArrivalsUseCase: GetBusArrivalsUseCase,
    dispatcher: CoroutineDispatcher,
) : Loop<List<BusStopArrival>>(
    dispatcher = dispatcher,
    coroutineScope = IosDataCoroutineScopeProvider.coroutineScope,
) {
    override suspend fun getData(): List<BusStopArrival> {
        return getBusArrivalsUseCase(busStopCode)
    }

    fun startAndCollectCallback(action: (value: List<BusStopArrival>) -> Unit) {
        startAndCollect {
            action(it)
        }
    }
}