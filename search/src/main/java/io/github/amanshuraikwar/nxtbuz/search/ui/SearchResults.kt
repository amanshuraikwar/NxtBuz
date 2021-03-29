package io.github.amanshuraikwar.nxtbuz.search.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.h6Bold
import io.github.amanshuraikwar.nxtbuz.common.model.BusStop
import io.github.amanshuraikwar.nxtbuz.search.ui.model.SearchResult
import io.github.amanshuraikwar.nxtbuz.search.ui.model.SearchScreenState

@Composable
fun SearchResults(
    modifier: Modifier = Modifier,
    contentPadding: Dp = 0.dp,
    screenState: SearchScreenState?,
    onBusStopSelected: (BusStop) -> Unit = {}
) {
    Crossfade(
        modifier = modifier,
        targetState = screenState
    ) { state ->
        when (state) {
            is SearchScreenState.Failed -> {
                Text(
                    modifier = Modifier
                        .padding(32.dp)
                        .padding(top = contentPadding),
                    text = state.errorMessage,
                    style = MaterialTheme.typography.h6Bold,
                    color = MaterialTheme.colors.onSurface,
                )
            }
            is SearchScreenState.Success -> {
                LazyColumn(
                    contentPadding = PaddingValues(top = contentPadding)
                ) {
                    items(
                        state.searchResultList,
                        key = { item ->
                            when (item) {
                                is SearchResult.BusStopResult -> {
                                    item.busStop.code
                                }
                            }
                        }
                    ) { item ->
                        when (item) {
                            is SearchResult.BusStopResult -> {
                                BusStopItem(
                                    modifier = Modifier.clickable {
                                        onBusStopSelected(item.busStop)
                                    },
                                    data = item
                                )
                            }
                        }
                    }
                }
            }
            null -> {
            }
        }
    }
}
