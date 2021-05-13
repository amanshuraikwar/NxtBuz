package io.github.amanshuraikwar.nxtbuz.map.di

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import dagger.Module
import dagger.Provides
import io.github.amanshuraikwar.nxtbuz.common.model.map.MapEvent
import io.github.amanshuraikwar.nxtbuz.common.util.flow.ReturnableFlow
import io.github.amanshuraikwar.nxtbuz.common.model.map.MapResult
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Named
import javax.inject.Singleton

@Module
class MapProvides {

    @Provides
    @Singleton
    @Named("mapEventFlow")
    fun provideMapEventFlow(): MutableSharedFlow<MapEvent> {
        // TODO: 24/1/21 dynamically decide replay
        return MutableSharedFlow(replay = 0)
    }

    @Provides
    @Singleton
    @Named("markerClicked")
    fun provideMarkerClickedFlow(): MutableStateFlow<Marker?> {
        return MutableStateFlow(null)
    }

    @Provides
    @Singleton
    @Named("mapCenter")
    fun provideMapCenterFlow(): MutableStateFlow<LatLng?> {
        return MutableStateFlow(null)
    }
}