package io.github.amanshuraikwar.nxtbuz.busstop.di

import dagger.Module
import dagger.Provides
import io.github.amanshuraikwar.nxtbuz.common.model.BusStop
import io.github.amanshuraikwar.nxtbuz.common.model.map.MapEvent
import io.github.amanshuraikwar.nxtbuz.common.util.flow.ReturnableFlow
import io.github.amanshuraikwar.nxtbuz.common.model.map.MapResult
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Named
import javax.inject.Singleton

@Module
class BusStopsProvides {

//    @Provides
//    @Singleton
//    @Named("bottomSheetSlideOffset")
//    fun provideBottomSheetSlideOffsetFlow(): MutableStateFlow<Float> {
//        return MutableStateFlow(0f)
//    }
//
//    @Provides
//    @Singleton
//    @Named("navigateToBusStopArrivals")
//    fun provideNavigateToBusStopArrivals(): MutableSharedFlow<BusStop> {
//        return MutableSharedFlow(replay = 0)
//    }
}