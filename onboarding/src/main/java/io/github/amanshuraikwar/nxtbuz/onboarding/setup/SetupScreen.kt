package io.github.amanshuraikwar.nxtbuz.onboarding.setup

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.amanshuraikwar.nxtbuz.common.compose.PrimaryButton
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.body1Bold

@Composable
fun SetupScreen(
    vm: SetupViewModel,
    onSetupComplete: () -> Unit,
) {
    val screenState by vm.screenState.collectAsState()

    DisposableEffect(key1 = null) {
        vm.initiateSetup()
        onDispose { }
    }

    WelcomeScreen(screenState.versionName) {
        SetupProgressView(
            progressState = screenState.setupProgressState,
            onSetupComplete = onSetupComplete,
            onRetryClick = {
                vm.initiateSetup()
            }
        )
    }
}

@Composable
fun SetupProgressView(
    progressState: SetupProgressState,
    onSetupComplete: () -> Unit,
    onRetryClick: () -> Unit,
) {
    when (progressState) {
        is SetupProgressState.Starting -> {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                text = "Setting up...",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.body1Bold,
                color = MaterialTheme.colors.onSurface
            )

            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(50)),
            )
        }
        is SetupProgressState.InProgress -> {
            val progress by animateIntAsState(
                targetValue = progressState.progress,
                animationSpec = tween(900)
            )

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                text = "Setting up...",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.body1Bold,
                color = MaterialTheme.colors.onSurface
            )

            if (progress == 0) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(50)),
                )
            } else {
                LinearProgressIndicator(
                    progress = progress.toFloat() / 100f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(50)),
                )
            }
        }
        is SetupProgressState.SetupComplete -> {
            PrimaryButton(
                modifier = Modifier
                    .fillMaxWidth(),
                text = "GET STARTED",
                onClick = onSetupComplete
            )
        }
        is SetupProgressState.Error -> {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                text = progressState.message,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.body1Bold,
                color = MaterialTheme.colors.onSurface
            )

            CompositionLocalProvider(
                LocalIndication provides rememberRipple(color = MaterialTheme.colors.error)
            ) {
                OutlinedButton(
                    modifier = Modifier
                        .fillMaxWidth(),
                    onClick = onRetryClick,
                ) {
                    Text(
                        "RETRY",
                        modifier = Modifier.padding(vertical = 4.dp),
                        color = MaterialTheme.colors.error
                    )
                }
            }
        }
    }
}