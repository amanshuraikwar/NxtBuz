package io.github.amanshuraikwar.nxtbuz.common.compose

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarOutline
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material.swipeable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.onStar
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.star
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Suppress("EXPERIMENTAL_IS_NOT_ENABLED")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SwipeableStarButtonView(
    modifier: Modifier = Modifier,
    starred: Boolean,
    onItemClick: () -> Unit,
    onStarToggle: (newStarState: Boolean) -> Unit,
    view: @Composable BoxScope.() -> Unit
) {
    val starButtonWidth = 72.dp

    val swipeableState = rememberSwipeableState(1)
    val starButtonWidthPx = with(LocalDensity.current) { starButtonWidth.toPx() }
    val anchors = mapOf(-starButtonWidthPx to 0, 0f to 1)

    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .swipeable(
                state = swipeableState,
                anchors = anchors,
                thresholds = { _, _ -> FractionalThreshold(0.3f) },
                orientation = Orientation.Horizontal
            ),
    ) {
        Box(
            Modifier
                .background(MaterialTheme.colors.star)
                .matchParentSize(),
        ) {
            CompositionLocalProvider(
                LocalIndication provides rememberRipple(color = MaterialTheme.colors.onStar)
            ) {
                Column(
                    Modifier
                        .align(Alignment.CenterEnd)
                        .clickable {
                            coroutineScope.launch {
                                onStarToggle(!starred)
                                swipeableState.animateTo(1)
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
        }

        Box(
            modifier = Modifier
                .offset {
                    IntOffset(swipeableState.offset.value.roundToInt(), 0)
                }
                .clickable(onClick = onItemClick)
                .background(MaterialTheme.colors.surface)
                .fillMaxWidth(),
        ) {
            view()
        }
    }
}