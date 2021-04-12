package io.github.amanshuraikwar.nxtbuz.map

import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.github.amanshuraikwar.nxtbuz.common.model.map.MapEvent
import io.github.amanshuraikwar.nxtbuz.common.model.map.MapMarker
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
        FirebaseCrashlytics.getInstance().recordException(th)
    }

    fun init(coroutineScope: CoroutineScope) {
        coroutineScope.launch(errorHandler) {
            val flow = getLocationUpdatesUseCase()

            pushMapEventUseCase(
                MapEvent.AddMarker(
                    MapMarker(
                        "center",
                        flow.value.lat,
                        flow.value.lng,
                        R.drawable.ic_marker_location_20,
                        "",
                        isFlat = true
                    )
                )
            )

            flow.collect { location ->
                pushMapEventUseCase(
                    MapEvent.MoveMarker(
                        "center",
                        LatLng(location.lat, location.lng)
                    )
                )
            }
        }
    }

    private suspend fun start() {
//        for (i in 0..100) {
//            val location = location?.let {
//                LatLng(it.latitude + 0.001, it.longitude + 0.001)
//            } ?: continue
//            pushMapEventUseCase.fireAndForget(
//                MapEvent.MoveMarker(
//                    "center",
//                    location
//                )
//            )
//            this.location = location
//            delay(2000)
//        }
    }
}