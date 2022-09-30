package io.github.amanshuraikwar.nxtbuz.train.departures

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import io.github.amanshuraikwar.nxtbuz.common.compose.layout.ScreenLayoutView
import io.github.amanshuraikwar.nxtbuz.train.departures.view.ScreenStateView

@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Composable
fun TrainDeparturesScreenView(
    modifier: Modifier = Modifier,
    trainStopCode: String,
    vm: TrainDeparturesViewModel,
    bottomSheetBgOffset: Dp,
    showBottomSheet: Boolean,
    onTrainClick: (trainCode: String) -> Unit,
) {
    val screenState by vm.screenState.collectAsState()
    DisposableEffect(key1 = trainStopCode) {
        vm.init(trainStopCode = trainStopCode)
        onDispose { }
    }

    ScreenLayoutView(
        modifier = modifier,
        input = screenState,
        bottomSheetBgOffset = bottomSheetBgOffset,
        showBottomSheet = showBottomSheet,
        onBottomSheetInit = {},
    ) { state, padding, backgroundColor ->
        ScreenStateView(
            screenState = state,
            modifier = Modifier
                .padding(top = padding.calculateTopPadding())
                .background(color = backgroundColor),
            onTrainClick = { trainStopCode ->
                vm.goingToTrainStop(trainStopCode = trainStopCode)
                onTrainClick(trainStopCode)
            },
            onReportErrorClick = vm::onReportErrorClick
        )
    }
}