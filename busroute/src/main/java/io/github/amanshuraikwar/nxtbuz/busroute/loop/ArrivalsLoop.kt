package io.github.amanshuraikwar.nxtbuz.busroute.loop

import android.util.Log
import io.github.amanshuraikwar.nxtbuz.domain.busarrival.GetBusArrivalsUseCase
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

@ExperimentalCoroutinesApi
class ArrivalsLoop(
    private val busServiceNumber: String,
    private val busStopCode: String,
    private val getBusBusArrivalsUseCase: GetBusArrivalsUseCase,
    private val dispatcher: CoroutineDispatcher,
) {

    private val busArrivalMutableStateFlow = MutableStateFlow<ArrivalsLoopData?>(null)
    private var coroutineJob: Job? = null
    private lateinit var coroutineScope: CoroutineScope

    private val loopErrorHandler = CoroutineExceptionHandler { _, throwable ->
        Log.e(TAG, "loopErrorHandler: ${throwable.message}", throwable)
        if (throwable !is CancellationException) {
            coroutineJob = null
            startLoopDelayed()
        }
    }

    fun start(
        coroutineScope: CoroutineScope,
    ): Flow<ArrivalsLoopData?> {
        this.coroutineScope = coroutineScope
        startLoop()
        return busArrivalMutableStateFlow
    }

    private fun startLoopDelayed() {
        coroutineJob = arrivalLoop(REFRESH_DELAY)
    }

    private fun startLoop() {
        coroutineJob = arrivalLoop()
    }

    private fun arrivalLoop(
        initialDelay: Long = 0,
    ) = coroutineScope.launch(dispatcher + loopErrorHandler) {
        delay(initialDelay)
        while (isActive) {
            val busArrival = getBusBusArrivalsUseCase(busStopCode, busServiceNumber)
            busArrivalMutableStateFlow.value =
                ArrivalsLoopData(
                    busStopCode, busServiceNumber, busArrival
                )
            delay(REFRESH_DELAY)
        }
    }

    fun stop() {
        coroutineJob?.cancel()
        coroutineJob = null
    }

    companion object {
        private const val REFRESH_DELAY = 10000L
        private const val TAG = "ArrivalsLoop"
    }
}