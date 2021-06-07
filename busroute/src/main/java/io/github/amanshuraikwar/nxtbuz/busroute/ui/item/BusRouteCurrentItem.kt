package io.github.amanshuraikwar.nxtbuz.busroute.ui.item

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.amanshuraikwar.nxtbuz.busroute.ui.model.BusRouteListItemData

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
        CircleAvatarItem(
            busStopDescription = busStopDescription,
            position = position,
        )

        BusArrivalItem(
            arrivalState = arrivalState,
            position = position,
            contentColor = MaterialTheme.colors.primary,
        )
    }
}