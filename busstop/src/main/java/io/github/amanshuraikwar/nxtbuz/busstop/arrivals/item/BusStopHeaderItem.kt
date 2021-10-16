package io.github.amanshuraikwar.nxtbuz.busstop.arrivals.item

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Directions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import io.github.amanshuraikwar.nxtbuz.busstop.R
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.disabled
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.h6Bold
import java.util.*

@Composable
fun BusStopHeaderItem(
    modifier: Modifier = Modifier,
    busStopDescription: String,
    busStopRoadName: String,
    busStopCode: String,
    onGoToBusStopClicked: () -> Unit = {},
) {
    Box(
        modifier = modifier.padding(vertical = 16.dp),
    ) {
        Surface(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 16.dp),
            color = MaterialTheme.colors.primary,
            shape = MaterialTheme.shapes.small
        ) {
            Icon(
                painter = painterResource(
                    R.drawable.ic_bus_stop_24
                ),
                modifier = Modifier
                    .padding(8.dp)
                    .size(24.dp),
                contentDescription = "Bus Stop",
                tint = MaterialTheme.colors.onPrimary
            )
        }

        Column(
            Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .padding(
                    start = 72.dp,
                    end = 88.dp,
                )
        ) {
            Text(
                text = busStopDescription,
                color = MaterialTheme.colors.onSurface,
                style = MaterialTheme.typography.h6Bold,
                modifier = Modifier
                    .fillMaxWidth()
            )

            Text(
                text = "$busStopRoadName • $busStopCode".uppercase(Locale.ROOT),
                color = MaterialTheme.colors.onSurface,
                style = MaterialTheme.typography.overline,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp)
            )
        }

        Icon(
            imageVector = Icons.Rounded.Directions,
            contentDescription = "Directions",
            tint = MaterialTheme.colors.onSurface,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp)
                .clip(shape = MaterialTheme.shapes.small)
                .clickable {
                    onGoToBusStopClicked()
                }
                .padding(16.dp)
                .size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))
    }
}

@Composable
fun BusStopHeaderItem(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.padding(vertical = 16.dp),
    ) {
        Surface(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 16.dp),
            color = MaterialTheme.colors.primary,
            shape = MaterialTheme.shapes.small
        ) {
            Icon(
                painter = painterResource(
                    R.drawable.ic_bus_stop_24
                ),
                modifier = Modifier
                    .padding(8.dp)
                    .size(24.dp),
                contentDescription = "Bus Stop",
                tint = MaterialTheme.colors.onPrimary
            )
        }

        Column(
            Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .padding(
                    start = 72.dp,
                    end = 88.dp,
                )
        ) {
            Text(
                text = "",
                color = MaterialTheme.colors.onSurface,
                style = MaterialTheme.typography.h6Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colors.onSurface.disabled)
            )

            Text(
                text = "                   ".uppercase(Locale.ROOT),
                color = MaterialTheme.colors.onSurface,
                style = MaterialTheme.typography.overline,
                modifier = Modifier
                    .padding(top = 2.dp)
                    .background(MaterialTheme.colors.onSurface.disabled)
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp)
                .clip(shape = MaterialTheme.shapes.small)
                .background(MaterialTheme.colors.onSurface.disabled)
                .padding(16.dp)
                .size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))
    }
}