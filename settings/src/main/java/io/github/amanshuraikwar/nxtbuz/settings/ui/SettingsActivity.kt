package io.github.amanshuraikwar.nxtbuz.settings.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.google.accompanist.insets.ExperimentalAnimatedInsets
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import dagger.android.support.DaggerAppCompatActivity
import io.github.amanshuraikwar.nxtbuz.common.compose.NxtBuzApp
import io.github.amanshuraikwar.nxtbuz.commonkmm.NxtBuzTheme
import io.github.amanshuraikwar.nxtbuz.common.util.setupSystemBars
import io.github.amanshuraikwar.nxtbuz.common.util.viewModelProvider
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalAnimationApi
class SettingsActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @ExperimentalAnimatedInsets
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val vm = viewModelProvider<SettingsViewModel>(viewModelFactory)

        setupSystemBars(
            isDarkTheme = when (vm.theme.value) {
                NxtBuzTheme.DARK -> true
                NxtBuzTheme.LIGHT -> false
            }
        )

        setContent {
            val theme by vm.theme.collectAsState()
            LaunchedEffect(key1 = theme) {
                launch {
                    setupSystemBars(
                        isDarkTheme = when (theme) {
                            NxtBuzTheme.DARK -> true
                            NxtBuzTheme.LIGHT -> false
                        }
                    )
                }
            }

            NxtBuzApp(isDark = theme == NxtBuzTheme.DARK) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    SettingsScreen(vm = vm) {
                        finish()
                    }
                }
            }
        }
        OssLicensesMenuActivity.setActivityTitle(
            "Open Source Licenses"
        )
    }
}