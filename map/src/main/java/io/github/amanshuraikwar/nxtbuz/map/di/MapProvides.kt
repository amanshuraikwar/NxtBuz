package io.github.amanshuraikwar.nxtbuz.map.di

import dagger.Module
import dagger.Provides
import io.github.amanshuraikwar.nxtbuz.common.model.map.MapEvent
import io.github.amanshuraikwar.nxtbuz.common.util.flow.ReturnableFlow
import io.github.amanshuraikwar.nxtbuz.common.model.map.MapResult
import javax.inject.Named
import javax.inject.Singleton

@Module
class MapProvides {

    @Provides
    @Singleton
    @Named("mapEventFlow")
    fun provideMapEventFlow(): ReturnableFlow<MapEvent, MapResult> {
        // TODO: 24/1/21 dynamically decide replay
        return ReturnableFlow(replay = 100)
    }
}