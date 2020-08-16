package io.github.amanshuraikwar.nxtbuz.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.core.view.ViewCompat
import dagger.android.support.DaggerAppCompatActivity
import io.github.amanshuraikwar.nxtbuz.common.util.location.LocationUtil
import io.github.amanshuraikwar.nxtbuz.common.util.makeStatusBarTransparent
import io.github.amanshuraikwar.nxtbuz.common.util.permission.PermissionUtil
import io.github.amanshuraikwar.nxtbuz.common.util.setMarginTop
import kotlinx.android.synthetic.main.activity_onboarding.*
import javax.inject.Inject

class OnboardingActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var permissionUtil: PermissionUtil

    @Inject
    lateinit var locationUtil: LocationUtil

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)
        makeStatusBarTransparent()
        ViewCompat.setOnApplyWindowInsetsListener(contentContainer) { _, insets ->
            screenTopGuideline.setMarginTop(insets.systemWindowInsetTop)
            insets.consumeSystemWindowInsets()
        }
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