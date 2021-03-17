package io.github.amanshuraikwar.nxtbuz.busroute.ui.item

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import io.github.amanshuraikwar.nxtbuz.busroute.model.BusRouteListItemData

@Composable
fun BusRouteCurrentItem(
    busStopDescription: String,
    position: BusRouteListItemData.BusRouteNode.Position =
        BusRouteListItemData.BusRouteNode.Position.MIDDLE,
    arrivalState: BusRouteListItemData.ArrivalState,
) {
    var alpha by remember {
        mutableStateOf(0f)
    }

    LaunchedEffect(busStopDescription) {
        animate(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = tween(300, delayMillis = 300)
        ) { animatedValue, _ ->
            alpha = animatedValue
        }
    }

    Column(
        Modifier
            .alpha(alpha)
            .fillMaxWidth()
            .animateContentSize(),
    ) {
        BusRouteNode(
            busStopDescription = busStopDescription,
            position = position,
        )

        if (arrivalState !is BusRouteListItemData.ArrivalState.Inactive) {
            BusRouteNode(
                drawBar = position != BusRouteListItemData.BusRouteNode.Position.DESTINATION,
                barColor = MaterialTheme.colors.primary,
            ) {
                if (arrivalState is BusRouteListItemData.ArrivalState.Active) {
                    BusArrival(
                        arrivals = arrivalState.arrivals,
                        lastUpdatedOn = arrivalState.lastUpdatedOn
                    )
                } else if (arrivalState is BusRouteListItemData.ArrivalState.Fetching) {
                    BusArrivalFetching()
                }
            }
        }
    }
}

//@Composable
//@Preview
//fun BusRouteCurrentItemPreview() {
//    PreviewSurface {
//        BusRouteCurrentItem("Opp Blk 19", arrivals = Arrivals.NotOperating, lastUpdatedOn = "")
//    }
//}
//
//@Composable
//@Preview
//fun BusRouteCurrentItemPreviewDark() {
//    PreviewSurface(darkTheme = true) {
//        BusRouteCurrentItem("Opp Blk 19", arrivals = Arrivals.DataNotAvailable, lastUpdatedOn = "")
//    }
//}