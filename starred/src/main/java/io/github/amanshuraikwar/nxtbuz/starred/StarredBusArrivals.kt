package io.github.amanshuraikwar.nxtbuz.starred

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.body1Bold
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.h6Bold
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.medium
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.star
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
        if (listItems.isEmpty()) {
            Row(
                modifier = Modifier
                    .horizontalScroll(
                        rememberScrollState(),
                        enabled = false
                    )
            ) {
                BusArrivalItem(
                    Modifier.padding(
                        top = 8.dp,
                        bottom = 8.dp,
                        start = 16.dp,
                    ),
                    decorationType
                )

                BusArrivalItem(
                    Modifier.padding(
                        top = 8.dp,
                        bottom = 8.dp,
                        start = 16.dp,
                    ),
                    decorationType
                )

                BusArrivalItem(
                    Modifier.padding(
                        top = 8.dp,
                        bottom = 8.dp,
                        start = 16.dp,
                    ),
                    decorationType
                )
            }
        } else {
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