package io.github.amanshuraikwar.nxtbuz.train.details

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import io.github.amanshuraikwar.nxtbuz.common.compose.FetchingView

@Composable
internal fun ScreenStateView(
    screenState: ScreenState,
    padding: PaddingValues = PaddingValues(),
    backgroundColor: Color,
    onTrainClick: (trainCode: String) -> Unit,
) {
    when (screenState) {
        ScreenState.Fetching -> {
            Column {
                Divider()

                FetchingView()
            }
        }
        is ScreenState.Success -> {
            Column {
                TrainHeaderView(
                    data = screenState.header
                )

                RouteView(
                    listItems = screenState.listItems,
                    onTrainRouteNodeClick = {}
                )
            }
        }
    }
}