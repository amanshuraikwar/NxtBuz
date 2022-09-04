package io.github.amanshuraikwar.nxtbuz.busroute.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.amanshuraikwar.nxtbuz.busroute.ui.model.CurrentBusStopArrivalsData
import io.github.amanshuraikwar.nxtbuz.common.compose.BusArrivalView
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.medium
import io.github.amanshuraikwar.nxtbuz.common.compose.util.PreviewSurface
import io.github.amanshuraikwar.nxtbuz.commonkmm.arrival.ArrivingBus
import io.github.amanshuraikwar.nxtbuz.commonkmm.arrival.ArrivingBusStop
import io.github.amanshuraikwar.nxtbuz.commonkmm.arrival.BusArrivals
import io.github.amanshuraikwar.nxtbuz.commonkmm.arrival.BusLoad
import io.github.amanshuraikwar.nxtbuz.commonkmm.arrival.BusType
import kotlinx.datetime.Clock

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CurrentBusStopArrivalsView(
    modifier: Modifier = Modifier,
    data: CurrentBusStopArrivalsData,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        val arrivalList = List(
            if (data.busArrivals is BusArrivals.Arriving) {
                data.busArrivals.followingArrivingBusList.size + 1
            } else {
                0
            }
        ) { index ->
            if (index == 0) {
                (data.busArrivals as BusArrivals.Arriving).nextArrivingBus
            } else {
                (data.busArrivals as BusArrivals.Arriving).followingArrivingBusList[index - 1]
            }
        }

        BusArrivalView(
            arrival =
            arrivalList.getOrNull(0)?.arrival ?: -1,
            busLoad = arrivalList.getOrNull(0)?.load,
            busType = arrivalList.getOrNull(0)?.type,
            wheelchairAccessible = arrivalList.getOrNull(0)?.wheelchairAccess
        )

        Icon(
            modifier = Modifier.padding(top = 8.dp),
            imageVector = Icons.Rounded.ArrowRight,
            contentDescription = "Next Bus Arrival",
            tint = MaterialTheme.colors.onSurface.medium
        )

        BusArrivalView(
            arrival = arrivalList.getOrNull(1)?.arrival ?: -1,
            busLoad = arrivalList.getOrNull(1)?.load,
            busType = arrivalList.getOrNull(1)?.type,
            wheelchairAccessible = arrivalList.getOrNull(1)?.wheelchairAccess
        )

        Icon(
            modifier = Modifier.padding(top = 8.dp),
            imageVector = Icons.Rounded.ArrowRight,
            contentDescription = "Next Bus Arrival",
            tint = MaterialTheme.colors.onSurface.medium
        )

        BusArrivalView(
            arrival = arrivalList.getOrNull(2)?.arrival ?: -1,
            busLoad = arrivalList.getOrNull(2)?.load,
            busType = arrivalList.getOrNull(2)?.type,
            wheelchairAccessible = arrivalList.getOrNull(2)?.wheelchairAccess
        )
    }
}

@Preview
@Composable
fun CurrentBusStopArrivalsView_Preview_Light() {
    PreviewSurface(darkTheme = false) {
        CurrentBusStopArrivalsView(
            modifier = Modifier.fillMaxWidth(),
            data = CurrentBusStopArrivalsData(
                busArrivals = BusArrivals.Arriving(
                    nextArrivingBus = ArrivingBus(
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
                    ),
                    followingArrivingBusList = emptyList()
                )
            ),
        )
    }
}