package io.github.amanshuraikwar.nxtbuz.map

import androidx.annotation.UiThread
import androidx.lifecycle.LiveData
import io.github.amanshuraikwar.nxtbuz.common.model.Event
import io.github.amanshuraikwar.nxtbuz.common.model.map.MapInitData
import io.github.amanshuraikwar.nxtbuz.common.model.map.MapEvent

interface MapViewModelDelegate {

    val initMap: LiveData<Event<MapInitData>>

    suspend fun initMap(
        lat: Double,
        lng: Double,
        onMapLongClick: (lat: Double, lng: Double) -> Unit
    )

    /**
     * @return New map state id
     */
    suspend fun newState(onMarkerInfoWindowClickListener: (markerId: String) -> Unit = {}): Int

    suspend fun pushMapEvent(mapStateId: Int, mapEvent: MapEvent)

    @UiThread
    fun onReCreate()

    fun detach()
}