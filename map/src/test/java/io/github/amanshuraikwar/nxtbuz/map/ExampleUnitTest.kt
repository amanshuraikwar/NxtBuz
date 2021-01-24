package io.github.amanshuraikwar.nxtbuz.map

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.junit.Test

import org.junit.Assert.*
import kotlin.coroutines.Continuation

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    data class ReturnableFlowData<T, U>(
        val param: T,
        val cont: CancellableContinuation<U>,
    )

    class ReturnableMutableSharedFlow<T, U>(
        private val waitingCoroutineScope: CoroutineScope,
    ) {

        val flow = MutableSharedFlow<ReturnableFlowData<T, U>>()

        suspend fun emit(value: T): U {
            return suspendCancellableCoroutine { cont ->
                waitingCoroutineScope.launch {
                    flow.emit(ReturnableFlowData(value, cont))
                }
            }
        }

        suspend inline fun collect(crossinline action: suspend (value: T) -> U) {
            flow.collect { (value, cont) ->
                cont.resumeWith(Result.success(action(value)))
            }
        }
    }

    @Test
    fun addition_isCorrect() {

        runBlocking {
            val flow = ReturnableMutableSharedFlow<String, String>(this)
            launch {
                for (i in 0..10) {
                    delay(100)
                    println("Emitting $i")
                    val emitResult = flow.emit("param $i")
                    println("Emit result $emitResult")
                }
            }
            flow.collect { value ->
                println("Collecting $value")
                "Result for $value"
            }
        }
    }

    @Test
    fun a() {
        val flow = MutableSharedFlow<Int>()

//        fun main() {
            runBlocking {
                launch {
                    for (i in 0..10) {
                        delay(100)
                        flow.emit(i)
                    }
                }
                delay(500)
                flow.collect {
                    println(it)
                }
            }
//        }
    }
}