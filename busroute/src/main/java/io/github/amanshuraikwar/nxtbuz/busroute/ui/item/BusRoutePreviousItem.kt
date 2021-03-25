package io.github.amanshuraikwar.nxtbuz.busroute.ui.item

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import io.github.amanshuraikwar.nxtbuz.busroute.model.BusRouteListItemData

@Composable
fun BusRoutePreviousItem(
    busStopDescription: String,
    position: BusRouteListItemData.BusRouteNode.Position =
        BusRouteListItemData.BusRouteNode.Position.MIDDLE,
    arrivalState: BusRouteListItemData.ArrivalState,
    onExpand: () -> Unit = {},
    onCollapse: () -> Unit = {}
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
        )

        BusArrivalItem(
            arrivalState = arrivalState,
            position = position,
            contentColor = MaterialTheme.colors.onSurface,
        )
    }
}