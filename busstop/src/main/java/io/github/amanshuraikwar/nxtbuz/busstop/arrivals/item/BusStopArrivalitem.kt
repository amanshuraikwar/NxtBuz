package io.github.amanshuraikwar.nxtbuz.busstop.arrivals.item

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import io.github.amanshuraikwar.nxtbuz.busstop.arrivals.model.BusStopArrivalListItemData
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.star
import io.github.amanshuraikwar.nxtbuz.common.util.toArrivalString

@Composable
fun BusStopArrivalItem(
    modifier: Modifier = Modifier,
    data: BusStopArrivalListItemData.BusStopArrival,
    onStarToggle: (newToggleState: Boolean) -> Unit = {}
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.CenterEnd
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 16.dp, bottom = 16.dp)
        ) {
            when (data) {
                is BusStopArrivalListItemData.BusStopArrival.Arriving -> {
                    BusService(
                        busServiceNumber = data.busServiceNumber,
                        busType = data.busType
                    )

                    Column(
                        modifier = Modifier.padding(top = 4.dp, start = 16.dp)
                    ) {
                        BusArrival(
                            arrival = data.arrival.toArrivalString(),
                            busLoad = data.busLoad,
                            wheelchairAccess = data.wheelchairAccess
                        )

                        Spacer(modifier = Modifier.size(2.dp))

                        BusDestination(
                            destinationBusStopDescription = data.destinationBusStopDescription
                        )
                    }

                }
                is BusStopArrivalListItemData.BusStopArrival.NotArriving -> {
                    BusService(
                        busServiceNumber = data.busServiceNumber,
                    )

                    Column(
                        modifier = Modifier.padding(top = 4.dp, start = 16.dp)
                    ) {
                        BusArrival(
                            arrival = data.reason,
                        )
                    }
                }
            }
        }

        CompositionLocalProvider(
            LocalIndication provides rememberRipple(color = MaterialTheme.colors.star)
        ) {
            Icon(
                imageVector = if (data.starred) {
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
                        onStarToggle(!data.starred)
                    }
                    .padding(16.dp)
            )
        }
    }
}