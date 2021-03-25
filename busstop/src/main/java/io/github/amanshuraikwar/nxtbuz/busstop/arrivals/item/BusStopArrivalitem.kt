package io.github.amanshuraikwar.nxtbuz.busstop.arrivals.item

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.amanshuraikwar.nxtbuz.busstop.arrivals.BusStopArrivalListItemData
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.star
import io.github.amanshuraikwar.nxtbuz.common.compose.util.PreviewSurface

@Composable
fun BusStopArrivalItem(
    modifier: Modifier = Modifier,
    data: BusStopArrivalListItemData.BusStopArrival
) {
    var alpha by remember {
        mutableStateOf(0f)
    }

    LaunchedEffect(data.busServiceNumber) {
        animate(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = tween(300, delayMillis = 300)
        ) { animatedValue, _ ->
            alpha = animatedValue
        }
    }

    Box(
        modifier = modifier.alpha(alpha),
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

        Icon(
            imageVector = Icons.Rounded.StarBorder,
            contentDescription = "Star",
            tint = MaterialTheme.colors.star,
            modifier = Modifier
                .clip(shape = CircleShape)
                .clickable {

                }
                .padding(16.dp)
        )
    }
}

@Preview
@Composable
fun BusStopArrivalItemPreview() {
    PreviewSurface {
//        BusStopArrivalItem(
//            data = BusStopArrivalListItemData.BusStopArrival()
//        )
    }
}

@Preview
@Composable
fun BusStopArrivalItemPreviewDark() {
    PreviewSurface(darkTheme = true) {
//        BusStopArrivalItem(
//            data = BusStopArrivalListItemData.BusStopArrival()
//        )
    }
}