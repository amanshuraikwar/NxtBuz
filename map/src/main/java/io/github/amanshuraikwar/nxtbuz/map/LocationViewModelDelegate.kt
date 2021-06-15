package io.github.amanshuraikwar.nxtbuz.map

import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.github.amanshuraikwar.nxtbuz.common.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.common.model.location.LocationOutput
import io.github.amanshuraikwar.nxtbuz.common.model.map.MapEvent
import io.github.amanshuraikwar.nxtbuz.common.model.map.MapMarker
import io.github.amanshuraikwar.nxtbuz.domain.location.GetLastKnownLocationUseCase
import io.github.amanshuraikwar.nxtbuz.domain.location.GetLocationUpdatesUseCase
import io.github.amanshuraikwar.nxtbuz.domain.location.LocationPermissionStatusUseCase
import io.github.amanshuraikwar.nxtbuz.domain.location.PushMapEventUseCase
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "LocationVMDelegate"

class LocationViewModelDelegate @Inject constructor(
    private val getLocationUpdatesUseCase: GetLocationUpdatesUseCase,
    private val getLastKnownLocationUseCase: GetLastKnownLocationUseCase,
    private val pushMapEventUseCase: PushMapEventUseCase,
    private val dispatcherProvider: CoroutinesDispatcherProvider,
) {
    private val errorHandler = CoroutineExceptionHandler { _, th ->
        Log.e(TAG, "errorHandler: $th", th)
        FirebaseCrashlytics.getInstance().recordException(th)
    }

    private var job: Job? = null

    @Synchronized
    fun init(coroutineScope: CoroutineScope) {
        job?.cancel()
        job = null
        job = coroutineScope.launch(errorHandler + dispatcherProvider.computation) {
            val lastKnownLocation = getLastKnownLocationUseCase()
            Log.i(TAG, "init: last known location = $lastKnownLocation")

            if (lastKnownLocation is LocationOutput.Success) {
                pushMapEventUseCase(
                    MapEvent.AddMarker(
                        MapMarker(
                            "center",
                            lastKnownLocation.lat,
                            lastKnownLocation.lng,
                            R.drawable.ic_marker_location_20,
                            "",
                            isFlat = true
                        )
                    )
                )

                pushMapEventUseCase(
                    MapEvent.MoveCenter(
                        lastKnownLocation.lat,
                        lastKnownLocation.lng,
                    )
                )
            } else {
                pushMapEventUseCase(
                    MapEvent.AddMarker(
                        MapMarker(
                            "center",
                            -1.0,
                            -1.0,
                            R.drawable.ic_marker_location_20,
                            "",
                            isFlat = true
                        )
                    )
                )
            }

            getLocationUpdatesUseCase().collect { location ->
                Log.i(TAG, "init: location update = $location")
                if (location is LocationOutput.Success) {
                    pushMapEventUseCase(
                        MapEvent.MoveMarker(
                            "center",
                            LatLng(location.lat, location.lng)
                        )
                    )
                }
            }
        }
    }
}