package io.github.amanshuraikwar.nxtbuz.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import io.github.amanshuraikwar.nxtbuz.data.busarrival.service.BusArrivalService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@Module
abstract class ServiceBindingModule {

    @ExperimentalCoroutinesApi
    @FlowPreview
    @ContributesAndroidInjector
    internal abstract fun a(): BusArrivalService
}
