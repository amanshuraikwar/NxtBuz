package io.github.amanshuraikwar.nxtbuz.map

import android.util.Log
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.LatLng
import io.github.amanshuraikwar.nxtbuz.common.model.Alert
import io.github.amanshuraikwar.nxtbuz.common.model.map.MapEvent
import io.github.amanshuraikwar.nxtbuz.domain.location.GetLocationUpdatesUseCase
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "LocationViewModelDelega"
class LocationViewModelDelegate @Inject constructor(
    private val getLocationUpdatesUseCase: GetLocationUpdatesUseCase,
    private val mapViewModelDelegate: MapViewModelDelegate,
) {

    private val errorHandler = CoroutineExceptionHandler { _, th ->
        Log.e(TAG, "errorHandler: $th", th)
//        FirebaseCrashlytics.getInstance().recordException(th)
//        _error.postValue(Alert())
    }

    fun init(coroutineScope: CoroutineScope) {
        coroutineScope.launch(errorHandler) {
            var circle: Circle? = null
            val flow = getLocationUpdatesUseCase()
            flow.collect { location ->
                if (circle == null) {
                    circle = mapViewModelDelegate.addCircle(
                        MapEvent.MapCircle(
                            location.lat,
                            location.lng,
                            100.0
                        )
                    )
                } else {
                    circle?.center = LatLng(
                        location.lat,
                        location.lng
                    )
                }
            }
        }
    }
}