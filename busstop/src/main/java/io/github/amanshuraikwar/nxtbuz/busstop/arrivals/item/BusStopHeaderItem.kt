package io.github.amanshuraikwar.nxtbuz.busstop.arrivals.item

import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Directions
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.amanshuraikwar.nxtbuz.busstop.R
import io.github.amanshuraikwar.nxtbuz.busstop.arrivals.model.BusStopArrivalListItemData
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.disabled
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.h6Bold
import io.github.amanshuraikwar.nxtbuz.common.compose.util.PreviewSurface

@Composable
fun BusStopHeaderItem(
    modifier: Modifier = Modifier,
    busStopDescription: String,
    busStopRoadName: String,
    busStopCode: String,
) {
//    var alpha by remember {
//        mutableStateOf(0f)
//    }
//
//    LaunchedEffect(data.busStopCode) {
//        animate(
//            initialValue = 0f,
//            targetValue = 1f,
//            animationSpec = tween(300, delayMillis = 300)
//        ) { animatedValue, _ ->
//            alpha = animatedValue
//        }
//    }

    Box(
        modifier = modifier/*.alpha(alpha)*/,
        contentAlignment = Alignment.CenterEnd
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 16.dp, bottom = 16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Surface(
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
                Modifier.padding(start = 16.dp)
            ) {
                Text(
                    text = busStopDescription,
                    color = MaterialTheme.colors.onSurface,
                    style = MaterialTheme.typography.h6Bold,
                )

                Text(
                    text = "${busStopRoadName} • ${busStopCode}",
                    color = MaterialTheme.colors.onSurface,
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }

        Icon(
            imageVector = Icons.Rounded.Directions,
            contentDescription = "Directions",
            tint = MaterialTheme.colors.onSurface,
            modifier = Modifier
                .clip(shape = MaterialTheme.shapes.small)
                .clickable {

                }
                .padding(16.dp)
        )
    }
}

@Composable
fun BusStopHeaderItem(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.CenterEnd
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 16.dp, bottom = 16.dp),
            contentAlignment = Alignment.TopStart
        ) {
            Surface(
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
                    .padding(start = 64.dp, end = 16.dp)
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
                    text = "                   ",
                    color = MaterialTheme.colors.onSurface,
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .background(MaterialTheme.colors.onSurface.disabled),
                )
            }
        }
    }
}

@Preview
@Composable
fun BusStopHeaderItemPreview() {
    PreviewSurface {
//        BusStopHeaderItem(
//            data = BusStopArrivalListItemData.BusStopHeader()
//        )
    }
}

@Preview
@Composable
fun BusStopHeaderItemPreviewDark() {
    PreviewSurface(darkTheme = true) {
//        BusStopHeaderItem(
//            data = BusStopArrivalListItemData.BusStopHeader()
//        )
    }
}