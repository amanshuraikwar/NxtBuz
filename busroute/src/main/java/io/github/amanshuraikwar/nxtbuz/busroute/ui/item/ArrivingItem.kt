package io.github.amanshuraikwar.nxtbuz.busroute.ui.item

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.medium
import io.github.amanshuraikwar.nxtbuz.common.model.arrival.BusArrivals
import java.util.*

@Composable
fun ArrivingItem(
    modifier: Modifier = Modifier,
    busArrivals: BusArrivals.Arriving,
    lastUpdatedOn: String,
) {
    Column(
        modifier = modifier,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ArrivingBusItem(
                busArrivals.nextArrivingBus
            )

            busArrivals.followingArrivingBusList.forEach { arrivingBus ->
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