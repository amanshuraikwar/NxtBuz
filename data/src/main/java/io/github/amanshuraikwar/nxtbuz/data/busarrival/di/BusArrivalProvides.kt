package io.github.amanshuraikwar.nxtbuz.data.busarrival.di

import dagger.Module
import dagger.Provides
//import io.github.amanshuraikwar.nxtbuz.common.model.BusArrivalsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Named
import javax.inject.Singleton

@Module
class BusArrivalProvides {

//    @Provides
//    @Singleton
//    @Named("busArrivalStateFlow")
//    fun a(): MutableStateFlow<BusArrivalsState> {
//        return MutableStateFlow(BusArrivalsState("", emptyList()))
//    }
//
//    // todo: for some reason this does not work
//    @Provides
//    @Singleton
//    @Named("busArrivalStateFlow")
//    fun b(
//        @Named("busArrivalStateFlow") a: MutableStateFlow<BusArrivalsState>
//    ): StateFlow<BusArrivalsState> {
//        return a
//    }
}