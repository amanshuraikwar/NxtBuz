package io.github.amanshuraikwar.nxtbuz.train.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import io.github.amanshuraikwar.nxtbuz.common.compose.FetchingView

@Composable
internal fun ScreenStateView(
    screenState: ScreenState,
    padding: PaddingValues = PaddingValues(),
    backgroundColor: Color,
    onTrainRouteNodeClick: (trainStopCode: String) -> Unit,
) {
    when (screenState) {
        ScreenState.Fetching -> {
            Column(
                modifier = Modifier
                    .padding(top = padding.calculateTopPadding())
                    .background(color = backgroundColor)
            ) {
                Divider()

                FetchingView()
            }
        }
        is ScreenState.Success -> {
            Column(
                modifier = Modifier
                    .padding(top = padding.calculateTopPadding())
                    .background(color = backgroundColor)
            ) {
                TrainHeaderView(
                    data = screenState.header
                )

                RouteView(
                    listItems = screenState.listItems,
                    onTrainRouteNodeClick = onTrainRouteNodeClick
                )
            }
        }
    }
}