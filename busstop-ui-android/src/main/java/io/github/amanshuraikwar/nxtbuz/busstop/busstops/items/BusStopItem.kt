package io.github.amanshuraikwar.nxtbuz.busstop.busstops.items

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarOutline
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.amanshuraikwar.nxtbuz.busstop.R
import io.github.amanshuraikwar.nxtbuz.busstop.busstops.model.BusStopsItemData
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.body1Bold
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.onStar
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.star
import io.github.amanshuraikwar.nxtbuz.common.compose.util.PreviewSurface
import io.github.amanshuraikwar.nxtbuz.commonkmm.BusStop
import kotlinx.coroutines.launch
import java.util.*
import kotlin.math.roundToInt

@Suppress("EXPERIMENTAL_IS_NOT_ENABLED")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BusStopItem(
    modifier: Modifier = Modifier,
    data: BusStopsItemData.BusStop,
) {
    val starButtonWidth = 72.dp

    val swipeableState = rememberSwipeableState(1)
    val starButtonWidthPx = with(LocalDensity.current) { starButtonWidth.toPx() }
    val anchors = mapOf(-starButtonWidthPx to 0, 0f to 1)

    val coroutineScope = rememberCoroutineScope()

    var starred by remember {
        mutableStateOf(false)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .swipeable(
                state = swipeableState,
                anchors = anchors,
                thresholds = { _, _ -> FractionalThreshold(0.3f) },
                orientation = Orientation.Horizontal
            ),
        contentAlignment = Alignment.CenterEnd
    ) {
        Box(
            Modifier
                .background(MaterialTheme.colors.star)
                .matchParentSize(),
        ) {
            Column(
                Modifier
                    .align(Alignment.CenterEnd)
                    .clickable {
                        coroutineScope.launch {
                            swipeableState.animateTo(1)
                            starred = !starred
                        }
                    }

                    .fillMaxHeight()
                    .width(starButtonWidth),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = if (starred) {
                        Icons.Rounded.Star
                    } else {
                        Icons.Rounded.StarOutline
                    },
                    contentDescription = if (starred) {
                        "Unstar"
                    } else {
                        "Star"
                    },
                    tint = MaterialTheme.colors.onStar,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                )

                Text(
                    text = if (starred) {
                        "Un-Star"
                    } else {
                        "Star"
                    },
                    modifier = Modifier
                        .padding(top = 8.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.button,
                    color = MaterialTheme.colors.onStar
                )
            }
        }

        Box(
            modifier = Modifier
                .background(MaterialTheme.colors.surface)
                .offset {
                    IntOffset(swipeableState.offset.value.roundToInt(), 0)
                }
                .fillMaxWidth(),
        ) {
            Box(
                contentAlignment = Alignment.BottomEnd
            ) {
                Surface(
                    modifier = Modifier
                        .padding(
                            start = 16.dp,
                            top = 16.dp,
                            bottom = 16.dp,
                            end = 16.dp
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

                AnimatedVisibility(
                    modifier = Modifier
                        .padding(bottom = 12.dp, end = 12.dp)
                        .size(16.dp),
                    visible = starred,
                    enter = expandIn(expandFrom = Alignment.Center)
                            + fadeIn(),
                    exit = shrinkOut(shrinkTowards = Alignment.Center)
                            + fadeOut()
                ) {
                    Surface(
                        color = MaterialTheme.colors.star,
                        shape = CircleShape,
                        elevation = 1.dp
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.AutoAwesome,
                            modifier = Modifier
                                .size(16.dp)
                                .padding(start = 1.dp, end = 2.dp)
                                .padding(vertical = 2.dp),
                            contentDescription = "Starred",
                            tint = MaterialTheme.colors.onStar
                        )
                    }

                }
            }

            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 16.dp,
                        start = 72.dp,
                        end = 16.dp,
                        bottom = 16.dp
                    )
            ) {
                Text(
                    text = data.busStopDescription,
                    style = MaterialTheme.typography.subtitle1,
                    color = MaterialTheme.colors.onSurface
                )

                Text(
                    text = data.busStopInfo.uppercase(Locale.ROOT),
                    style = MaterialTheme.typography.overline,
                    color = MaterialTheme.colors.onSurface,
                    modifier = Modifier.padding(top = 2.dp)
                )

                Text(
                    text = data.operatingBuses,
                    style = MaterialTheme.typography.body1Bold,
                    color = MaterialTheme.colors.primary,
                    modifier = Modifier.padding(top = 8.dp),
                    lineHeight = 20.sp,
                )
            }
        }
    }
}

@Preview
@Composable
fun BusStopItemPreview() {
    PreviewSurface(darkTheme = true) {
        BusStopItem(
            data = BusStopsItemData.BusStop(
                id = "bus-stops-screen-12345",
                busStopDescription = "Opp Blk 19",
                busStopInfo = "Jln Jurong Kechil • 123456",
                operatingBuses = "961M 77  88 162",
                busStop = BusStop(
                    code = "",
                    roadName = "",
                    description = "",
                    latitude = 1.0,
                    longitude = 1.0,
                    operatingBusList = listOf()
                )
            )
        )
    }
}