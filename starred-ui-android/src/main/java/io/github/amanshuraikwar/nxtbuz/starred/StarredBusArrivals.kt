package io.github.amanshuraikwar.nxtbuz.starred

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.amanshuraikwar.nxtbuz.common.compose.util.itemsIndexedSafe

enum class DecorationType {
    OUTLINE,
    SHADOW
}

@ExperimentalMaterialApi
@Composable
fun StarredBusArrivals(
    modifier: Modifier = Modifier,
    vm: StarredViewModel,
    onItemClicked: (busStopCode: String, busServiceNumber: String) -> Unit = { _, _ -> },
    decorationType: DecorationType = DecorationType.SHADOW,
) {
    DisposableEffect(key1 = true) {
        vm.start()
        onDispose {
            vm.onDispose()
        }
    }

    val listItems by vm.listItemsFlow.collectAsState()

    Box(
        modifier.animateContentSize()
    ) {
        if (listItems.isNotEmpty()) {
            LazyRow(
                contentPadding = PaddingValues(
                    top = 8.dp,
                    bottom = 8.dp,
                    start = 16.dp,
                    end = 16.dp
                )
            ) {
                itemsIndexedSafe(
                    items = listItems,
                    key = { _, item ->
                        item.busStopCode + item.busServiceNumber
                    },
                    errorKey = ""
                ) { index, item ->
                    if (index != 0) {
                        Spacer(modifier = Modifier.size(16.dp))
                    }

                    BusArrivalItem(
                        item.busStopDescription,
                        item.busServiceNumber,
                        item.busArrivals,
                        onClick = {
                            onItemClicked(
                                item.busStopCode,
                                item.busServiceNumber
                            )
                        },
                        onUnStarClicked = {
                            vm.onUnStarClicked(
                                busServiceNumber = item.busServiceNumber,
                                busStopCode = item.busStopCode,
                            )
                        },
                        decorationType = decorationType,
                    )
                }
            }
        }
    }
}