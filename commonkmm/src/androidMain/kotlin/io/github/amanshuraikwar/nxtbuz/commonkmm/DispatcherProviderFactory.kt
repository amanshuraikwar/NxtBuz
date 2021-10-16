package io.github.amanshuraikwar.nxtbuz.commonkmm

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors

actual object DispatcherProviderFactory {
    actual fun getDispatcherProvider(): CoroutinesDispatcherProvider {
        return CoroutinesDispatcherProvider(
            Dispatchers.Main,
            Dispatchers.Default,
            Dispatchers.IO,
            Executors.newFixedThreadPool(8).asCoroutineDispatcher(),
            Executors.newSingleThreadExecutor().asCoroutineDispatcher(),
            Executors.newSingleThreadExecutor().asCoroutineDispatcher(),
            Executors.newSingleThreadExecutor().asCoroutineDispatcher()
        )
    }
}