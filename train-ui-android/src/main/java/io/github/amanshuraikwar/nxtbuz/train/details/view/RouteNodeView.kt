package io.github.amanshuraikwar.nxtbuz.train.details.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import io.github.amanshuraikwar.nxtbuz.common.compose.BarAvatarView
import io.github.amanshuraikwar.nxtbuz.common.compose.CircleAvatarItem
import io.github.amanshuraikwar.nxtbuz.common.compose.CircleAvatarPosition
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.medium
import io.github.amanshuraikwar.nxtbuz.commonkmm.train.TrainCrowdStatus
import io.github.amanshuraikwar.nxtbuz.commonkmm.train.TrainRouteNodeTiming
import io.github.amanshuraikwar.nxtbuz.train.R
import io.github.amanshuraikwar.nxtbuz.train.details.ListItemData

@Composable
internal fun RouteNodeOriginView(
    modifier: Modifier = Modifier,
    data: ListItemData.RouteNodeOrigin
) {
    Column(modifier) {
        CircleAvatarItem(
            position = CircleAvatarPosition.ORIGIN
        ) {
            RouteNodeNameView(
                name = data.trainStopName,
                crowdStatus = data.crowdStatus,
                timing = data.type.departureTiming
            )
        }

        BarAvatarView(
            barColor = MaterialTheme.colors.primary
        ) {
            RouteNodeTimingView(timing = data.type.departureTiming)
        }
    }
}

@Composable
internal fun TrackNameView(
    modifier: Modifier = Modifier,
    data: TrainRouteNodeTiming
) {
    when (data) {
        is TrainRouteNodeTiming.Available -> {
            val actualTrack = data.actualTrack
            if (actualTrack != null) {
                Text(
                    modifier = modifier
                        .background(
                            color = if (actualTrack == data.plannedTrack) {
                                MaterialTheme.colors.primary
                            } else {
                                MaterialTheme.colors.error
                            },
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 4.dp, vertical = 1.dp),
                    text = actualTrack.uppercase(java.util.Locale.ROOT),
                    style = MaterialTheme.typography.body2.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = if (actualTrack == data.plannedTrack) {
                        MaterialTheme.colors.onPrimary
                    } else {
                        MaterialTheme.colors.onError
                    },
                )
            }
        }
        TrainRouteNodeTiming.NoData -> {
            // nothing
        }
    }
}

@Composable
internal fun RouteNodeNameView(
    modifier: Modifier = Modifier,
    name: String,
    crowdStatus: TrainCrowdStatus,
    timing: TrainRouteNodeTiming
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = name,
            color = MaterialTheme.colors.onSurface,
            style = MaterialTheme.typography.subtitle1,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            TrackNameView(
                data = timing
            )

            Icon(
                modifier = Modifier
                    .padding(start = 4.dp)
                    .size(20.dp),
                painter = painterResource(
                    when (crowdStatus) {
                        TrainCrowdStatus.MEDIUM -> R.drawable.ic_bus_load_2_16
                        TrainCrowdStatus.LOW -> R.drawable.ic_bus_load_1_16
                        TrainCrowdStatus.UNKNOWN -> R.drawable.ic_bus_load_0_16
                        TrainCrowdStatus.HIGH -> R.drawable.ic_bus_load_3_16
                    }
                ),
                contentDescription = null,
                tint = MaterialTheme.colors.onSurface
            )
        }
    }
}

@Composable
internal fun RouteNodeMiddleView(
    modifier: Modifier = Modifier,
    data: ListItemData.RouteNodeMiddle
) {
    Column(modifier) {
        CircleAvatarItem(
            topBarColor = MaterialTheme.colors.primary,
            position = CircleAvatarPosition.MIDDLE
        ) {
            RouteNodeNameView(
                name = data.trainStopName,
                crowdStatus = data.crowdStatus,
                timing = data.type.departureTiming
            )
        }

        BarAvatarView(
            barColor = MaterialTheme.colors.primary
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                RouteNodeTimingView(
                    timing = data.type.arrivalTiming,
                    showOnlyCorrectTime = true
                )

                Icon(
                    modifier = Modifier
                        .padding(start = 2.dp)
                        .size(16.dp),
                    imageVector = Icons.Rounded.ArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colors.onSurface.medium
                )

                RouteNodeTimingView(
                    modifier = Modifier
                        .padding(start = 2.dp),
                    timing = data.type.departureTiming,
                )
            }
        }
    }
}

@Composable
internal fun RouteNodeDestinationView(
    modifier: Modifier = Modifier,
    data: ListItemData.RouteNodeDestination
) {
    Column(modifier) {
        CircleAvatarItem(
            topBarColor = MaterialTheme.colors.primary,
            position = CircleAvatarPosition.DESTINATION
        ) {
            RouteNodeNameView(
                name = data.trainStopName,
                crowdStatus = data.crowdStatus,
                timing = data.type.arrivalTiming
            )
        }

        BarAvatarView(
            drawBar = false,
            barColor = MaterialTheme.colors.primary
        ) {
            RouteNodeTimingView(timing = data.type.arrivalTiming)
        }
    }
}

@Composable
internal fun RouteNodeTimingView(
    modifier: Modifier = Modifier,
    timing: TrainRouteNodeTiming,
    showOnlyCorrectTime: Boolean = false
) {
    when (timing) {
        is TrainRouteNodeTiming.Available -> {
            if (timing.cancelled) {
                Text(
                    modifier = modifier,
                    text = "CANCELLED".toUpperCase(Locale.current),
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.error
                )
            } else {
                val actualTime = timing.actualTime
                if (actualTime != null) {
                    if (timing.delayedByMinutes == 0) {
                        Text(
                            modifier = modifier,
                            text = actualTime.toUpperCase(Locale.current),
                            style = MaterialTheme.typography.body2.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colors.onSurface,
                        )
                    } else {
                        if (showOnlyCorrectTime) {
                            Text(
                                modifier = modifier,
                                text = actualTime.toUpperCase(Locale.current),
                                style = MaterialTheme.typography.body2.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colors.error,
                            )
                        } else {
                            Row(modifier) {
                                Text(
                                    style = MaterialTheme.typography.body2.copy(
                                        textDecoration = TextDecoration.LineThrough,
                                    ),
                                    text = timing.plannedTime.toUpperCase(Locale.current),
                                    color = MaterialTheme.colors.onSurface.medium,
                                )

                                Text(
                                    text = "  ${actualTime.toUpperCase(Locale.current)}",
                                    style = MaterialTheme.typography.body2.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = MaterialTheme.colors.error,
                                )
                            }
                        }
                    }

                } else {
                    Text(
                        modifier = modifier,
                        text = timing.plannedTime.toUpperCase(Locale.current),
                        style = MaterialTheme.typography.body2.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colors.onSurface,
                    )
                }
            }
        }
        TrainRouteNodeTiming.NoData -> {
            Text(
                modifier = modifier,
                text = "NO DATA".toUpperCase(Locale.current),
                style = MaterialTheme.typography.body2,
                color = MaterialTheme.colors.onSurface.medium
            )
        }
    }
}