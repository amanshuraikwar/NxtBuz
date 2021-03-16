package io.github.amanshuraikwar.nxtbuz.busroute.ui.item

import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import io.github.amanshuraikwar.nxtbuz.busroute.model.BusRouteListItemData
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.body1Bold
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.star
import java.util.*

@Composable
fun BusRouteHeaderItem(
    modifier: Modifier = Modifier,
    data: BusRouteListItemData.BusRouteHeader,
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
                .padding(top = 16.dp, start = 16.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            BusService(
                busServiceNumber = data.busServiceNumber,
            )

            Column(
                modifier = Modifier.padding(start = 16.dp),
            ) {
                Text(
                    text = data.destinationBusStopDescription,
                    style = MaterialTheme.typography.body1Bold,
                    color = MaterialTheme.colors.onSurface,
                )

                Spacer(modifier = Modifier.size(2.dp))

                Text(
                    text = "From ${data.originBusStopDescription}".toUpperCase(Locale.ROOT),
                    style = MaterialTheme.typography.overline,
                    color = MaterialTheme.colors.onSurface,
                )
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