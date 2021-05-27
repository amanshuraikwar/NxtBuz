package io.github.amanshuraikwar.nxtbuz.domain.loop

import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

private const val TAG = "Loop"
private const val REFRESH_DELAY = 10000L

abstract class Loop<T>(
    private val coroutineScope: CoroutineScope,
    private val dispatcher: CoroutineDispatcher,
) {
    private val sharedFlow = MutableSharedFlow<T>(replay = 1)
    private var coroutineJob: Job? = null

    private val loopErrorHandler = CoroutineExceptionHandler { _, throwable ->
        Log.e(TAG, "loopErrorHandler: ${throwable.message}", throwable)
        if (throwable !is CancellationException) {
            startLoopDelayed()
        }
    }

    fun start(): SharedFlow<T> {
        startLoop()
        return sharedFlow
    }

    private fun startLoopDelayed() {
        coroutineJob?.cancel()
        coroutineJob = null
        coroutineJob = launchCoroutine(REFRESH_DELAY)
    }

    private fun startLoop() {
        coroutineJob?.cancel()
        coroutineJob = null
        coroutineJob = launchCoroutine()
    }

    private fun launchCoroutine(
        initialDelay: Long = 0,
    ): Job {
        return coroutineScope.launch(dispatcher + loopErrorHandler) {
            delay(initialDelay)
            while (isActive) {
                sharedFlow.tryEmit(getData())
                delay(REFRESH_DELAY)
            }
        }
    }

    fun emitNow() {
        coroutineScope.launch(dispatcher + loopErrorHandler) {
            sharedFlow.emit(getData())
        }
    }

    protected abstract suspend fun getData(): T

    fun stop() {
        coroutineJob?.cancel()
        coroutineJob = null
    }
}