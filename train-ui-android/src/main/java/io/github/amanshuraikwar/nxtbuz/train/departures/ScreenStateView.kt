package io.github.amanshuraikwar.nxtbuz.train.departures

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
                TrainStopHeaderView(
                    data = screenState.header
                )

                Divider()

                DeparturesView(
                    listItems = screenState.listItems,
                    onTrainClick = onTrainClick
                )
            }
        }
    }
}