package io.github.amanshuraikwar.nxtbuz.busstop.arrivals.item

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import io.github.amanshuraikwar.nxtbuz.busstop.R

@Composable
fun BusDestination(
    destinationBusStopDescription: String,
) {
    Row {
        Icon(
            painter = painterResource(R.drawable.ic_destination_16),
            modifier = Modifier.size(16.dp),
            contentDescription = "Wheelchair Access",
            tint = MaterialTheme.colors.onSurface
        )

        Text(
            text = destinationBusStopDescription,
            style = MaterialTheme.typography.overline,
            color = MaterialTheme.colors.onSurface,
            modifier = Modifier.padding(start = 2.dp)
        )
    }
}