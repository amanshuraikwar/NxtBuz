package io.github.amanshuraikwar.nxtbuz.busstop.arrivals.item

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.amanshuraikwar.nxtbuz.busstop.arrivals.model.BusStopArrivalListItemData
import io.github.amanshuraikwar.nxtbuz.common.compose.SwipeableStarButtonView
import io.github.amanshuraikwar.nxtbuz.commonkmm.arrival.BusLoad
import io.github.amanshuraikwar.nxtbuz.commonkmm.arrival.BusType

@ExperimentalAnimationApi
@Composable
fun BusStopArrivalItem(
    modifier: Modifier = Modifier,
    data: BusStopArrivalListItemData.BusStopArrival,
    onStarToggle: (newToggleState: Boolean) -> Unit,
    onClick: () -> Unit
) {
    SwipeableStarButtonView(
        modifier = modifier,
        starred = data.starred,
        onItemClick = onClick,
        onStarToggle = onStarToggle
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = 16.dp,
                    start = 16.dp,
                    bottom = 16.dp,
                    end = 16.dp
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            when (data) {
                is BusStopArrivalListItemData.BusStopArrival.Arriving -> {
                    Column {
                        BusService(
                            busServiceNumber = data.busServiceNumber,
                            //busType = BusType.BD,
                            starred = data.starred
                        )

                        Spacer(modifier = Modifier.size(4.dp))

                        BusDestination(
                            destinationBusStopDescription = data.destinationBusStopDescription
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.Top
                    ) {
                        BusArrival(
                            arrival = data.arrivingBusList.getOrNull(0)?.arrival ?: -1,
                            busLoad = data.arrivingBusList.getOrNull(0)?.load ?: BusLoad.SEA,
                            busType =
                            data.arrivingBusList.getOrNull(0)?.type ?: BusType.SD
                        )

                        Icon(
                            modifier = Modifier.padding(top = 8.dp),
                            imageVector = Icons.Rounded.ArrowRight,
                            contentDescription = "Next Bus Arrival"
                        )

                        BusArrival(
                            arrival = data.arrivingBusList.getOrNull(1)?.arrival ?: -1,
                            busLoad = data.arrivingBusList.getOrNull(1)?.load ?: BusLoad.SEA,
                            busType =
                            data.arrivingBusList.getOrNull(0)?.type ?: BusType.SD
                        )

                        Icon(
                            modifier = Modifier.padding(top = 8.dp),
                            imageVector = Icons.Rounded.ArrowRight,
                            contentDescription = "Next Bus Arrival"
                        )

                        BusArrival(
                            arrival = data.arrivingBusList.getOrNull(2)?.arrival ?: -1,
                            busLoad = data.arrivingBusList.getOrNull(2)?.load ?: BusLoad.SEA,
                            busType =
                            data.arrivingBusList.getOrNull(0)?.type ?: BusType.SD
                        )
                    }

                }
                is BusStopArrivalListItemData.BusStopArrival.NotArriving -> {
                    BusServiceDisabled(
                        busServiceNumber = data.busServiceNumber,
                        starred = data.starred
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
    }
}