package io.github.amanshuraikwar.nxtbuz.train.departures.view

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.amanshuraikwar.nxtbuz.common.compose.HeaderView
import io.github.amanshuraikwar.nxtbuz.common.compose.util.itemsIndexedSafe
import io.github.amanshuraikwar.nxtbuz.train.departures.ListItemData

@Composable
internal fun DeparturesView(
    modifier: Modifier = Modifier,
    listItems: List<ListItemData>,
    onTrainClick: (trainCode: String) -> Unit,
) {
    val infiniteTransition = rememberInfiniteTransition()
    val infiniteAnimatingAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, delayMillis = 600),
            repeatMode = RepeatMode.Reverse
        )
    )

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
                    is ListItemData.Departure -> item.id
                }
            },
            errorKey = "train-stop-arrivals-error-key"
        ) { _, item ->
            when (item) {
                is ListItemData.Departure -> {
                    TrainDepartureView(
                        data = item,
                        infiniteAnimatingAlpha = infiniteAnimatingAlpha,
                        onClick = onTrainClick
                    )
                }
                is ListItemData.Header -> {
                    HeaderView(
                        title = item.title
                    )
                }
            }
        }
    }
}