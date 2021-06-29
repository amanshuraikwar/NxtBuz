package io.github.amanshuraikwar.nxtbuz.launcher

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import dagger.android.support.DaggerAppCompatActivity
import io.github.amanshuraikwar.nxtbuz.common.model.EventObserver
import io.github.amanshuraikwar.nxtbuz.common.model.LaunchDestination
import io.github.amanshuraikwar.nxtbuz.common.model.NxtBuzTheme
import io.github.amanshuraikwar.nxtbuz.common.util.*
import javax.inject.Inject

/**
 * A 'Trampoline' activity for sending users to an appropriate screen on launch.
 */
class LauncherActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel: LauncherViewModel = viewModelProvider(viewModelFactory)

        viewModel.launchDestination.observe(
            this,
            EventObserver { destination ->
                @Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
                when (destination) {
                    LaunchDestination.MAIN_ACTIVITY ->
                        startMainActivity()
                }.checkAllMatched
                finish()
            }
        )
    }
}
