package io.github.amanshuraikwar.nxtbuz.busroute.ui.item

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowRight
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.amanshuraikwar.nxtbuz.busroute.R
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.h6Bold
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.medium
import io.github.amanshuraikwar.nxtbuz.common.compose.util.PreviewSurface
import io.github.amanshuraikwar.nxtbuz.common.model.Arrivals
import io.github.amanshuraikwar.nxtbuz.common.model.ArrivingBus
import io.github.amanshuraikwar.nxtbuz.common.model.BusLoad
import io.github.amanshuraikwar.nxtbuz.common.model.BusType
import java.util.*

@Composable
fun BusArrival(
    modifier: Modifier = Modifier,
    arrivals: Arrivals,
    lastUpdatedOn: String,
) {
    Crossfade(targetState = arrivals) { state ->
        when (state) {
            is Arrivals.NotOperating -> {
                Text(
                    modifier = modifier,
                    text = "Not Operating",
                    style = MaterialTheme.typography.h6Bold,
                    color = MaterialTheme.colors.onSurface.medium
                )
            }
            is Arrivals.DataNotAvailable -> {
                Text(
                    modifier = modifier,
                    text = "No Data",
                    style = MaterialTheme.typography.h6Bold,
                    color = MaterialTheme.colors.onSurface.medium
                )
            }
            is Arrivals.Arriving -> {
                Column(
                    modifier = modifier,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        ArrivingBusItem(
                            state.nextArrivingBus
                        )

                        state.followingArrivingBusList.forEach { arrivingBus ->
                            Icon(
                                imageVector = Icons.Rounded.ArrowRight,
                                contentDescription = "Arrow",
                                tint = MaterialTheme.colors.onSurface.medium,
                            )

                            ArrivingBusItem(
                                arrivingBus,
                                contentColor = MaterialTheme.colors.onSurface.medium,
                            )
                        }
                    }

                    Text(
                        modifier = Modifier.padding(top = 8.dp),
                        text = lastUpdatedOn.toUpperCase(Locale.ROOT),
                        color = MaterialTheme.colors.onSurface.medium,
                        style = MaterialTheme.typography.overline,
                    )
                }
            }
        }
    }
}

@Composable
fun BusArrivalFetching(
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier,
        text = "Fetching arrivals...",
        style = MaterialTheme.typography.h6Bold,
        color = MaterialTheme.colors.onSurface.medium
    )
}

@Composable
fun ArrivingBusItem(
    arrivingBus: ArrivingBus,
    contentColor: Color = MaterialTheme.colors.onSurface,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(
                when (arrivingBus.type) {
                    BusType.SD -> R.drawable.ic_bus_normal_16
                    BusType.DD -> R.drawable.ic_bus_dd_16
                    BusType.BD -> R.drawable.ic_bus_feeder_16
                }
            ),
            modifier = Modifier.size(16.dp),
            contentDescription = "Bus Type",
            tint = contentColor
        )

        Spacer(modifier = Modifier.size(8.dp))

        Text(
            text = arrivingBus.arrival,
            style = MaterialTheme.typography.h6Bold,
            color = contentColor,
            modifier = Modifier.animateContentSize()
        )

        Spacer(modifier = Modifier.size(8.dp))

        Icon(
            painter = painterResource(
                when (arrivingBus.load) {
                    BusLoad.SEA -> R.drawable.ic_bus_load_1_16
                    BusLoad.SDA -> R.drawable.ic_bus_load_2_16
                    BusLoad.LSD -> R.drawable.ic_bus_load_3_16
                }
            ),
            modifier = Modifier.size(16.dp),
            contentDescription = "Bus Load",
            tint = contentColor
        )

//        Icon(
//            painter = painterResource(
//                if (arrivingBus.feature == "WAB") {
//                    R.drawable.ic_accessible_16
//                } else {
//                    R.drawable.ic_not_accessible_16
//                }
//            ),
//            modifier = Modifier.size(18.dp),
//            contentDescription = "Wheelchair Access",
//            tint = MaterialTheme.colors.onSurface
//        )
    }
}

@Preview
@Composable
fun BusArrivalPreview() {
    PreviewSurface {
//        BusArrival(
//            arrival = "in 04 mins",
//            busLoad = BusLoad.values().random(),
//            wheelchairAccess = false
//        )
    }
}

@Preview
@Composable
fun BusArrivalPreviewDark() {
    PreviewSurface(darkTheme = true) {
//        BusArrival(
//            arrival = "Arriving Now",
//            busLoad = BusLoad.values().random(),
//            wheelchairAccess = true
//        )
    }
}