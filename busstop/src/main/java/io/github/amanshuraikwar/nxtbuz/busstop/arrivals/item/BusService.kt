package io.github.amanshuraikwar.nxtbuz.busstop.arrivals.item

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.amanshuraikwar.nxtbuz.busstop.R
import io.github.amanshuraikwar.nxtbuz.busstop.theme.disabled
import io.github.amanshuraikwar.nxtbuz.busstop.theme.h6Bold
import io.github.amanshuraikwar.nxtbuz.busstop.theme.medium
import io.github.amanshuraikwar.nxtbuz.common.model.BusType

@Composable
fun BusService(
    busServiceNumber: String,
    busType: BusType,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(
                when (busType) {
                    BusType.SD -> R.drawable.ic_bus_normal_16
                    BusType.DD -> R.drawable.ic_bus_dd_16
                    BusType.BD -> R.drawable.ic_bus_feeder_16
                }
            ),
            modifier = Modifier.size(16.dp),
            contentDescription = "Bus Type",
            tint = MaterialTheme.colors.onSurface
        )

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colors.primary
                )
                .padding(vertical = 4.dp, horizontal = 8.dp)
        ) {
            Text(
                text = "961M",
                style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Normal),
                modifier = Modifier.alpha(0f)
            )

            Text(
                text = busServiceNumber,
                style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Normal),
                color = MaterialTheme.colors.onPrimary
            )
        }
    }
}

@Composable
fun BusService(
    busServiceNumber: String,
) {
    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.disabled) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_bus_normal_16),
                modifier = Modifier.size(16.dp),
                contentDescription = "Bus Type",
                tint = MaterialTheme.colors.onSurface.medium
            )

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .background(
                        shape = MaterialTheme.shapes.small,
                        color = MaterialTheme.colors.primary.medium
                    )
                    .padding(vertical = 4.dp, horizontal = 8.dp)
            ) {
                Text(
                    text = "961M",
                    style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Normal),
                    modifier = Modifier.alpha(0f)
                )

                Text(
                    text = busServiceNumber,
                    style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Normal),
                    color = MaterialTheme.colors.onPrimary.medium
                )
            }
        }
    }
}