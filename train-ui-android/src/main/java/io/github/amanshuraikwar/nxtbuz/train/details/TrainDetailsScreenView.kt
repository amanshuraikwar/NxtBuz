package io.github.amanshuraikwar.nxtbuz.train.details

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
fun TrainDetailsScreenView(
    modifier: Modifier = Modifier,
    trainCode: String,
    vm: TrainDetailsViewModel,
    bottomSheetBgOffset: Dp,
    showBottomSheet: Boolean,
) {
    val bottomSheetState = rememberNxtBuzBottomSheetState(
        initialValue = BottomSheetValue.Collapsed
    )
    val screenState by vm.screenState.collectAsState()

    val backgroundColor = if (bottomSheetState.bottomSheetState.expandProgressFraction == 1f) {
        MaterialTheme.colors.surface
    } else {
        Color.Transparent
    }

    DisposableEffect(key1 = trainCode) {
        vm.init(trainCode = trainCode)
        onDispose { }
    }

    if (showBottomSheet) {
        NxtBuzBottomSheet(
            modifier = modifier,
            state = bottomSheetState,
            bottomSheetBgOffset = bottomSheetBgOffset
        ) { padding ->
            ScreenStateView(
                screenState = screenState,
                padding = padding,
                backgroundColor = backgroundColor,
                onTrainClick = vm::onTrainClick
            )
        }
    } else {
        Surface(
            modifier = modifier,
            elevation = 0.dp
        ) {
            ScreenStateView(
                screenState = screenState,
                backgroundColor = backgroundColor,
                onTrainClick = vm::onTrainClick
            )
        }
    }
}