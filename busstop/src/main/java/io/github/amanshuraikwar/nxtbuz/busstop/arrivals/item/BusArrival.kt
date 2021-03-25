package io.github.amanshuraikwar.nxtbuz.busstop.arrivals.item

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.amanshuraikwar.nxtbuz.busstop.R
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.h6Bold
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.medium
import io.github.amanshuraikwar.nxtbuz.common.compose.util.PreviewSurface
import io.github.amanshuraikwar.nxtbuz.common.model.BusLoad

@Composable
fun BusArrival(
    arrival: String,
    busLoad: BusLoad,
    wheelchairAccess: Boolean,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = arrival,
            style = MaterialTheme.typography.h6Bold,
            color = MaterialTheme.colors.onSurface,
            modifier = Modifier.animateContentSize()
        )

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
            painter = painterResource(
                if (wheelchairAccess) {
                    R.drawable.ic_accessible_16
                } else {
                    R.drawable.ic_not_accessible_16
                }
            ),
            modifier = Modifier.size(18.dp),
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

@Preview
@Composable
fun BusArrivalPreview() {
    PreviewSurface {
        BusArrival(
            arrival = "in 04 mins",
            busLoad = BusLoad.values().random(),
            wheelchairAccess = false
        )
    }
}

@Preview
@Composable
fun BusArrivalPreviewDark() {
    PreviewSurface(darkTheme = true) {
        BusArrival(
            arrival = "Arriving Now",
            busLoad = BusLoad.values().random(),
            wheelchairAccess = true
        )
    }
}