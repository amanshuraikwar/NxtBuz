package io.github.amanshuraikwar.nxtbuz.busroute.ui.item

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.amanshuraikwar.nxtbuz.busroute.model.BusRouteListItemData

@Composable
fun BusRouteCurrentItem(
    busStopDescription: String,
    position: BusRouteListItemData.BusRouteNode.Position =
        BusRouteListItemData.BusRouteNode.Position.MIDDLE,
    arrivalState: BusRouteListItemData.ArrivalState,
) {
    Column(
        Modifier
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