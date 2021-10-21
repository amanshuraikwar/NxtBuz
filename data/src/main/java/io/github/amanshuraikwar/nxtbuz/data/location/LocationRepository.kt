package io.github.amanshuraikwar.nxtbuz.data.location

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Tasks
import io.github.amanshuraikwar.nxtbuz.commonkmm.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.common.di.ApplicationContext
import io.github.amanshuraikwar.nxtbuz.common.model.location.PermissionStatus
import io.github.amanshuraikwar.nxtbuz.common.model.location.LocationOutput
import io.github.amanshuraikwar.nxtbuz.common.model.location.LocationSettingsState
import io.github.amanshuraikwar.nxtbuz.preferencestorage.PreferenceStorage
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.concurrent.ExecutionException
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "LocationRepository"

//@Singleton
//class LocationRepository @Inject constructor(
//    @ApplicationContext private val context: Context,
//    private val fusedLocationProviderClient: FusedLocationProviderClient,
//    private val preferenceStorage: PreferenceStorage,
//    private val dispatcherProvider: CoroutinesDispatcherProvider
//) {
//    @SuppressLint("MissingPermission")
//    @Suppress("BlockingMethodInNonBlockingContext")
//    suspend fun getLastKnownLocation(
//        activity: Activity
//    ): LocationOutput {
//        return withContext(dispatcherProvider.io) {
//            when (val permissionStatus = getLocationPermissionStatus()) {
//                PermissionStatus.GRANTED -> {
//                    when (val settingsState = getLocationSettingsState(activity)) {
//                        is LocationSettingsState.Error -> {
//                            Log.d(TAG, "getLastKnownLocation: $settingsState")
//                            LocationOutput.Error(reason = settingsState.reason)
//                        }
//                        is LocationSettingsState.ResolvableError -> {
//                            Log.d(TAG, "getLastKnownLocation: $settingsState")
//                            LocationOutput.SettingsNotEnabled(
//                                settingsState
//                            )
//                        }
//                        LocationSettingsState.SettingsEnabled -> {
//                            Log.d(TAG, "getLastKnownLocation: $settingsState")
//                            getLastKnownLocation()
//                        }
//                    }
//                }
//                PermissionStatus.DENIED,
//                PermissionStatus.DENIED_PERMANENTLY -> {
//                    Log.d(TAG, "getLastKnownLocation: $permissionStatus")
//                    LocationOutput.PermissionsNotGranted(
//                        permissionStatus = permissionStatus
//                    )
//                }
//            }
//        }
//    }
//
//    fun getLocationRequest(): LocationRequest? {
//        return LocationRequest.create()?.apply {
//            interval = 10000
//            fastestInterval = 5000
//            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
//        }
//    }
//
//    suspend fun getDefaultLocation(): Pair<Double, Double> = withContext(dispatcherProvider.io) {
//        preferenceStorage.defaultLocation
//    }
//
//    @Suppress("BlockingMethodInNonBlockingContext")
//    suspend fun getLocationSettingsState(activity: Activity): LocationSettingsState {
//        return withContext(dispatcherProvider.io) {
//            val locationRequest = getLocationRequest()
//                ?: return@withContext LocationSettingsState.Error(
//                    reason = "Location request is null."
//                )
//
//            val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
//            val client = LocationServices.getSettingsClient(activity)
//
//            try {
//                Tasks.await(client.checkLocationSettings(builder.build()))
//                LocationSettingsState.SettingsEnabled
//            } catch (e: ExecutionException) {
//                val cause = e.cause
//                if (cause is ResolvableApiException) {
//                    LocationSettingsState.ResolvableError(exception = cause)
//                } else {
//                    LocationSettingsState.Error(reason = e.message ?: "Something went wrong!")
//                }
//            } catch (e: Exception) {
//                LocationSettingsState.Error(reason = e.message ?: "Something went wrong!")
//            }
//        }
//    }
//
//    fun getLocationPermissionStatus(): PermissionStatus {
//        return if (ContextCompat.checkSelfPermission(
//                context, Manifest.permission.ACCESS_FINE_LOCATION
//            ) == PackageManager.PERMISSION_GRANTED
//        ) {
//            PermissionStatus.GRANTED
//        } else {
//            if (preferenceStorage.permissionDeniedPermanently) {
//                PermissionStatus.DENIED_PERMANENTLY
//            } else {
//                PermissionStatus.DENIED
//            }
//        }
//    }
//
//    @SuppressLint("MissingPermission")
//    private suspend fun getLastKnownLocation(): LocationOutput.Success {
//        return suspendCancellableCoroutine { cont ->
//            fusedLocationProviderClient.requestLocationUpdates(
//                getLocationRequest(),
//                object : LocationCallback() {
//                    override fun onLocationResult(p0: LocationResult?) {
//                        if (cont.isActive) {
//                            p0?.lastLocation
//                                ?.let { location ->
//                                    cont.resumeWith(
//                                        Result.success(
//                                            LocationOutput.Success(
//                                                lat = location.latitude,
//                                                lng = location.longitude
//                                            )
//                                        )
//                                    )
//                                    fusedLocationProviderClient.removeLocationUpdates(
//                                        this
//                                    )
//                                }
//                        }
//                    }
//                },
//                Looper.getMainLooper()
//            )
//        }
//    }
//}