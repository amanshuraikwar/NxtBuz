package io.github.amanshuraikwar.nxtbuz.data

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors
import javax.inject.Inject

/**
 * Provide coroutines context.
 */
data class CoroutinesDispatcherProvider(
    val main: CoroutineDispatcher,
    val computation: CoroutineDispatcher,
    val io: CoroutineDispatcher,
    val pool8: CoroutineDispatcher,
    val map: CoroutineDispatcher,
    val arrivalService: CoroutineDispatcher
) {

    @Inject
    constructor() : this(
        Main,
        Default,
        IO,
        Executors.newFixedThreadPool(8).asCoroutineDispatcher(),
        Executors.newFixedThreadPool(1).asCoroutineDispatcher(),
        Executors.newSingleThreadExecutor().asCoroutineDispatcher()
    )
}
