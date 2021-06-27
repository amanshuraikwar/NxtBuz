package io.github.amanshuraikwar.nxtbuz.busroute.ui.item

import androidx.compose.animation.*
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import io.github.amanshuraikwar.nxtbuz.busroute.R
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.h6Bold
import io.github.amanshuraikwar.nxtbuz.common.model.arrival.ArrivingBus
import io.github.amanshuraikwar.nxtbuz.common.model.arrival.BusLoad
import io.github.amanshuraikwar.nxtbuz.common.model.arrival.BusType

@ExperimentalAnimationApi
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

        AnimatedContent(
            targetState = arrivingBus.arrival,
            transitionSpec = {
                if (targetState > initialState) {
                    slideInVertically({ it }) + fadeIn() with
                            slideOutVertically({ -it }) + fadeOut()
                } else {
                    slideInVertically({ -it }) + fadeIn() with
                            slideOutVertically({ it }) + fadeOut()
                }.using(SizeTransform(clip = false))
            }
        ) { target ->
            Text(
                text = target.toArrivalString(),
                style = MaterialTheme.typography.h6Bold,
                color = contentColor,
                modifier = Modifier.animateContentSize()
            )
        }

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
    }
}

fun Int.toArrivalString(): String {
    return when {
        this > 0 -> String.format("%02d", this)
        else -> "Arr"
    }
}