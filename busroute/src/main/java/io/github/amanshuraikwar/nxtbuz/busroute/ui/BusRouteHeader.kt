package io.github.amanshuraikwar.nxtbuz.busroute.ui

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import io.github.amanshuraikwar.nxtbuz.busroute.ui.item.BusService
import io.github.amanshuraikwar.nxtbuz.busroute.ui.model.BusRouteHeaderData
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.body1Bold
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.disabled
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.star
import java.util.*

@Composable
fun BusRouteHeader(
    modifier: Modifier = Modifier,
    data: BusRouteHeaderData,
    onStarToggle: (newToggleState: Boolean) -> Unit = {}
) {
    val starred by data.starred.collectAsState()

    Box(
        modifier = modifier,
        contentAlignment = Alignment.CenterEnd
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = 16.dp,
                    start = 16.dp,
                    bottom = 16.dp,
                    end = 88.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BusService(
                busServiceNumber = data.busServiceNumber,
            )

            Column(
                modifier = Modifier.padding(start = 16.dp),
            ) {
                Text(
                    text = data.destinationBusStopDescription,
                    style = MaterialTheme.typography.body1Bold,
                    color = MaterialTheme.colors.onSurface,
                )

                Spacer(modifier = Modifier.size(2.dp))

                Text(
                    text = "From ${data.originBusStopDescription}".toUpperCase(Locale.ROOT),
                    style = MaterialTheme.typography.overline,
                    color = MaterialTheme.colors.onSurface,
                )
            }
        }

        CompositionLocalProvider(
            LocalIndication provides rememberRipple(color = MaterialTheme.colors.star)
        ) {
            Icon(
                imageVector = if (starred) {
                    Icons.Rounded.Star
                } else {
                    Icons.Rounded.StarBorder
                },
                contentDescription = "Star",
                tint = MaterialTheme.colors.star,
                modifier = Modifier
                    .padding(end = 16.dp)
                    .clip(shape = MaterialTheme.shapes.small)
                    .clickable {
                        onStarToggle(!starred)
                    }
                    .padding(16.dp)
                    .size(24.dp)
            )
        }
    }
}

@Composable
fun BusRouteHeader(
    modifier: Modifier = Modifier,
    busServiceNumber: String,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.CenterEnd
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 16.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BusService(
                busServiceNumber = busServiceNumber,
            )

            Column(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp),
            ) {
                Text(
                    text = "",
                    style = MaterialTheme.typography.body1Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colors.onSurface.disabled)
                )

                Text(
                    text = "                   ",
                    style = MaterialTheme.typography.overline,
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .background(MaterialTheme.colors.onSurface.disabled),
                )
            }
        }
    }
}