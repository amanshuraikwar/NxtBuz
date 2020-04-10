package io.github.amanshuraikwar.nxtbuz.ui.launcher

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import io.github.amanshuraikwar.nxtbuz.ui.launcher.LaunchDestination.MAIN_ACTIVITY
import io.github.amanshuraikwar.nxtbuz.ui.launcher.LaunchDestination.ONBOARDING
import dagger.android.support.DaggerAppCompatActivity
import io.github.amanshuraikwar.nxtbuz.ui.onboarding.OnboardingActivity
import io.github.amanshuraikwar.nxtbuz.domain.result.EventObserver
import io.github.amanshuraikwar.nxtbuz.ui.main.MainActivity
import io.github.amanshuraikwar.nxtbuz.util.checkAllMatched
import io.github.amanshuraikwar.nxtbuz.util.makeStatusBarTransparent
import io.github.amanshuraikwar.nxtbuz.util.viewModelProvider
import javax.inject.Inject

/**
 * A 'Trampoline' activity for sending users to an appropriate screen on launch.
 */
class LauncherActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        makeStatusBarTransparent()
        val viewModel: LaunchViewModel = viewModelProvider(viewModelFactory)

        viewModel.launchDestination.observe(
            this,
            EventObserver { destination ->
                @Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
                when (destination) {
                    MAIN_ACTIVITY ->
                        startActivity(Intent(this, MainActivity::class.java))
                    ONBOARDING ->
                        startActivity(Intent(this, OnboardingActivity::class.java))
                }.checkAllMatched
                finish()
            }
        )
    }
}
