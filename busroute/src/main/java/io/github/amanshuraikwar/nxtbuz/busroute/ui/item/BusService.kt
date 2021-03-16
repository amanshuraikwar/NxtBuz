package io.github.amanshuraikwar.nxtbuz.busroute.ui.item

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.amanshuraikwar.nxtbuz.busroute.R

@Composable
fun BusService(
    busServiceNumber: String,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(
                R.drawable.ic_bus_24
            ),
            modifier = Modifier.size(24.dp),
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