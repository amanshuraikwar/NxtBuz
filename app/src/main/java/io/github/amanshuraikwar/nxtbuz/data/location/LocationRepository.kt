package io.github.amanshuraikwar.nxtbuz.data.location

import android.annotation.SuppressLint
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.tasks.Tasks
import io.github.amanshuraikwar.nxtbuz.data.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.data.prefs.PreferenceStorage
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationRepository @Inject constructor(
    private val fusedLocationProviderClient: FusedLocationProviderClient,
    private val preferenceStorage: PreferenceStorage,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) {

    @Suppress("BlockingMethodInNonBlockingContext")
    @SuppressLint("MissingPermission")
    suspend fun getLastKnownLocation(): Location? = withContext(dispatcherProvider.io) {
        Tasks.await(fusedLocationProviderClient.lastLocation)
    }

    suspend fun getDefaultLocation(): Pair<Double, Double> = withContext(dispatcherProvider.io) {
        preferenceStorage.defaultLocation
    }
}