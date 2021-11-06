package io.github.amanshuraikwar.nxtbuz.userdata

import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertTrue

actual fun runTest(block: suspend () -> Unit) = runBlocking { block() }

class IosGreetingTest {

    @Test
    fun testExample() {
        //assertTrue(Greeting().greeting().contains("iOS"), "Check iOS is mentioned")
    }
}