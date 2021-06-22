package io.github.amanshuraikwar.nxtbuz.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import io.github.amanshuraikwar.nxtbuz.common.compose.Fab
import io.github.amanshuraikwar.nxtbuz.common.compose.FabDecorationType

@Composable
fun BackButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    decorationType: FabDecorationType,
) {
    Fab(
        modifier = Modifier
            .then(modifier),
        imageVector = Icons.Rounded.ArrowBack,
        contentDescription = "Back",
        onClick = onClick,
        decorationType = decorationType,
    )
}

@ExperimentalAnimationApi
@Composable
fun BackButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    visible: Boolean,
    decorationType: FabDecorationType,
) {
    val state = remember {
        MutableTransitionState(visible)
    }

    LaunchedEffect(key1 = visible) {
        state.targetState = visible
    }

    AnimatedVisibility(
        modifier = modifier,
        visibleState = state,
        enter = fadeIn(animationSpec = tween(300)) +
                slideIn(
                    initialOffset = { IntOffset(x = -it.width, y = 0) },
                    animationSpec = tween(300)
                ),
        exit = fadeOut(animationSpec = tween(300)) +
                slideOut(
                    targetOffset = { IntOffset(x = -it.width, y = 0) },
                    animationSpec = tween(300)
                )
    ) {
        BackButton(
            onClick = onClick,
            decorationType = decorationType
        )
    }
}