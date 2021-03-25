package io.github.amanshuraikwar.nxtbuz.busstop.ui.items

import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.amanshuraikwar.nxtbuz.busstop.R
import io.github.amanshuraikwar.nxtbuz.busstop.ui.BusStopsItemData
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.body1Bold

@Composable
fun BusStopItem(
    modifier: Modifier = Modifier,
    data: BusStopsItemData.BusStop,
) {
    var alpha by remember {
        mutableStateOf(0f)
    }

    LaunchedEffect(data.busStopInfo) {
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
    ) {
        Surface(
            modifier = Modifier
                .padding(
                    start = 16.dp,
                    top = 16.dp,
                    bottom = 16.dp
                ),
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
                .fillMaxWidth()
                .padding(
                    top = 16.dp,
                    start = 72.dp,
                    end = 16.dp,
                    bottom = 16.dp
                )
        ) {
            Text(
                text = data.busStopDescription,
                style = MaterialTheme.typography.subtitle1,
                color = MaterialTheme.colors.onSurface
            )

            Text(
                text = data.busStopInfo.toUpperCase(),
                style = MaterialTheme.typography.overline,
                color = MaterialTheme.colors.onSurface,
                modifier = Modifier.padding(top = 2.dp)
            )

            Text(
                text = data.operatingBuses,
                style = MaterialTheme.typography.body1Bold,
                color = MaterialTheme.colors.primary,
                modifier = Modifier.padding(top = 8.dp),
                lineHeight = 20.sp,
            )
        }
    }
}