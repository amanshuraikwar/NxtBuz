package io.github.amanshuraikwar.nxtbuz.settings.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.lifecycle.ViewModelProvider
import dagger.android.support.DaggerAppCompatActivity
import io.github.amanshuraikwar.nxtbuz.common.compose.NxtBuzApp
import io.github.amanshuraikwar.nxtbuz.common.util.makeStatusBarTransparent
import io.github.amanshuraikwar.nxtbuz.common.util.viewModelProvider
import javax.inject.Inject

@ExperimentalAnimationApi
class SettingsActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        makeStatusBarTransparent()
        setContent {
            NxtBuzApp {
                SettingsScreen(vm = viewModelProvider(viewModelFactory)) {
                    finish()
                }
            }
        }
    }
}