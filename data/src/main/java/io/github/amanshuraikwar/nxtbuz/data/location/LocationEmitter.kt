package io.github.amanshuraikwar.nxtbuz.data.location

import android.annotation.SuppressLint
import android.os.Looper
import com.google.android.gms.location.*
import io.github.amanshuraikwar.nxtbuz.common.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.common.model.location.LocationOutput
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationEmitter @Inject constructor(
    private val fusedLocationProviderClient: FusedLocationProviderClient,
    private val locationRepository: LocationRepository,
    private val dispatcherProvider: CoroutinesDispatcherProvider,
) {
    // signifies if we have already attached our location callback
    private var started = false
    private val locationUpdateFlow = MutableSharedFlow<LocationOutput>(replay = 1)

    private val locationCallback: LocationCallback by lazy {
        object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult?.lastLocation
                    ?.let { location ->
                        locationUpdateFlow.tryEmit(
                            LocationOutput.Success(
                                lat = location.latitude,
                                lng = location.longitude
                            )
                        )
                    }
            }

            override fun onLocationAvailability(p0: LocationAvailability?) {
                super.onLocationAvailability(p0)
            }
        }
    }

    @SuppressLint("MissingPermission")
    suspend fun getLocation(): SharedFlow<LocationOutput> {
        return withContext(dispatcherProvider.location) {
            // make thread safe to avoid attaching multiple location callbacks
            synchronized(this@LocationEmitter) {
                if (!started) {
                    fusedLocationProviderClient.requestLocationUpdates(
                        locationRepository.getLocationRequest(),
                        locationCallback,
                        Looper.getMainLooper()
                    )
                    started = true
                }
            }
            locationUpdateFlow
        }
    }

    fun cleanup() {
        // make thread safe
        synchronized(this) {
            if (started) {
                fusedLocationProviderClient.removeLocationUpdates(locationCallback)
                started = false
            }
        }
    }
}