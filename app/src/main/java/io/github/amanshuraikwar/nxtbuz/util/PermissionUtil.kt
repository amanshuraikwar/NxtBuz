package io.github.amanshuraikwar.nxtbuz.util

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import javax.inject.Inject

const val MY_PERMISSIONS_REQUEST_FINE_LOCATION = 1000

class PermissionUtil @Inject constructor(private val activity: AppCompatActivity) {

    fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }
}