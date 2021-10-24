package io.github.amanshuraikwar.nxtbuz.common.compose

import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomSheetState
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@ExperimentalMaterialApi
@Composable
fun rememberNxtBuzBottomSheetState(
    initialValue: BottomSheetValue,
): NxtBuzBottomSheetState {
    return remember {
        NxtBuzBottomSheetState(initialValue = initialValue)
    }
}

@ExperimentalMaterialApi
class NxtBuzBottomSheetState constructor(
    initialValue: BottomSheetValue,
    val bottomSheetState: BottomSheetState = BottomSheetState(initialValue)
) {
    var isInitialised: Boolean by mutableStateOf(false)
        internal set
}

@ExperimentalMaterialApi
@Composable
fun NxtBuzBottomSheet(
    modifier: Modifier = Modifier,
    state: NxtBuzBottomSheetState = remember {
        NxtBuzBottomSheetState(BottomSheetValue.Collapsed)
    },
    bottomSheetBgOffset: Dp = 0.dp,
    bottomSheetContent: @Composable (PaddingValues) -> Unit,
) {
    var alpha by remember(state) {
        mutableStateOf(0f)
    }
    var offsetY by remember(state) {
        mutableStateOf(128.dp)
    }

    LaunchedEffect(state) {
        state.isInitialised = false
        if (state.bottomSheetState.isExpanded) {
            state.bottomSheetState.collapse()
        }
        animate(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = tween(300)
        ) { animatedValue, _ ->
            alpha = animatedValue
            offsetY = ((1 - animatedValue) * 128).dp
        }
        state.isInitialised = true
    }

    ComposeBottomSheet(
        modifier = modifier
            .alpha(alpha = alpha)
            .offset(y = offsetY),
        bottomSheetState = state.bottomSheetState,
        bgOffset = bottomSheetBgOffset,
        sheetContent = {
            Box {
                Puck(
                    Modifier
                        .padding(top = bottomSheetBgOffset)
                        .alpha(
                            1 - state.bottomSheetState.expandProgressFraction
                        )
                )

                bottomSheetContent(
                    PaddingValues(top = bottomSheetBgOffset + 12.dp)
                )
            }
        },
        sheetPeekHeight =
        (LocalConfiguration.current.screenHeightDp * 2 / 5).dp + bottomSheetBgOffset
    ) {}
}