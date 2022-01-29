package io.github.amanshuraikwar.nxtbuz.busstop.arrivals.item

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.amanshuraikwar.nxtbuz.busstop.arrivals.model.BusStopArrivalListItemData
import io.github.amanshuraikwar.nxtbuz.common.compose.StarIndicatorView
import io.github.amanshuraikwar.nxtbuz.common.compose.SwipeableStarButtonView

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
                )
        ) {
            when (data) {
                is BusStopArrivalListItemData.BusStopArrival.Arriving -> {
                    Box(
                        contentAlignment = Alignment.BottomEnd
                    ) {
                        BusService(
                            modifier
                                .padding(
                                    bottom = 4.dp,
                                    end = 4.dp
                                ),
                            busServiceNumber = data.busServiceNumber,
                            busType = data.busType
                        )

                        StarIndicatorView(
                            isStarred = data.starred
                        )
                    }

                    Column(
                        modifier = Modifier.padding(top = 4.dp, start = 12.dp)
                    ) {
                        BusArrival(
                            arrival = data.arrival,
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
    }
}