package io.github.amanshuraikwar.nxtbuz.domain.loop

//import android.util.Log
//import kotlinx.coroutines.*
//import kotlinx.coroutines.flow.MutableSharedFlow
//import kotlinx.coroutines.flow.SharedFlow
//import kotlinx.coroutines.flow.collect
//import kotlin.coroutines.CoroutineContext
//
//private const val TAG = "Loop"
//private const val REFRESH_DELAY = 10000L
//
//abstract class Loop<T>(
//    private val coroutineScope: CoroutineScope,
//    private val dispatcher: CoroutineDispatcher,
//) {
//    private val sharedFlow = MutableSharedFlow<T>(replay = 1)
//    private var producerCoroutineJob: Job? = null
//    private var collectorCoroutineJob: Job? = null
//
//    private val loopErrorHandler = CoroutineExceptionHandler { _, throwable ->
//        Log.e(TAG, "loopErrorHandler: ${throwable.message}", throwable)
//        if (throwable !is CancellationException) {
//            startLoopDelayed()
//        }
//    }
//
//    fun start(): SharedFlow<T> {
//        startLoop()
//        return sharedFlow
//    }
//
//    @Synchronized
//    fun startAndCollect(coroutineContext: CoroutineContext, action: suspend (value: T) -> Unit) {
//        collectorCoroutineJob?.cancel()
//        collectorCoroutineJob = null
//        collectorCoroutineJob = coroutineScope.launch(coroutineContext) {
//            start()
//                .collect { value ->
//                    action(value)
//                }
//        }
//    }
//
//    @Synchronized
//    private fun startLoopDelayed() {
//        producerCoroutineJob?.cancel()
//        producerCoroutineJob = null
//        producerCoroutineJob = launchCoroutine(REFRESH_DELAY)
//    }
//
//    @Synchronized
//    private fun startLoop() {
//        producerCoroutineJob?.cancel()
//        producerCoroutineJob = null
//        producerCoroutineJob = launchCoroutine()
//    }
//
//    private fun launchCoroutine(
//        initialDelay: Long = 0,
//    ): Job {
//        return coroutineScope.launch(dispatcher + loopErrorHandler) {
//            delay(initialDelay)
//            while (isActive) {
//                sharedFlow.tryEmit(getData())
//                delay(REFRESH_DELAY)
//            }
//        }
//    }
//
//    fun emitNow() {
//        coroutineScope.launch(dispatcher + loopErrorHandler) {
//            sharedFlow.emit(getData())
//        }
//    }
//
//    protected abstract suspend fun getData(): T
//
//    fun stop() {
//        producerCoroutineJob?.cancel()
//        producerCoroutineJob = null
//        collectorCoroutineJob?.cancel()
//        collectorCoroutineJob = null
//    }
//}