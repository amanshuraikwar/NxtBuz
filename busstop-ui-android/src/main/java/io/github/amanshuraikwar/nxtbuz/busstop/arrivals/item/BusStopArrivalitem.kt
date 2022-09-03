package io.github.amanshuraikwar.nxtbuz.busstop.arrivals.item

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarOutline
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.amanshuraikwar.nxtbuz.busstop.arrivals.model.BusStopArrivalListItemData
import io.github.amanshuraikwar.nxtbuz.common.compose.swipe.SwipeAction
import io.github.amanshuraikwar.nxtbuz.common.compose.swipe.SwipeableActionsBox
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.onStar
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.star
import io.github.amanshuraikwar.nxtbuz.common.compose.util.PreviewSurface
import io.github.amanshuraikwar.nxtbuz.commonkmm.Bus
import io.github.amanshuraikwar.nxtbuz.commonkmm.BusStop
import io.github.amanshuraikwar.nxtbuz.commonkmm.arrival.ArrivingBus
import io.github.amanshuraikwar.nxtbuz.commonkmm.arrival.ArrivingBusStop
import io.github.amanshuraikwar.nxtbuz.commonkmm.arrival.BusLoad
import io.github.amanshuraikwar.nxtbuz.commonkmm.arrival.BusType
import kotlinx.datetime.Clock

@ExperimentalAnimationApi
@Composable
fun BusStopArrivalItem(
    modifier: Modifier = Modifier,
    data: BusStopArrivalListItemData.BusStopArrival,
    onStarToggle: (newToggleState: Boolean) -> Unit,
    onClick: () -> Unit
) {
    val star = SwipeAction(
        icon = {
            Icon(
                modifier = Modifier.padding(16.dp),
                imageVector = if (data.starred) {
                    Icons.Rounded.Star
                } else {
                    Icons.Rounded.StarOutline
                },
                tint = MaterialTheme.colors.onStar,
                contentDescription = "Star"
            )
        },
        background = MaterialTheme.colors.star,
        onSwipe = { onStarToggle(!data.starred) },
        isUndo = data.starred,
    )

    SwipeableActionsBox(
        modifier = modifier.clickable(onClick = onClick),
        endActions = listOf(star),
        backgroundUntilSwipeThreshold = MaterialTheme.colors.onStar
    ) {
        Row(
            modifier = Modifier
                .background(MaterialTheme.colors.surface)
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
                    ArrivingBusView(
                        data = data,
                        busServiceView = { busServiceNumber, isStarred ->
                            BusService(
                                busServiceNumber = busServiceNumber,
                                starred = isStarred
                            )
                        },
                        busDestinationView = { busDestination ->
                            BusDestinationView(
                                destinationBusStopDescription = busDestination
                            )
                        },
                        busArrivalView = { arrival, busLoad, busType ->
                            BusArrivalView(
                                arrival = arrival,
                                busLoad = busLoad,
                                busType = busType
                            )
                        }
                    )
                }
                is BusStopArrivalListItemData.BusStopArrival.NotArriving -> {
                    BusServiceDisabled(
                        busServiceNumber = data.busServiceNumber,
                        starred = data.starred
                    )

                    Column(
                        modifier = Modifier.padding(top = 4.dp, start = 16.dp)
                    ) {
                        BusArrivalView(
                            arrival = data.reason,
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
@Preview(name = "Destination bus stop description is too long")
fun BusStopArrivalItem_Preview_Light() {
    PreviewSurface(darkTheme = false) {
        BusStopArrivalItem(
            data = BusStopArrivalListItemData.BusStopArrival.Arriving(
                busServiceNumber = "961M",
                busStop = BusStop(
                    code = "123",
                    roadName = "road name",
                    description = "Description",
                    latitude = 0.0,
                    longitude = 0.1,
                    operatingBusList = listOf(
                        Bus("961M")
                    ),
                    isStarred = false,
                ),
                starred = true,
                destinationBusStopDescription = "Harbourfront Station / Vivocity",
                arrivingBusList = listOf(
                    ArrivingBus(
                        origin = ArrivingBusStop(
                            busStopCode = "",
                            roadName = "",
                            busStopDescription = ""
                        ),
                        destination = ArrivingBusStop(
                            busStopCode = "",
                            roadName = "",
                            busStopDescription = ""
                        ),
                        arrival = 1,
                        arrivalInstant = Clock.System.now(),
                        latitude = 0.0,
                        longitude = 0.0,
                        visitNumber = 1,
                        load = BusLoad.LSD,
                        wheelchairAccess = true,
                        type = BusType.BD
                    )
                )
            ),
            onStarToggle = {},
            onClick = {}
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
@Preview(name = "Destination bus stop description is too long")
fun BusStopArrivalItem_Preview_Dark() {
    PreviewSurface(darkTheme = true) {
        BusStopArrivalItem(
            data = BusStopArrivalListItemData.BusStopArrival.Arriving(
                busServiceNumber = "961M",
                busStop = BusStop(
                    code = "123",
                    roadName = "road name",
                    description = "Description",
                    latitude = 0.0,
                    longitude = 0.1,
                    operatingBusList = listOf(
                        Bus("961M")
                    ),
                    isStarred = false,
                ),
                starred = true,
                destinationBusStopDescription = "Harbourfront Station / Vivocity",
                arrivingBusList = listOf(
                    ArrivingBus(
                        origin = ArrivingBusStop(
                            busStopCode = "",
                            roadName = "",
                            busStopDescription = ""
                        ),
                        destination = ArrivingBusStop(
                            busStopCode = "",
                            roadName = "",
                            busStopDescription = ""
                        ),
                        arrival = 1,
                        arrivalInstant = Clock.System.now(),
                        latitude = 0.0,
                        longitude = 0.0,
                        visitNumber = 1,
                        load = BusLoad.LSD,
                        wheelchairAccess = true,
                        type = BusType.BD
                    )
                )
            ),
            onStarToggle = {},
            onClick = {}
        )
    }
}