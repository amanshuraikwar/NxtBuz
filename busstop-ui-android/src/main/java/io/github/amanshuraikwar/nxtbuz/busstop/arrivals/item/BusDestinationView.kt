package io.github.amanshuraikwar.nxtbuz.busstop.arrivals.item

import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.amanshuraikwar.nxtbuz.common.compose.util.PreviewSurface
import java.util.Locale

@Composable
fun BusDestinationView(
    destinationBusStopDescription: String,
) {
    Text(
        text = destinationBusStopDescription.uppercase(Locale.ROOT),
        style = MaterialTheme.typography.overline,
        color = MaterialTheme.colors.onSurface,
        modifier = Modifier.padding(start = 2.dp),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}

@Preview
@Composable
fun BusDestinationView_Preview_Light() {
    PreviewSurface(darkTheme = false) {
        BusDestinationView("Buona Vista Ter")
    }
}

@Preview
@Composable
fun BusDestinationView_Preview_Dark() {
    PreviewSurface(darkTheme = true) {
        BusDestinationView("Buona Vista Ter")
    }
}