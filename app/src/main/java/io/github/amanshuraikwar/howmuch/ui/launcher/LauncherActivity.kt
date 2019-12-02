package io.github.amanshuraikwar.howmuch.ui.launcher

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import io.github.amanshuraikwar.howmuch.ui.launcher.LaunchDestination.MAIN_ACTIVITY
import io.github.amanshuraikwar.howmuch.ui.launcher.LaunchDestination.ONBOARDING
import dagger.android.support.DaggerAppCompatActivity
import io.github.amanshuraikwar.howmuch.ui.onboarding.OnboardingActivity
import io.github.amanshuraikwar.howmuch.domain.result.EventObserver
import io.github.amanshuraikwar.howmuch.ui.main.MainActivity
import io.github.amanshuraikwar.howmuch.util.checkAllMatched
import io.github.amanshuraikwar.howmuch.util.viewModelProvider
import javax.inject.Inject

/**
 * A 'Trampoline' activity for sending users to an appropriate screen on launch.
 */
class LauncherActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
