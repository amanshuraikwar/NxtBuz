package io.github.amanshuraikwar.nxtbuz.commonkmm

import kotlinx.coroutines.CoroutineDispatcher

/**
 * Provide coroutines context.
 */
data class CoroutinesDispatcherProvider(
    val main: CoroutineDispatcher,
    val computation: CoroutineDispatcher,
    val io: CoroutineDispatcher,
    val pool8: CoroutineDispatcher,
    val map: CoroutineDispatcher,
    val arrivalService: CoroutineDispatcher,
    val location: CoroutineDispatcher
)