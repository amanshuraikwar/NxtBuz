package io.github.amanshuraikwar.nxtbuz.starred

import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp

@Composable
fun StarredBusArrivals(
    modifier: Modifier = Modifier,
    vm: StarredViewModel
) {

    LaunchedEffect(key1 = true) {
        vm.start()
    }

    val screenWidth = LocalConfiguration.current.screenWidthDp

    var offsetX by remember {
        mutableStateOf(screenWidth.dp)
    }

    var alpha by remember {
        mutableStateOf(0f)
    }

    LaunchedEffect(key1 = "") {
        animate(
            0f,
            1f,
            animationSpec = tween(600, delayMillis = 600)
        ) { animatedValue, _ ->
            offsetX = ((1 - animatedValue) * screenWidth).dp
            alpha = animatedValue
        }
    }

    LazyRow(
        modifier
            .offset(x = offsetX)
            .alpha(alpha),
        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp)
    ) {
        itemsIndexed(
            items = vm.listItems,
            key = { _, item ->
                item.busStopCode + item.busServiceNumber
            }

        ) { index, item ->
            if (index != 0) {
                Spacer(modifier = Modifier.size(16.dp))
            }

            BusArrivalItem(
                item.busStopDescription,
                item.busServiceNumber,
                item.arrivals,
            )
        }
    }
}