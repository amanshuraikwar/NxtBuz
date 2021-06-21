package io.github.amanshuraikwar.nxtbuz.common.util.permission

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ResolvableApiException
import io.github.amanshuraikwar.nxtbuz.common.model.PermissionStatus
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine

class PermissionUtil(
    private val activity: Activity,
) {

    private val countContinuationMap: MutableMap<Int, CancellableContinuation<PermissionStatus>> =
        mutableMapOf()

    private val checkSettingsContinuationMap: MutableMap<Int, CancellableContinuation<Boolean>> =
        mutableMapOf()

    private var count = 1

    @Synchronized
    private fun generatePermissionRequestId(): Int {
        return ++count
    }

    fun hasLocationPermission(): PermissionStatus {

        return if (ContextCompat.checkSelfPermission(
                activity, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            PermissionStatus.GRANTED
        } else {
            // we are not returning PermissionStatus.DENIED_PERMANENTLY for any case
            // because the logic fails for first time check permissions
            PermissionStatus.DENIED
        }
    }

    suspend fun askPermission(): PermissionStatus = suspendCancellableCoroutine { cont ->

        val id = generatePermissionRequestId()

        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION + id
        )

        countContinuationMap[id] = cont
    }

    fun onPermissionResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        val id = requestCode - PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
        val cont = countContinuationMap[id] ?: run {
            Log.w(TAG, "onPermissionResult: No continuation found for $id")
            return

        }

        if (!cont.isActive) {
            return
        }

        if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            cont.resumeWith(Result.success(PermissionStatus.GRANTED))
        } else {
            cont.resumeWith(
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        activity,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                ) {
                    Result.success(PermissionStatus.DENIED)
                } else {
                    Result.success(PermissionStatus.DENIED_PERMANENTLY)
                }
            )
        }
    }

    suspend fun askForSettingsChange(exception: ResolvableApiException): Boolean {
        val id = generatePermissionRequestId()
        return try {
            exception.startResolutionForResult(activity, REQUEST_CHECK_SETTINGS + id)
            suspendCancellableCoroutine { cont ->
                checkSettingsContinuationMap[id] = cont
            }
        } catch (e: IntentSender.SendIntentException) {
            return false
        }
    }

    fun onCheckSettingResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ): Boolean {
        if (requestCode > 2001) {
            val id = requestCode - REQUEST_CHECK_SETTINGS
            val cont = checkSettingsContinuationMap[id] ?: run {
                Log.w(TAG, "onCheckSettingResult: No continuation found for $id")
                return false
            }

            if (!cont.isActive) {
                return false
            }

            Log.d(TAG, "onCheckSettingResult: $resultCode $data")

            cont.resumeWith(Result.success(resultCode == Activity.RESULT_OK))

            return true
        } else {
            return false
        }
    }

    companion object {
        const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1001
        const val REQUEST_CHECK_SETTINGS = 2001
        private const val TAG = "PermissionUtil"
    }
}