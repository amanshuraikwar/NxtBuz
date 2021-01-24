package io.github.amanshuraikwar.nxtbuz.common

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val flow = MutableSharedFlow<Int>()

        fun main() {
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
        }
    }
}