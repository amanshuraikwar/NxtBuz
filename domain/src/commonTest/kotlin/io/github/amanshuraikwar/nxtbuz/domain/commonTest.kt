package io.github.amanshuraikwar.nxtbuz.domain

import kotlin.test.Test
import kotlin.test.assertTrue

class CommonGreetingTest {

    @Test
    fun testExample() {
        assertTrue(Greeting().greeting().contains("Hello"), "Check 'Hello' is mentioned")
    }
}