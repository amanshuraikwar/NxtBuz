package io.github.amanshuraikwar.nxtbuz.ui.onboarding

import android.os.Bundle
import androidx.core.view.ViewCompat
import dagger.android.support.DaggerAppCompatActivity
import io.github.amanshuraikwar.nxtbuz.R
import io.github.amanshuraikwar.nxtbuz.util.makeStatusBarTransparent
import io.github.amanshuraikwar.nxtbuz.util.setMarginTop
import kotlinx.android.synthetic.main.activity_onboarding.*

class OnboardingActivity : DaggerAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)
        makeStatusBarTransparent()
        ViewCompat.setOnApplyWindowInsetsListener(contentContainer) { _, insets ->
            //searchBg.setMarginTop(insets.systemWindowInsetTop)
            screenTopGuideline.setMarginTop(insets.systemWindowInsetTop)
            insets.consumeSystemWindowInsets()
        }
    }
}