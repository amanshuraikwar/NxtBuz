package io.github.amanshuraikwar.nxtbuz.locationdata

import android.annotation.SuppressLint
import android.os.Looper
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import io.github.amanshuraikwar.nxtbuz.common.model.location.LocationOutput
import io.github.amanshuraikwar.nxtbuz.common.model.location.PermissionStatus
import io.github.amanshuraikwar.nxtbuz.commonkmm.CoroutinesDispatcherProvider
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext

private const val TAG = "LocationEmitter"

class LocationEmitter constructor(
    private val fusedLocationProviderClient: FusedLocationProviderClient,
    private val locationRepository: LocationRepository,
    private val dispatcherProvider: CoroutinesDispatcherProvider,
) {
    // signifies if we have already attached our location callback
    private var started = false
    private val locationUpdateFlow = MutableSharedFlow<LocationOutput>(replay = 1)
    private val locationAvailabilityFlow = MutableStateFlow(false)

    private val _locationPermissionStatus = MutableStateFlow(
        locationRepository.getLocationPermissionStatus()
    )
    val locationPermissionStatus = _locationPermissionStatus.asStateFlow()

    private val locationCallback: LocationCallback by lazy {
        object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                Log.i(TAG, "onLocationResult: $locationResult")
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
                Log.i(TAG, "onLocationAvailability: $p0")
                p0?.isLocationAvailable?.let { available ->
                    if (!available) {
                        locationUpdateFlow.tryEmit(
                            LocationOutput.SettingsNotEnabled()
                        )
                    }
                    locationAvailabilityFlow.value = available
                }
            }
        }
    }

    suspend fun getLocation(): SharedFlow<LocationOutput> {
        return withContext(dispatcherProvider.location) {
            if (locationRepository.getLocationPermissionStatus() == PermissionStatus.GRANTED) {
                attachCallback()
            }
            locationUpdateFlow
        }
    }

    suspend fun getLocationAvailability(): StateFlow<Boolean> {
        return withContext(dispatcherProvider.location) {
            if (locationRepository.getLocationPermissionStatus() == PermissionStatus.GRANTED) {
                attachCallback()
            }
            locationAvailabilityFlow
        }
    }

    @SuppressLint("MissingPermission")
    private fun attachCallback() {
        // make thread safe to avoid attaching multiple location callbacks
        synchronized(this) {
            if (!started) {
                fusedLocationProviderClient.requestLocationUpdates(
                    locationRepository.getLocationRequest(),
                    locationCallback,
                    Looper.getMainLooper()
                )
                started = true
            }
        }
    }

    suspend fun refreshLocationPermissionStatus() {
        withContext(dispatcherProvider.location) {
            _locationPermissionStatus.value = locationRepository.getLocationPermissionStatus()
            if (locationRepository.getLocationPermissionStatus() == PermissionStatus.GRANTED) {
                attachCallback()
            }
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