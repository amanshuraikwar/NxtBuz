package io.github.amanshuraikwar.nxtbuz.commonkmm.loop

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

private const val REFRESH_DELAY = 10000L

abstract class Loop<T>(
    private val coroutineScope: CoroutineScope,
    private val dispatcher: CoroutineDispatcher,
    private val refreshDelay: Long = REFRESH_DELAY
) {
    private val sharedFlow = MutableSharedFlow<T>(replay = 1)
    private var producerCoroutineJob: Job? = null
    private var collectorCoroutineJob: Job? = null

    private val loopErrorHandler = CoroutineExceptionHandler { _, throwable ->
        println("loopErrorHandler: ${throwable.message}")
        if (throwable !is CancellationException) {
            startLoopDelayed()
        }
    }

    fun start(): SharedFlow<T> {
        startLoop()
        return sharedFlow
    }

    fun startAndCollect(coroutineContext: CoroutineContext, action: suspend (value: T) -> Unit) {
        collectorCoroutineJob?.cancel()
        collectorCoroutineJob = null
        collectorCoroutineJob = coroutineScope.launch(coroutineContext) {
            start()
                .collect { value ->
                    action(value)
                }
        }
    }

    fun startAndCollect(action: suspend (value: T) -> Unit) {
        collectorCoroutineJob?.cancel()
        collectorCoroutineJob = null
        collectorCoroutineJob = coroutineScope.launch {
            start()
                .collect { value ->
                    action(value)
                }
        }
    }

    private fun startLoopDelayed() {
        producerCoroutineJob?.cancel()
        producerCoroutineJob = null
        producerCoroutineJob = launchCoroutine(refreshDelay)
    }

    private fun startLoop() {
        producerCoroutineJob?.cancel()
        producerCoroutineJob = null
        producerCoroutineJob = launchCoroutine()
    }

    private fun launchCoroutine(
        initialDelay: Long = 0,
    ): Job {
        return coroutineScope.launch(dispatcher + loopErrorHandler) {
            delay(initialDelay)
            while (isActive) {
                sharedFlow.tryEmit(getData())
                delay(refreshDelay)
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
        producerCoroutineJob?.cancel()
        producerCoroutineJob = null
        collectorCoroutineJob?.cancel()
        collectorCoroutineJob = null
    }

    companion object {
        operator fun <T> invoke(
            coroutineScope: CoroutineScope,
            dispatcher: CoroutineDispatcher,
            refreshDelay: Long = REFRESH_DELAY,
            getData: suspend () -> T
        ): Loop<T> {
            return object : Loop<T>(
                coroutineScope = coroutineScope,
                dispatcher = dispatcher,
                refreshDelay = refreshDelay
            ) {
                override suspend fun getData(): T {
                    return getData()
                }
            }
        }
    }
}