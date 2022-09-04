package io.github.amanshuraikwar.nxtbuz.busroute.ui.item

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.amanshuraikwar.nxtbuz.busroute.ui.model.BusRouteListItemData

@ExperimentalAnimationApi
@Composable
fun BusRoutePreviousItem(
    busStopDescription: String,
    position: BusRouteListItemData.BusRouteNode.Position =
        BusRouteListItemData.BusRouteNode.Position.MIDDLE,
    arrivalState: BusRouteListItemData.ArrivalState,
    onExpand: () -> Unit = {},
    onCollapse: () -> Unit = {},
    onAvatarClick: () -> Unit = {}
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
        CircleAvatarItem(
            busStopDescription = busStopDescription,
            circleColor = MaterialTheme.colors.onSurface,
            bottomBarColor = MaterialTheme.colors.onSurface,
            busStopDescriptionStyle = MaterialTheme.typography.body2,
            position = position,
            onAvatarClick = onAvatarClick,
        )

        BusArrivalItem(
            arrivalState = arrivalState,
            position = position,
            contentColor = MaterialTheme.colors.onSurface,
        )
    }
}