package io.github.amanshuraikwar.nxtbuz.busroute.ui.item

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.amanshuraikwar.nxtbuz.busroute.model.BusRouteListItemData
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.disabled
import io.github.amanshuraikwar.nxtbuz.common.compose.util.PreviewSurface

@Composable
fun BusRoutePreviousItem(
    busStopDescription: String,
    position: BusRouteListItemData.BusRouteNode.Position =
        BusRouteListItemData.BusRouteNode.Position.MIDDLE,
) {
    var expanded by remember {
        mutableStateOf(false)
    }

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
                expanded = !expanded
            }
            .fillMaxWidth()
            .animateContentSize(),
    ) {
        BusRouteNode(
            busStopDescription = busStopDescription,
            circleColor = MaterialTheme.colors.onSurface,
            bottomBarColor = MaterialTheme.colors.onSurface,
            busStopDescriptionStyle = MaterialTheme.typography.body2,
            position = position,
        )
        if (expanded) {
            Text(text = "helohelohelo", Modifier.padding(top = 100.dp))
        }
    }
}



@Composable
@Preview
fun BusRoutePreviousItemPreview() {
    PreviewSurface {
        BusRoutePreviousItem("Opp Blk 19")
    }
}

@Composable
@Preview
fun BusRoutePreviousItemPreviewDark() {
    PreviewSurface(darkTheme = true) {
        BusRoutePreviousItem("Opp Blk 19")
    }
}