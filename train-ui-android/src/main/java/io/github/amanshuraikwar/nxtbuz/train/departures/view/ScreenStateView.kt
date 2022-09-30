package io.github.amanshuraikwar.nxtbuz.train.departures.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.github.amanshuraikwar.nxtbuz.common.compose.FetchingView
import io.github.amanshuraikwar.nxtbuz.common.compose.util.PreviewSurface
import io.github.amanshuraikwar.nxtbuz.train.departures.ScreenState

@Composable
internal fun ScreenStateView(
    modifier: Modifier = Modifier,
    screenState: ScreenState,
    onTrainClick: (trainCode: String) -> Unit,
    onReportErrorClick: (Exception) -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
    ) {
        when (screenState) {
            ScreenState.Fetching -> {
                TrainStopHeaderView()
                Divider()
                FetchingView()
            }

            is ScreenState.Success -> {
                TrainStopHeaderView(
                    data = screenState.header
                )
                Divider()
                if (screenState.listItems.isEmpty()) {
                    FetchingView()
                } else {
                    DeparturesView(
                        listItems = screenState.listItems,
                        onTrainClick = onTrainClick
                    )
                }
            }

            is ScreenState.Error -> {
                TrainStopHeaderView()
                Divider()
                ErrorView(screenState, onReportErrorClick)
            }
        }
    }
}

@Composable
@Preview
internal fun ScreenStateView_Preview_Fetching() {
    PreviewSurface(darkTheme = true) {
        ScreenStateView(
            screenState = ScreenState.Fetching,
            onReportErrorClick = {},
            onTrainClick = {}
        )
    }
}

@Composable
@Preview
internal fun ScreenStateView_Preview_Fetching_Light() {
    PreviewSurface(darkTheme = false) {
        ScreenStateView(
            screenState = ScreenState.Fetching,
            onReportErrorClick = {},
            onTrainClick = {}
        )
    }
}

@Composable
@Preview
internal fun ScreenStateView_Preview_Error() {
    PreviewSurface(darkTheme = true) {
        ScreenStateView(
            screenState = ScreenState.Error(
                message = "This is an error message!",
                exception = Exception(),
                ableToReport = true
            ),
            onReportErrorClick = {},
            onTrainClick = {}
        )
    }
}