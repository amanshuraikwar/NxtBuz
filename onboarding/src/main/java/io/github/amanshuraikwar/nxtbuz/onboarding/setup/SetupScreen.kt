package io.github.amanshuraikwar.nxtbuz.onboarding.setup

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.statusBarsPadding
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.disabled
import io.github.amanshuraikwar.nxtbuz.onboarding.R

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

    SetupScreen(
        screenState = screenState,
        onSetupComplete = onSetupComplete,
        onRetryClick = {
            vm.initiateSetup()
        }
    )
}

@Composable
fun SetupScreen(
    screenState: SetupScreenState,
    onSetupComplete: () -> Unit,
    onRetryClick: () -> Unit,
) {
    val surfaceColor by animateColorAsState(
        targetValue = if (screenState is SetupScreenState.Error) {
            MaterialTheme.colors.error.disabled
        } else {
            MaterialTheme.colors.surface
        }
    )

    Surface(
        Modifier
            .fillMaxSize(),
        color = surfaceColor
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            Column {
                Spacer(modifier = Modifier.height(128.dp))

                val iconSurfaceColor by animateColorAsState(
                    targetValue = if (screenState is SetupScreenState.Error) {
                        MaterialTheme.colors.error
                    } else {
                        MaterialTheme.colors.primary
                    }
                )

                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = iconSurfaceColor,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    elevation = 2.dp
                ) {
                    val tint by animateColorAsState(
                        targetValue = if (screenState is SetupScreenState.Error) {
                            MaterialTheme.colors.onError
                        } else {
                            MaterialTheme.colors.onPrimary
                        }
                    )

                    Icon(
                        painter = painterResource(
                            id = R.drawable.ic_setup_108
                        ),
                        contentDescription = "Setup Icon",
                        tint = tint,
                        modifier = Modifier
                            .padding(16.dp)
                            .size(48.dp)
                    )
                }

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 48.dp)
                        .padding(horizontal = 32.dp)
                        .animateContentSize(),
                    text = if (screenState is SetupScreenState.Error) {
                        screenState.message
                    } else {
                        stringResource(id = R.string.onboarding_title_setup)
                    },
                    color = MaterialTheme.colors.onSurface,
                    style = MaterialTheme.typography.h4,
                    textAlign = TextAlign.Center
                )
            }

            Box(
                Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(end = 32.dp, start = 32.dp, bottom = 48.dp)
            ) {
                when (screenState) {
                    SetupScreenState.Fetching -> {
                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(50)),
                        )
                    }
                    is SetupScreenState.InProgress -> {
                        val progress by animateIntAsState(
                            targetValue = screenState.progress,
                            animationSpec = tween(900)
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
                    SetupScreenState.SetupComplete -> {
                        onSetupComplete()
                    }
                    is SetupScreenState.Error -> {
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
        }
    }
}