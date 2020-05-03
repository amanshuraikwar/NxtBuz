package io.github.amanshuraikwar.nxtbuz.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import dagger.android.support.DaggerAppCompatActivity
import io.github.amanshuraikwar.nxtbuz.R
import io.github.amanshuraikwar.nxtbuz.util.location.LocationUtil
import io.github.amanshuraikwar.nxtbuz.util.makeStatusBarTransparent
import io.github.amanshuraikwar.nxtbuz.util.permission.PermissionUtil
import io.github.amanshuraikwar.nxtbuz.util.viewModelProvider
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var permissionUtil: PermissionUtil

    @Inject
    lateinit var locationUtil: LocationUtil

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        makeStatusBarTransparent()
        viewModel = viewModelProvider(viewModelFactory)
    }

    override fun onBackPressed() {
        viewModel.onBackPressed()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        permissionUtil.onPermissionResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode > LocationUtil.REQUEST_CHECK_SETTINGS) {
            locationUtil.onResult(requestCode, resultCode, data)
        }
    }
}