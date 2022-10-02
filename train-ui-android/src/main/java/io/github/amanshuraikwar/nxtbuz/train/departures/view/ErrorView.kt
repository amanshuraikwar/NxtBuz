package io.github.amanshuraikwar.nxtbuz.train.departures.view

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.amanshuraikwar.nxtbuz.common.compose.PrimaryButton
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.h6Bold
import io.github.amanshuraikwar.nxtbuz.train.departures.ScreenState

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