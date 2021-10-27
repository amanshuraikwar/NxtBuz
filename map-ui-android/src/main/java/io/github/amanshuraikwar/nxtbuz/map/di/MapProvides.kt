package io.github.amanshuraikwar.nxtbuz.map.di

import com.google.android.gms.maps.model.LatLng
import dagger.Module
import dagger.Provides
import io.github.amanshuraikwar.nxtbuz.common.model.map.MapEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Named
import javax.inject.Singleton

@Module
class MapProvides {
    @Provides
    @Singleton
    @Named("mapEventFlow")
    fun provideMapEventFlow(): MutableSharedFlow<MapEvent> {
        return MutableSharedFlow(replay = 0)
    }

    @Provides
    @Singleton
    @Named("mapCenter")
    fun provideMapCenterFlow(): MutableStateFlow<LatLng?> {
        return MutableStateFlow(null)
    }
}