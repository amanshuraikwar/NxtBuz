package io.github.amanshuraikwar.nxtbuz.busstop.arrivals.item

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Directions
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarOutline
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import io.github.amanshuraikwar.nxtbuz.busstop.R
import io.github.amanshuraikwar.nxtbuz.common.compose.StarIndicatorView
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.directions
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.disabled
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.h6Bold
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.medium
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.star
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
            modifier = Modifier.padding(top = 16.dp, bottom = 16.dp),
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

                Row(
                    modifier = Modifier
                        .padding(top = 12.dp)
                ) {
                    CompositionLocalProvider(
                        LocalIndication provides rememberRipple(color = MaterialTheme.colors.star)
                    ) {
                        BusStopHeaderButton(
                            imageVector = if (starred) {
                                Icons.Rounded.Star
                            } else {
                                Icons.Rounded.StarOutline
                            },
                            text = if (starred) {
                                "UN-STAR"
                            } else {
                                "STAR"
                            },
                            onClick = {
                                onStarToggle(!starred)
                            },
                            outlineColor = MaterialTheme.colors.star.disabled,
                            onUnSelectedColor = MaterialTheme.colors.star
                        )
                    }

                    CompositionLocalProvider(
                        LocalIndication provides rememberRipple(
                            color = MaterialTheme.colors.directions
                        )
                    ) {
                        BusStopHeaderButton(
                            modifier = Modifier.padding(start = 12.dp),
                            imageVector = Icons.Rounded.Directions,
                            text = "DIRECTIONS",
                            onClick = onGoToBusStopClicked,
                            outlineColor = MaterialTheme.colors.directions.disabled,
                            onUnSelectedColor = MaterialTheme.colors.directions
                        )
                    }
                }
            }
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