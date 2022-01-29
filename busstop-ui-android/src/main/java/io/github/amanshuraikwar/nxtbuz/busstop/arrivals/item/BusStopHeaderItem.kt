package io.github.amanshuraikwar.nxtbuz.busstop.arrivals.item

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Directions
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarOutline
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import io.github.amanshuraikwar.nxtbuz.busstop.R
import io.github.amanshuraikwar.nxtbuz.common.compose.StarIndicatorView
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.disabled
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.h6Bold
import java.util.*

@Composable
fun BusStopHeaderItem(
    modifier: Modifier = Modifier,
    busStopDescription: String,
    busStopRoadName: String,
    busStopCode: String,
    starred: Boolean,
    onGoToBusStopClicked: () -> Unit = {},
    onStarToggle: (newStarState: Boolean) -> Unit,
) {
    Column(
        modifier = modifier,
    ) {
        Box(
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
        ) {
            Box(
                contentAlignment = Alignment.BottomEnd
            ) {
                Surface(
                    modifier = Modifier
                        .padding(
                            start = 16.dp,
                            bottom = 4.dp,
                            end = 4.dp
                        ),
                    color = MaterialTheme.colors.primary,
                    shape = MaterialTheme.shapes.small
                ) {
                    Icon(
                        painter = painterResource(
                            R.drawable.ic_bus_stop_24
                        ),
                        modifier = Modifier
                            .padding(8.dp)
                            .size(24.dp),
                        contentDescription = "Bus Stop",
                        tint = MaterialTheme.colors.onPrimary
                    )
                }

                StarIndicatorView(
                    isStarred = starred
                )
            }

            Column(
                Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth()
                    .padding(
                        start = 72.dp,
                        end = 16.dp,
                    )
            ) {
                Text(
                    text = busStopDescription,
                    color = MaterialTheme.colors.onSurface,
                    style = MaterialTheme.typography.h6Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                )

                Text(
                    text = "$busStopRoadName • $busStopCode".uppercase(Locale.ROOT),
                    color = MaterialTheme.colors.onSurface,
                    style = MaterialTheme.typography.overline,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 2.dp)
                )
            }
        }

        Row(
            modifier = Modifier
                .padding(start = 72.dp)
                .padding(end = 16.dp)
                .padding(bottom = 16.dp, top = 8.dp)
        ) {
            BusStopHeaderButton(
                imageVector = Icons.Rounded.Directions,
                text = "Directions",
                onClick = onGoToBusStopClicked
            )

            BusStopHeaderButton(
                modifier = Modifier.padding(start = 12.dp),
                imageVector = if (starred) {
                    Icons.Rounded.Star
                } else {
                    Icons.Rounded.StarOutline
                },
                text = if (starred) {
                    "Un-Star"
                } else {
                    "Star"
                },
                onClick = {
                    onStarToggle(!starred)
                }
            )
        }
    }
}

@Composable
fun BusStopHeaderButton(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier,
        border = BorderStroke(1.dp, MaterialTheme.colors.onSurface),
        shape = RoundedCornerShape(percent = 50),
    ) {
        Row(
            modifier = Modifier
                .clickable(onClick = onClick)
                .padding(horizontal = 4.dp)
                .clip(RoundedCornerShape(percent = 50)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = imageVector,
                contentDescription = text,
                tint = MaterialTheme.colors.onSurface,
                modifier = Modifier
                    .padding(4.dp)
                    .size(16.dp)
            )

            Text(
                modifier = Modifier
                    .padding(end = 4.dp, start = 2.dp)
                    .padding(bottom = 4.dp, top = 2.dp),
                text = text,
                style = MaterialTheme.typography.button,
                color = MaterialTheme.colors.onSurface
            )
        }
    }
}

@Composable
fun BusStopHeaderItem(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.padding(vertical = 16.dp),
    ) {
        Surface(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 16.dp),
            color = MaterialTheme.colors.primary,
            shape = MaterialTheme.shapes.small
        ) {
            Icon(
                painter = painterResource(
                    R.drawable.ic_bus_stop_24
                ),
                modifier = Modifier
                    .padding(8.dp)
                    .size(24.dp),
                contentDescription = "Bus Stop",
                tint = MaterialTheme.colors.onPrimary
            )
        }

        Column(
            Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .padding(
                    start = 72.dp,
                    end = 88.dp,
                )
        ) {
            Text(
                text = "",
                color = MaterialTheme.colors.onSurface,
                style = MaterialTheme.typography.h6Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colors.onSurface.disabled)
            )

            Text(
                text = "                   ".uppercase(Locale.ROOT),
                color = MaterialTheme.colors.onSurface,
                style = MaterialTheme.typography.overline,
                modifier = Modifier
                    .padding(top = 2.dp)
                    .background(MaterialTheme.colors.onSurface.disabled)
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp)
                .clip(shape = MaterialTheme.shapes.small)
                .background(MaterialTheme.colors.onSurface.disabled)
                .padding(16.dp)
                .size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))
    }
}