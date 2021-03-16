package io.github.amanshuraikwar.nxtbuz.busroute.ui.item

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.amanshuraikwar.nxtbuz.busroute.model.BusRouteListItemData
import io.github.amanshuraikwar.nxtbuz.common.compose.util.PreviewSurface
import io.github.amanshuraikwar.nxtbuz.common.model.Arrivals

@Composable
fun BusRouteNextItem(
    busStopDescription: String,
    position: BusRouteListItemData.BusRouteNode.Position =
        BusRouteListItemData.BusRouteNode.Position.MIDDLE,
    arrivalState: BusRouteListItemData.ArrivalState,
    onExpand: () -> Unit = {},
    onCollapse: () -> Unit = {}
) {
    Column(
        Modifier
            .clickable {
                if (arrivalState is BusRouteListItemData.ArrivalState.Inactive) {
                    onExpand()
                } else {
                    onCollapse()
                }
            }
            .fillMaxWidth()
            .animateContentSize(),
    ) {
        BusRouteNode(
            busStopDescription = busStopDescription,
            circleColor = MaterialTheme.colors.primary,
            topBarColor = MaterialTheme.colors.primary,
            bottomBarColor = MaterialTheme.colors.primary,
            busStopDescriptionStyle = MaterialTheme.typography.body2,
            position = position,
        )

        Crossfade(targetState = arrivalState) { state ->
            when (state) {
                is BusRouteListItemData.ArrivalState.Active -> {
                    BusRouteNode(
                        drawBar = position != BusRouteListItemData.BusRouteNode.Position.DESTINATION,
                        barColor = MaterialTheme.colors.primary,
                    ) {
                        BusArrival(
                            arrivals = state.arrivals,
                            lastUpdatedOn = state.lastUpdatedOn
                        )
                    }
                }
                is BusRouteListItemData.ArrivalState.Fetching -> {
                    BusRouteNode(
                        drawBar = position != BusRouteListItemData.BusRouteNode.Position.DESTINATION,
                        barColor = MaterialTheme.colors.primary,
                    ) {
                        BusArrivalFetching()
                    }
                }
                BusRouteListItemData.ArrivalState.Inactive -> { }
            }
        }
    }
}


//@Composable
//@Preview
//fun BusRouteNextItemPreview() {
//    PreviewSurface {
//        BusRouteNextItem("Opp Blk 19")
//    }
//}
//
//@Composable
//@Preview
//fun BusRouteNextItemPreviewDark() {
//    PreviewSurface(darkTheme = true) {
//        BusRouteNextItem("Opp Blk 19")
//    }
//}