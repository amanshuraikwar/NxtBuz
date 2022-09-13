package io.github.amanshuraikwar.nxtbuz.train.departures

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
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
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.directions
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.h6Bold
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.medium
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.onDirections
import io.github.amanshuraikwar.nxtbuz.commonkmm.train.TrainDepartureStatus
import java.util.Locale

// TODO-amanshuraikwar (13 Sep 2022 05:23:44 PM): show track with an icon

@Composable
internal fun TrainDepartureView(
    data: ListItemData.Departure
) {
    val infiniteTransition = rememberInfiniteTransition()
    val animatingAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, delayMillis = 600),
            repeatMode = RepeatMode.Reverse
        )
    )

    Row(
        Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = data.destinationTrainStopName,
                style = MaterialTheme.typography.h6,
                color = MaterialTheme.colors.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                modifier = Modifier.padding(top = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier
                        .let {
                            if (data.departureStatus == TrainDepartureStatus.ON_STATION) {
                                it.alpha(animatingAlpha)
                            } else {
                                it
                            }
                        }
                        .background(
                            color = MaterialTheme.colors.primary,
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 4.dp, vertical = 1.dp),
                    text = data.track.uppercase(Locale.ROOT),
                    style = MaterialTheme.typography.body2.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colors.onPrimary
                )

                Text(
                    modifier = Modifier.padding(start = 8.dp),
                    text = data.trainCategoryName.uppercase(Locale.ROOT),
                    style = MaterialTheme.typography.overline,
                    color = MaterialTheme.colors.onSurface.medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

        }

        Column(
            horizontalAlignment = Alignment.End
        ) {
            DepartureTimeView(
                delayByMinutes = data.delayedByMinutes,
                departureTime = data.actualDeparture,
                isCancelled = data.departureStatus == TrainDepartureStatus.CANCELLED
            )

            ArrivalTimeView(
                modifier = Modifier
                    .let {
                        if (data.departureStatus == TrainDepartureStatus.ON_STATION) {
                            it.alpha(animatingAlpha)
                        } else {
                            it
                        }
                    }
                    .padding(top = 4.dp),
                delayByMinutes = data.delayedByMinutes,
                plannedArrivalTime = data.plannedArrival,
                trainDepartureStatus = data.departureStatus
            )
        }
    }
}

@Composable
fun ArrivalTimeView(
    modifier: Modifier = Modifier,
    trainDepartureStatus: TrainDepartureStatus,
    plannedArrivalTime: String,
    delayByMinutes: Int,
) {
    when (trainDepartureStatus) {
        TrainDepartureStatus.INCOMING, TrainDepartureStatus.UNKNOWN -> {
            Row(modifier) {
                Text(
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
                        text = "  ${delayByMinutes}m late",
                        style = MaterialTheme.typography.body2.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colors.error,
                    )
                }
            }
        }
        TrainDepartureStatus.ON_STATION -> {
            Text(
                modifier = modifier
                    .background(
                        color = MaterialTheme.colors.directions,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 4.dp, vertical = 1.dp),
                text = "ON STATION",
                style = MaterialTheme.typography.body2.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colors.onDirections
            )
        }
        TrainDepartureStatus.CANCELLED -> {
            Text(
                modifier = modifier
                    .background(
                        color = MaterialTheme.colors.error,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 4.dp, vertical = 1.dp),
                text = "CANCELLED",
                style = MaterialTheme.typography.body2.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colors.onError
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
                MaterialTheme.colors.onSurface.medium
            }
            delayByMinutes == 0 -> {
                MaterialTheme.colors.onSurface
            }
            else -> {
                MaterialTheme.colors.error
            }

        }
    )

    Text(
        text = departureTime,
        style = MaterialTheme.typography.h6Bold,
        color = textColor,
    )
}