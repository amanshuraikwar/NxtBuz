package io.github.amanshuraikwar.nxtbuz.search.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.ExperimentalAnimatedInsets
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.h6Bold
import io.github.amanshuraikwar.nxtbuz.commonkmm.BusStop
import io.github.amanshuraikwar.nxtbuz.search.ui.model.SearchResult
import io.github.amanshuraikwar.nxtbuz.search.ui.model.SearchScreenState

@ExperimentalAnimatedInsets
@Composable
fun SearchResults(
    modifier: Modifier = Modifier,
    contentPadding: Dp = 0.dp,
    screenState: SearchScreenState,
    onBusStopSelected: (BusStop) -> Unit = {}
) {
    when (screenState) {
        is SearchScreenState.Failed -> {
            Text(
                modifier = modifier
                    .padding(32.dp)
                    .padding(top = contentPadding),
                text = screenState.errorMessage,
                style = MaterialTheme.typography.h6Bold,
                color = MaterialTheme.colors.onSurface,
            )
        }
        is SearchScreenState.Success -> {
            LazyColumn(
                modifier = modifier,
                contentPadding = rememberInsetsPaddingValues(LocalWindowInsets.current.ime)
            ) {
                items(
                    screenState.searchResultList,
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
        is SearchScreenState.Nothing -> {
            Text(
                text = "Your search results will show up here.",
                color = MaterialTheme.colors.onSurface,
                style = MaterialTheme.typography.h6Bold,
                modifier = Modifier
                    .clickable {

                    }
                    .fillMaxSize()
                    .padding(32.dp)
            )
        }
    }
}
