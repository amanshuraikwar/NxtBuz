package io.github.amanshuraikwar.nxtbuz.commonkmm

expect object DispatcherProviderFactory {
    fun getDispatcherProvider(): CoroutinesDispatcherProvider
}