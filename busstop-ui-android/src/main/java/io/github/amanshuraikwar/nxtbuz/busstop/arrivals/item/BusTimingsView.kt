package io.github.amanshuraikwar.nxtbuz.busstop.arrivals.item

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.amanshuraikwar.nxtbuz.busstop.arrivals.model.BusStopArrivalListItemData

@OptIn(ExperimentalAnimationApi::class)
@Composable
internal fun BusTimingsView(
    modifier: Modifier = Modifier,
    data: BusStopArrivalListItemData.BusStopArrival.Arriving
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Top
    ) {
        BusArrival(
            arrival = data.arrivingBusList.getOrNull(0)?.arrival ?: -1,
            busLoad = data.arrivingBusList.getOrNull(0)?.load,
            busType =
            data.arrivingBusList.getOrNull(0)?.type
        )

        Icon(
            modifier = Modifier.padding(top = 8.dp),
            imageVector = Icons.Rounded.ArrowRight,
            contentDescription = "Next Bus Arrival"
        )

        BusArrival(
            arrival = data.arrivingBusList.getOrNull(1)?.arrival ?: -1,
            busLoad = data.arrivingBusList.getOrNull(1)?.load,
            busType =
            data.arrivingBusList.getOrNull(1)?.type
        )

        Icon(
            modifier = Modifier.padding(top = 8.dp),
            imageVector = Icons.Rounded.ArrowRight,
            contentDescription = "Next Bus Arrival"
        )

        BusArrival(
            arrival = data.arrivingBusList.getOrNull(2)?.arrival ?: -1,
            busLoad = data.arrivingBusList.getOrNull(2)?.load,
            busType =
            data.arrivingBusList.getOrNull(2)?.type
        )
    }
}