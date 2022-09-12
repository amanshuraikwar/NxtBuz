package io.github.amanshuraikwar.nxtbuz.busstop.busstops.items

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarOutline
import androidx.compose.material.icons.rounded.Train
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.amanshuraikwar.nxtbuz.busstop.busstops.model.BusStopsItemData
import io.github.amanshuraikwar.nxtbuz.common.compose.StarIndicatorView
import io.github.amanshuraikwar.nxtbuz.common.compose.swipe.SwipeAction
import io.github.amanshuraikwar.nxtbuz.common.compose.swipe.SwipeableActionsBox
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.onStar
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.star
import io.github.amanshuraikwar.nxtbuz.common.compose.util.PreviewSurface
import java.util.Locale

@Composable
fun TrainStopItemView(
    modifier: Modifier = Modifier,
    data: BusStopsItemData.TrainStop,
    onClick: () -> Unit,
    onStarToggle: (newStarState: Boolean) -> Unit
) {
    val star = SwipeAction(
        icon = {
            Icon(
                modifier = Modifier.padding(16.dp),
                imageVector = if (data.isStarred) {
                    Icons.Rounded.Star
                } else {
                    Icons.Rounded.StarOutline
                },
                tint = MaterialTheme.colors.onStar,
                contentDescription = "Star"
            )
        },
        background = MaterialTheme.colors.star,
        onSwipe = { onStarToggle(!data.isStarred) },
        isUndo = data.isStarred,
    )

    SwipeableActionsBox(
        modifier = modifier.clickable(onClick = onClick),
        endActions = listOf(star),
        backgroundUntilSwipeThreshold = MaterialTheme.colors.onStar
    ) {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colors.surface)
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
                        imageVector = Icons.Rounded.Train,
                        modifier = Modifier
                            .padding(8.dp)
                            .size(24.dp),
                        contentDescription = "Bus Stop",
                        tint = MaterialTheme.colors.onPrimary
                    )
                }

                StarIndicatorView(
                    Modifier
                        .padding(bottom = 12.dp, end = 12.dp),
                    isStarred = data.isStarred
                )
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
                    text = data.name,
                    style = MaterialTheme.typography.subtitle1,
                    color = MaterialTheme.colors.onSurface
                )

                Text(
                    text = data.code.uppercase(Locale.ROOT),
                    style = MaterialTheme.typography.overline,
                    color = MaterialTheme.colors.onSurface,
                    modifier = Modifier.padding(top = 2.dp)
                )

//            Text(
//                text = data.operatingBuses,
//                style = MaterialTheme.typography.body1Bold,
//                color = MaterialTheme.colors.primary,
//                modifier = Modifier.padding(top = 8.dp),
//                lineHeight = 20.sp,
//            )
            }
        }
    }
}

@Preview
@Composable
fun TrainStopItemPreview() {
    PreviewSurface(darkTheme = true) {
        TrainStopItemView(
            data = BusStopsItemData.TrainStop(
                id = "train-stops-screen-12345",
                code = "123456",
                name = "Opp Blk 19",
                hasDepartureTimes = true,
                hasTravelAssistance = true,
                isStarred = true
            ),
            onClick = {},
            onStarToggle = {}
        )
    }
}