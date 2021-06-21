package io.github.amanshuraikwar.nxtbuz.common.util.location

import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.util.Log
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import io.github.amanshuraikwar.nxtbuz.common.model.SettingsState
import kotlin.coroutines.Continuation
import kotlin.coroutines.suspendCoroutine

class LocationUtil(
    private val activity: Activity
) {

    private val countContinuationMap: MutableMap<Int, Continuation<SettingsState>> =
        mutableMapOf()

    private var count = 1

    @Synchronized
    private fun generateId(): Int {
        return ++count
    }

    private fun getLocationSettingsRequest(): LocationSettingsRequest? {

        val locationRequest =
            LocationRequest.create()
                ?.apply {
                    priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                }
                ?: return null

        return LocationSettingsRequest.Builder().addLocationRequest(locationRequest).build()
    }

    suspend fun settingEnabled(): SettingsState = suspendCoroutine { cont ->

        val locationSettingsRequest = getLocationSettingsRequest() ?: run {
            Log.w(
                TAG,
                "settingEnabled: LocationRequest.create() returned null."
            )
            cont.resumeWith(
                Result.success(
                    SettingsState.UnResolvable(
                        "LocationRequest.create() returned null."
                    )
                )
            )
            return@suspendCoroutine
        }

        val client: SettingsClient = LocationServices.getSettingsClient(activity)
        val task: Task<LocationSettingsResponse> =
            client.checkLocationSettings(locationSettingsRequest)

        task.addOnSuccessListener {
            cont.resumeWith(Result.success(SettingsState.Enabled))
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                cont.resumeWith(Result.success(SettingsState.Resolvable))
            } else {
                Log.e(TAG, "settingEnabled: Unexpected error.", exception)
                cont.resumeWith(
                    Result.success(
                        SettingsState.UnResolvable(
                            exception.message ?: "Unexpected error."
                        )
                    )
                )
            }
        }
    }

    fun onResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val id = requestCode - REQUEST_CHECK_SETTINGS
        val cont = countContinuationMap[id] ?: run {
            Log.w(TAG, "onResult: No continuation found for $id")
            return
        }
        if (resultCode == Activity.RESULT_OK) {
            cont.resumeWith(Result.success(SettingsState.Enabled))
        } else {
            cont.resumeWith(Result.success(SettingsState.UserCancelled))
        }
    }

    suspend fun enableSettings(): SettingsState = suspendCoroutine { cont ->

        val locationSettingsRequest = getLocationSettingsRequest() ?: run {
            Log.w(
                TAG,
                "settingEnabled: LocationRequest.create() returned null."
            )
            cont.resumeWith(
                Result.success(
                    SettingsState.UnResolvable(
                        "LocationRequest.create() returned null."
                    )
                )
            )
            return@suspendCoroutine
        }

        val client: SettingsClient = LocationServices.getSettingsClient(activity)
        val task: Task<LocationSettingsResponse> =
            client.checkLocationSettings(locationSettingsRequest)

        task.addOnSuccessListener {
            cont.resumeWith(Result.success(SettingsState.Enabled))
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    val id = generateId()
                    exception.startResolutionForResult(
                        activity,
                        REQUEST_CHECK_SETTINGS + id
                    )
                    countContinuationMap[id] = cont
                } catch (sendEx: IntentSender.SendIntentException) {
                    cont.resumeWith(
                        Result.success(
                            SettingsState.UnResolvable(
                                sendEx.localizedMessage ?: ""
                            )
                        )
                    )
                }
            } else {
                Log.e(TAG, "settingEnabled: Unexpected error.", exception)
                cont.resumeWith(
                    Result.success(
                        SettingsState.UnResolvable(
                            exception.message ?: "Unexpected error."
                        )
                    )
                )
            }
        }
    }

    companion object {
        private const val TAG = "LocationUtil"
        const val REQUEST_CHECK_SETTINGS = 2001
    }
}