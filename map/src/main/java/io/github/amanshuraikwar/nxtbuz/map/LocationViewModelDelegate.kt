package io.github.amanshuraikwar.nxtbuz.map

import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import io.github.amanshuraikwar.nxtbuz.common.model.map.MapEvent
import io.github.amanshuraikwar.nxtbuz.common.model.map.MapMarker
import io.github.amanshuraikwar.nxtbuz.common.model.map.MapResult
import io.github.amanshuraikwar.nxtbuz.domain.location.GetLocationUpdatesUseCase
import io.github.amanshuraikwar.nxtbuz.domain.location.PushMapEventUseCase
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "LocationVMDelegate"

class LocationViewModelDelegate @Inject constructor(
    private val getLocationUpdatesUseCase: GetLocationUpdatesUseCase,
    private val pushMapEventUseCase: PushMapEventUseCase,
) {

    private val errorHandler = CoroutineExceptionHandler { _, th ->
        Log.e(TAG, "errorHandler: $th", th)
//        FirebaseCrashlytics.getInstance().recordException(th)
//        _error.postValue(Alert())
    }

    fun init(coroutineScope: CoroutineScope) {
        coroutineScope.launch(errorHandler) {
            var marker: Marker? = null
            val flow = getLocationUpdatesUseCase()
            flow.collect { location ->
                if (marker == null) {
                    val result = pushMapEventUseCase(
                            MapEvent.AddMapMarkers(
                                listOf(
                                    MapMarker(
                                        "center",
                                        location.lat,
                                        location.lng,
                                        R.drawable.ic_marker_location_20,
                                        "",
                                        isFlat = true
                                    )
                                )
                            )
                        ) as? MapResult.AddMapMarkersResult
                    marker = result?.markerList?.get(0)
                } else {
                    marker?.position = LatLng(
                        location.lat,
                        location.lng
                    )
                }
            }
        }
    }
}