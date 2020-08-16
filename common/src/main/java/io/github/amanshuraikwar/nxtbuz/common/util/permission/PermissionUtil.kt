package io.github.amanshuraikwar.nxtbuz.common.util.permission

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import io.github.amanshuraikwar.nxtbuz.common.model.PermissionStatus
import kotlin.coroutines.Continuation
import kotlin.coroutines.suspendCoroutine

class PermissionUtil(private val activity: AppCompatActivity) {

    private val countContinuationMap: MutableMap<Int, Continuation<PermissionStatus>> =
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

    suspend fun askPermission(): PermissionStatus = suspendCoroutine { cont ->

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

    companion object {
        const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1001
        private const val TAG = "PermissionUtil"
    }
}