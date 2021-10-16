package io.github.amanshuraikwar.nxtbuz.busstop.arrivals.item

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccessibleForward
import androidx.compose.material.icons.rounded.NotAccessible
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import io.github.amanshuraikwar.nxtbuz.busstop.R
import io.github.amanshuraikwar.nxtbuz.common.compose.VerticalInOutAnimatedContent
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.h6Bold
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.medium
import io.github.amanshuraikwar.nxtbuz.commonkmm.arrival.BusLoad
import io.github.amanshuraikwar.nxtbuz.common.util.toArrivalString

@ExperimentalAnimationApi
@Composable
fun BusArrival(
    arrival: Int,
    busLoad: BusLoad,
    wheelchairAccess: Boolean,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        VerticalInOutAnimatedContent(
            targetValue = arrival
        ) {
            Text(
                text = arrival.toArrivalString(),
                style = MaterialTheme.typography.h6Bold,
                color = MaterialTheme.colors.onSurface,
                modifier = Modifier.animateContentSize()
            )
        }

        Spacer(modifier = Modifier.size(16.dp))

        Icon(
            painter = painterResource(
                when (busLoad) {
                    BusLoad.SEA -> R.drawable.ic_bus_load_1_16
                    BusLoad.SDA -> R.drawable.ic_bus_load_2_16
                    BusLoad.LSD -> R.drawable.ic_bus_load_3_16
                }
            ),
            modifier = Modifier.size(16.dp),
            contentDescription = "Bus Load",
            tint = MaterialTheme.colors.onSurface
        )

        Icon(
            imageVector = if (wheelchairAccess) {
                Icons.Rounded.AccessibleForward
            } else {
                Icons.Rounded.NotAccessible
            },
            modifier = Modifier
                .size(16.dp)
                .padding(1.dp),
            contentDescription = "Wheelchair Access",
            tint = MaterialTheme.colors.onSurface
        )
    }
}

@Composable
fun BusArrival(
    arrival: String,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = arrival,
            style = MaterialTheme.typography.h6Bold,
            color = MaterialTheme.colors.onSurface.medium,
            modifier = Modifier.animateContentSize()
        )
    }
}