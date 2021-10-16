package io.github.amanshuraikwar.nxtbuz.commonkmm

import kotlinx.coroutines.Dispatchers

actual object DispatcherProviderFactory {
    actual fun getDispatcherProvider(): CoroutinesDispatcherProvider {
        return CoroutinesDispatcherProvider(
            Dispatchers.Main,
            Dispatchers.Default,
            Dispatchers.Default,
            Dispatchers.Default,
            Dispatchers.Default,
            Dispatchers.Default,
            Dispatchers.Default
        )
    }
}