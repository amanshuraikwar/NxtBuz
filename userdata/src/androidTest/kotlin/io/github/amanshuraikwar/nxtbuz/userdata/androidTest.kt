package io.github.amanshuraikwar.nxtbuz.userdata

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test

actual fun runTest(block: suspend () -> Unit) = runBlocking { block() }

class AndroidGreetingTest {

    @Test
    fun testExample() {
        //assertTrue("Check Android is mentioned", Greeting().greeting().contains("Android"))
    }
}