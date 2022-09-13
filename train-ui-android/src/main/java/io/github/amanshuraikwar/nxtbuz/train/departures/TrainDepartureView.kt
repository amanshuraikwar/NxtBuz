package io.github.amanshuraikwar.nxtbuz.train.departures

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.body1Bold
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.disabled
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.h4Bold
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.h6Bold
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.medium
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.outline
import java.util.Locale

// TODO-amanshuraikwar (13 Sep 2022 05:23:44 PM): show track with an icon

@Composable
internal fun TrainDepartureView(
    data: ListItemData.Departure
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = data.destinationTrainStopName,
                style = MaterialTheme.typography.body1Bold,
                color = MaterialTheme.colors.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = data.trainCategoryName.uppercase(Locale.ROOT),
                style = MaterialTheme.typography.overline,
                color = MaterialTheme.colors.onSurface.medium,
                modifier = Modifier.padding(top = 4.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Text(
                modifier = Modifier.padding(top = 4.dp),
                text = "Track ${data.track}".uppercase(Locale.ROOT),
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.onSurface.medium
            )
        }

        Column(
            horizontalAlignment = Alignment.End
        ) {
            DepartureTimeView(
                delayByMinutes = data.delayedByMinutes,
                departureTime = data.actualDeparture,
                isCancelled = data.cancelled
            )

            ArrivalTimeView(
                modifier = Modifier.padding(top = 4.dp),
                delayByMinutes = data.delayedByMinutes,
                plannedArrivalTime = data.plannedArrival,
            )
        }
    }
}

@Composable
fun ArrivalTimeView(
    modifier: Modifier = Modifier,
    plannedArrivalTime: String,
    delayByMinutes: Int,
) {
    Row {
        Text(
            modifier = modifier,
            style = if (delayByMinutes == 0) {
                MaterialTheme.typography.body2
            } else {
                MaterialTheme.typography.body2.copy(
                    textDecoration = TextDecoration.LineThrough,
                )
            },
            text = plannedArrivalTime,
            color = MaterialTheme.colors.onSurface.medium,
        )

        if (delayByMinutes > 0) {
            Text(
                modifier = modifier,
                text = "  ${delayByMinutes}m late",
                style = MaterialTheme.typography.body2.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colors.error,
            )
        }
    }
}

@Composable
fun DepartureTimeView(
    delayByMinutes: Int,
    departureTime: String,
    isCancelled: Boolean,
) {
    val textColor by animateColorAsState(
        targetValue = when {
            isCancelled -> {
                MaterialTheme.colors.onSurface.disabled
            }
            delayByMinutes == 0 -> {
                MaterialTheme.colors.onSurface
            }
            else -> {
                MaterialTheme.colors.error
            }

        }
    )

    Surface(
//        border = BorderStroke(
//            1.dp,
//            MaterialTheme.colors.outline
//
//        ),
//        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colors.surface
    ) {
//        Box(
//            modifier = Modifier.padding(8.dp),
//            contentAlignment = Alignment.Center
//        ) {
//            Text(
//                text = "---------",
//                style = MaterialTheme.typography.h6Bold,
//                modifier = Modifier
//                    .alpha(0f)
//            )

            Text(
                text = departureTime,
                style = MaterialTheme.typography.h6Bold,
                color = textColor,
            )
//        }
    }
}