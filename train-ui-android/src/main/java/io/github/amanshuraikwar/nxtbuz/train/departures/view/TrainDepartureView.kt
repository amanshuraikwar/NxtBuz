package io.github.amanshuraikwar.nxtbuz.train.departures.view

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.directions
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.h6Bold
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.medium
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.onDirections
import io.github.amanshuraikwar.nxtbuz.commonkmm.train.TrainDepartureStatus
import io.github.amanshuraikwar.nxtbuz.train.departures.ListItemData
import java.util.Locale

// TODO-amanshuraikwar (13 Sep 2022 05:23:44 PM): show track with an icon

@Composable
internal fun TrainDepartureView(
    data: ListItemData.Departure,
    infiniteAnimatingAlpha: Float,
    onClick: (trainCode: String) -> Unit
) {
    TrainDepartureView(
        modifier = Modifier
            .clickable {
                onClick(data.id)
            }
            .fillMaxWidth()
            .padding(16.dp),
        viaStationsVisible = data.viaStations != null,
        destinationView = {
            Text(
                text = data.destinationTrainStopName,
                style = MaterialTheme.typography.subtitle1,
                color = if (data.departureStatus == TrainDepartureStatus.CANCELLED) {
                    MaterialTheme.colors.onSurface.medium
                } else {
                    MaterialTheme.colors.onSurface
                },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        infoView = {
            Text(
                modifier = Modifier.padding(top = 8.dp),
                text = "${data.trainCategoryName} • ${data.id}".uppercase(Locale.ROOT),
                style = MaterialTheme.typography.overline,
                color = MaterialTheme.colors.onSurface.medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        viaStationsView = {
            if (data.viaStations != null) {
                Text(
                    modifier = Modifier.padding(top = 4.dp),
                    text = data.viaStations,
                    style = MaterialTheme.typography.body2,
                    color = if (data.departureStatus == TrainDepartureStatus.CANCELLED) {
                        MaterialTheme.colors.onSurface.medium
                    } else {
                        MaterialTheme.colors.onSurface
                    },
                )
            }
        },
        departureTimeView = {
            DepartureTimeView(
                delayByMinutes = data.delayedByMinutes,
                departureTime = data.actualDeparture ?: data.plannedDeparture,
                isCancelled = data.departureStatus == TrainDepartureStatus.CANCELLED
            )
        },
        arrivalTimeView = {
            ArrivalTimeView(
                modifier = Modifier
                    .let {
                        if (data.departureStatus == TrainDepartureStatus.ON_STATION
                            || data.departureStatus == TrainDepartureStatus.CANCELLED
                        ) {
                            it.alpha(infiniteAnimatingAlpha)
                        } else {
                            it
                        }
                    }
                    .padding(top = 4.dp),
                delayByMinutes = data.delayedByMinutes,
                plannedArrivalTime = data.plannedArrival,
                trainDepartureStatus = data.departureStatus
            )
        },
        trackView = {
            Text(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .background(
                        color = if (data.departureStatus == TrainDepartureStatus.CANCELLED) {
                            MaterialTheme.colors.onSurface.medium
                        } else {
                            MaterialTheme.colors.primary
                        },
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 4.dp, vertical = 1.dp),
                text = data.track?.uppercase(Locale.ROOT) ?: "^-^",
                style = MaterialTheme.typography.body2.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = if (data.departureStatus == TrainDepartureStatus.CANCELLED) {
                    MaterialTheme.colors.surface.medium
                } else {
                    MaterialTheme.colors.onPrimary
                },
            )
        }
    )
}

@Composable
fun ArrivalTimeView(
    modifier: Modifier = Modifier,
    trainDepartureStatus: TrainDepartureStatus,
    plannedArrivalTime: String?,
    delayByMinutes: Int,
) {
    when (trainDepartureStatus) {
        TrainDepartureStatus.INCOMING -> {
            if (plannedArrivalTime != null) {
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
                            style = MaterialTheme.typography.body2.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colors.error,
                        )
                    }
                }
            } else {
                Row(modifier) {
                    if (delayByMinutes == 0) {
                        Text(
                            style = MaterialTheme.typography.body2,
                            text = "INCOMING",
                            color = MaterialTheme.colors.onSurface.medium,
                        )
                    } else {
                        Text(
                            text = "${delayByMinutes}m late",
                            style = MaterialTheme.typography.body2.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colors.error,
                        )
                    }
                }
            }
        }

        TrainDepartureStatus.UNKNOWN -> {
            if (plannedArrivalTime != null) {
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
                            style = MaterialTheme.typography.body2.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colors.error,
                        )
                    }
                }
            } else {
                Row(modifier) {
                    if (delayByMinutes == 0) {
                        Text(
                            style = MaterialTheme.typography.body2,
                            text = "--",
                            color = MaterialTheme.colors.onSurface.medium,
                        )
                    } else {
                        Text(
                            text = "${delayByMinutes}m late",
                            style = MaterialTheme.typography.body2.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colors.error,
                        )
                    }
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
        style = if (isCancelled) {
            MaterialTheme.typography.h6Bold.copy(
                textDecoration = TextDecoration.LineThrough
            )
        } else {
            MaterialTheme.typography.h6Bold
        },
        color = textColor,
    )
}

@Composable
private fun TrainDepartureView(
    modifier: Modifier = Modifier,
    viaStationsVisible: Boolean,
    destinationView: @Composable () -> Unit,
    infoView: @Composable () -> Unit,
    viaStationsView: @Composable () -> Unit,
    departureTimeView: @Composable () -> Unit,
    arrivalTimeView: @Composable () -> Unit,
    trackView: @Composable () -> Unit,
) {
    Layout(
        modifier = modifier,
        content = {
            destinationView()
            if (viaStationsVisible) {
                viaStationsView()
            }
            infoView()

            departureTimeView()
            arrivalTimeView()
            trackView()
        }
    ) { measurables, constraints ->
        var timingsWidth = 0
        var timingsHeight = 0
        val timingsPlaceables = mutableListOf<Placeable>()
        for (i in measurables.size - 3 until measurables.size) {
            val placeable = measurables[i].measure(constraints.copy(minWidth = 0))
            timingsHeight += placeable.height
            timingsWidth = timingsWidth.coerceAtLeast(placeable.width)
            timingsPlaceables.add(placeable)
        }

        var height = timingsHeight

        val infoPlaceables = mutableListOf<Placeable>()
        var infoPlaceablesHeight = 0
        for (i in 0..measurables.size - 4) {
            val placeable = measurables[i].measure(
                constraints.copy(
                    maxWidth = constraints.maxWidth
                            - timingsWidth
                            - 4.dp.roundToPx(),
                    minWidth = 0
                )
            )
            infoPlaceablesHeight += placeable.height
            infoPlaceables.add(placeable)
        }

        height = height.coerceAtLeast(infoPlaceablesHeight)

        layout(
            width = constraints.maxWidth,
            height = height,
        ) {
            var dy = 0
            for (placeable in infoPlaceables) {
                placeable.place(
                    IntOffset(
                        0,
                        dy
                    )
                )
                dy += placeable.height
            }

            dy = 0
            for (placeable in timingsPlaceables) {
                placeable.place(
                    IntOffset(
                        constraints.maxWidth - placeable.width,
                        dy
                    )
                )
                dy += placeable.height
            }
        }
    }
}