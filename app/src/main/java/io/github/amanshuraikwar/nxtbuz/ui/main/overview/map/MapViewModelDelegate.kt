package io.github.amanshuraikwar.nxtbuz.ui.main.overview.map

import androidx.annotation.UiThread
import androidx.lifecycle.LiveData
import com.google.android.gms.maps.OnMapReadyCallback
import io.github.amanshuraikwar.nxtbuz.domain.result.Event
import io.github.amanshuraikwar.nxtbuz.ui.main.overview.model.MapEvent
import kotlinx.coroutines.CoroutineScope

interface MapViewModelDelegate {

    val initMap: LiveData<Event<MapInitData>>

    suspend fun CoroutineScope.initMap(lat: Double, lng: Double)

    suspend fun pushMapEvent(mapEvent: MapEvent)

    @UiThread
    fun onReCreate()

    fun detach()
}