package io.github.amanshuraikwar.nxtbuz.train.details.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.amanshuraikwar.nxtbuz.common.compose.FetchingView
import io.github.amanshuraikwar.nxtbuz.common.compose.PrimaryButton
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.h6Bold
import io.github.amanshuraikwar.nxtbuz.train.details.ScreenState

@Composable
internal fun ScreenStateView(
    modifier: Modifier = Modifier,
    screenState: ScreenState,
    onTrainRouteNodeClick: (trainStopCode: String) -> Unit,
    onReportErrorClick: (Exception) -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
    ) {
        when (screenState) {
            ScreenState.Fetching -> {
                TrainHeaderView()
                Divider()
                FetchingView()
            }

            is ScreenState.Success -> {
                TrainHeaderView(
                    data = screenState.header
                )

                RouteView(
                    listItems = screenState.listItems,
                    onTrainRouteNodeClick = onTrainRouteNodeClick
                )
            }

            is ScreenState.Error -> {
                TrainHeaderView()
                Divider()
                ErrorView(screenState, onReportErrorClick)
            }
        }
    }
}

@Suppress("UnusedReceiverParameter")
@Composable
internal fun ColumnScope.ErrorView(
    error: ScreenState.Error,
    onReportClick: (Exception) -> Unit,
) {
    Text(
        modifier = Modifier
            .padding(top = 24.dp, end = 16.dp, start = 72.dp),
        text = error.message,
        style = MaterialTheme.typography.h6Bold,
        color = MaterialTheme.colors.error
    )

    if (error.ableToReport) {
        PrimaryButton(
            modifier = Modifier
                .padding(top = 24.dp, end = 16.dp, start = 72.dp),
            text = "Report Issue",
            onClick = {
                onReportClick(error.exception)
            }
        )
    }
}