package io.github.amanshuraikwar.nxtbuz.train.details

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.amanshuraikwar.nxtbuz.common.compose.HeaderView
import io.github.amanshuraikwar.nxtbuz.common.compose.util.itemsIndexedSafe

@Composable
internal fun RouteView(
    modifier: Modifier = Modifier,
    listItems: List<ListItemData>,
    onTrainRouteNodeClick: (trainStopCode: String) -> Unit,
) {
    val lazyListState = remember {
        LazyListState(
            0,
            0
        )
    }

    LaunchedEffect(null) {
        lazyListState.scrollToItem(0)
    }

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(
            bottom = 256.dp,
        ),
        state = lazyListState,
    ) {
        itemsIndexedSafe(
            items = listItems,
            key = { _, item ->
                when (item) {
                    is ListItemData.Header -> item.id
                    is ListItemData.RouteNodeOrigin -> item.trainStopCode
                    is ListItemData.RouteNodeDestination -> item.trainStopCode
                    is ListItemData.RouteNodeMiddle -> item.trainStopCode
                }
            },
            errorKey = "train-route-node-error-key"
        ) { _, item ->
            when (item) {
                is ListItemData.Header -> {
                    HeaderView(
                        title = item.title
                    )
                }
                is ListItemData.RouteNodeOrigin -> {
                    RouteNodeOriginView(data = item)
                }
                is ListItemData.RouteNodeDestination -> {
                    RouteNodeDestinationView(data = item)
                }
                is ListItemData.RouteNodeMiddle -> {
                    RouteNodeMiddleView(data = item)
                }
            }
        }
    }
}