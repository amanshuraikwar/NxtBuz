package io.github.amanshuraikwar.nxtbuz.ui.main.fragment.map

import androidx.annotation.UiThread
import androidx.lifecycle.LiveData
import io.github.amanshuraikwar.nxtbuz.domain.result.Event
import io.github.amanshuraikwar.nxtbuz.ui.main.fragment.map.model.MapInitData
import io.github.amanshuraikwar.nxtbuz.ui.main.fragment.model.MapEvent
import kotlinx.coroutines.CoroutineScope

interface MapViewModelDelegate {

    val initMap: LiveData<Event<MapInitData>>

    suspend fun initMap(
        lat: Double,
        lng: Double,
        onMapLongClick: (lat: Double, lng: Double) -> Unit
    )

    suspend fun pushMapEvent(mapEvent: MapEvent)

    @UiThread
    fun onReCreate()

    fun detach()
}