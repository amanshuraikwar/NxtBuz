package io.github.amanshuraikwar.nxtbuz.common.compose.layout

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.amanshuraikwar.nxtbuz.common.compose.NxtBuzBottomSheet
import io.github.amanshuraikwar.nxtbuz.common.compose.expandProgressFraction
import io.github.amanshuraikwar.nxtbuz.common.compose.rememberNxtBuzBottomSheetState

@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Composable
fun <T> ScreenLayoutView(
    modifier: Modifier = Modifier,
    input: T,
    bottomSheetBgOffset: Dp,
    showBottomSheet: Boolean,
    onBottomSheetInit: (Boolean) -> Unit,
    layout: @Composable (input: T, PaddingValues, backgroundColor: Color) -> Unit
) {
    val bottomSheetState = rememberNxtBuzBottomSheetState(
        initialValue = BottomSheetValue.Collapsed
    )

    val backgroundColor = if (bottomSheetState.bottomSheetState.expandProgressFraction == 1f) {
        MaterialTheme.colors.surface
    } else {
        Color.Transparent
    }

    LaunchedEffect(key1 = input) {
        onBottomSheetInit(bottomSheetState.isInitialised)
    }

    LaunchedEffect(key1 = bottomSheetState.isInitialised) {
        onBottomSheetInit(bottomSheetState.isInitialised)
    }

    LaunchedEffect(key1 = input) {
        if (!showBottomSheet) {
            onBottomSheetInit(true)
        }
    }

    if (showBottomSheet) {
        NxtBuzBottomSheet(
            modifier = modifier,
            state = bottomSheetState,
            bottomSheetBgOffset = bottomSheetBgOffset
        ) { padding ->
            layout(
                input,
                padding,
                backgroundColor
            )
        }
    } else {
        Surface(
            modifier = modifier,
            elevation = 0.dp
        ) {
            layout(
                input,
                PaddingValues(),
                backgroundColor
            )
        }
    }
}