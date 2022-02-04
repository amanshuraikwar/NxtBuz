package io.github.amanshuraikwar.nxtbuz.busstop.arrivals.item

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.amanshuraikwar.nxtbuz.common.compose.StarIndicatorView
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.medium

@Composable
fun BusService(
    modifier: Modifier = Modifier,
    busServiceNumber: String,
    starred: Boolean
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.BottomEnd
    ) {
        BusService(
            Modifier
                .padding(
                    bottom = 4.dp,
                    end = 4.dp
                ),
            busServiceNumber = busServiceNumber,
        )

        StarIndicatorView(
            isStarred = starred
        )
    }
}

@Composable
fun BusService(
    modifier: Modifier = Modifier,
    busServiceNumber: String,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(
                    shape = RoundedCornerShape(50),
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
fun BusServiceDisabled(
    modifier: Modifier = Modifier,
    busServiceNumber: String,
    starred: Boolean
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.BottomEnd
    ) {
        BusServiceDisabled(
            Modifier
                .padding(
                    bottom = 4.dp,
                    end = 4.dp
                ),
            busServiceNumber = busServiceNumber,
        )

        StarIndicatorView(
            isStarred = starred
        )
    }
}

@Composable
fun BusServiceDisabled(
    modifier: Modifier = Modifier,
    busServiceNumber: String,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
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