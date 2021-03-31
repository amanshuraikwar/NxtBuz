package io.github.amanshuraikwar.nxtbuz.ui

import androidx.compose.runtime.Composable
import dev.chrisbanes.accompanist.insets.ProvideWindowInsets
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.NxtBuzTheme

@Composable
fun NxtBuzApp(
    content: @Composable () -> Unit,
) {
    NxtBuzTheme {
        ProvideWindowInsets {
            content()
        }
    }
}