package io.github.amanshuraikwar.nxtbuz.common.compose

import androidx.compose.foundation.LocalIndication
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.google.accompanist.insets.ExperimentalAnimatedInsets
import com.google.accompanist.insets.ProvideWindowInsets
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.NxtBuzTheme

@ExperimentalAnimatedInsets
@Composable
fun NxtBuzApp(
    content: @Composable () -> Unit,
) {
    NxtBuzTheme {
        ProvideWindowInsets(windowInsetsAnimationsEnabled = false) {
            CompositionLocalProvider(
                LocalIndication provides rememberRipple(color = MaterialTheme.colors.primary)
            ) {
                content()
            }
        }
    }
}