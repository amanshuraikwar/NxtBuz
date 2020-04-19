package io.github.amanshuraikwar.nxtbuz.ui.onboarding

import android.os.Bundle
import androidx.core.view.ViewCompat
import dagger.android.support.DaggerAppCompatActivity
import io.github.amanshuraikwar.nxtbuz.R
import io.github.amanshuraikwar.nxtbuz.util.makeStatusBarTransparent
import io.github.amanshuraikwar.nxtbuz.util.permission.PermissionUtil
import io.github.amanshuraikwar.nxtbuz.util.setMarginTop
import kotlinx.android.synthetic.main.activity_onboarding.*
import javax.inject.Inject

class OnboardingActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var permissionUtil: PermissionUtil

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
}