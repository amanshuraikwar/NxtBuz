package io.github.amanshuraikwar.nxtbuz.data.location

import android.annotation.SuppressLint
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import io.github.amanshuraikwar.nxtbuz.common.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.common.model.location.Location
import io.github.amanshuraikwar.nxtbuz.data.prefs.PreferenceStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationEmitter @Inject constructor(
    private val fusedLocationProviderClient: FusedLocationProviderClient,
    private val preferenceStorage: PreferenceStorage,
    private val dispatcherProvider: CoroutinesDispatcherProvider,
) {

    // signifies if we have already attached our location callback
    private var started = false

    private val locationStateFlow: MutableStateFlow<Location> by lazy {
        val defaultLocation = preferenceStorage.defaultLocation
        return@lazy MutableStateFlow(
            Location(defaultLocation.first, defaultLocation.second)
        )
    }

    private val locationCallback: LocationCallback by lazy {
        object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                val lastLocation = locationResult.lastLocation
                locationStateFlow.value = Location(
                    lastLocation.latitude,
                    lastLocation.longitude
                )
            }
        }
    }

    @SuppressLint("MissingPermission")
    suspend fun getLocation(): StateFlow<Location> = withContext(dispatcherProvider.location) {
        // make thread safe to avoid attaching multiple location callbacks
        synchronized(this@LocationEmitter) {
            if (!started) {
                //Looper.prepare()
                fusedLocationProviderClient.requestLocationUpdates(
                    LocationRequest.create(),
                    locationCallback,
                    Looper.getMainLooper()
                )
                started = true
            }
            return@withContext locationStateFlow.asStateFlow()
        }
    }

    suspend fun cleanup(): Unit = withContext(dispatcherProvider.location) {
        // make thread safe
        synchronized(this) {
            if (started) {
                fusedLocationProviderClient.removeLocationUpdates(locationCallback)
                started = false
            }
        }
    }
}