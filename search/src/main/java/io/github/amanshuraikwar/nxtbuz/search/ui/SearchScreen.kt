package io.github.amanshuraikwar.nxtbuz.search.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import dev.chrisbanes.accompanist.insets.ExperimentalAnimatedInsets
import io.github.amanshuraikwar.nxtbuz.common.model.BusStop
import io.github.amanshuraikwar.nxtbuz.search.ui.model.SearchState

@ExperimentalAnimatedInsets
@ExperimentalComposeUiApi
@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    searchState: SearchState,
    onBusStopSelected: (BusStop) -> Unit = {},
    onBackClick: () -> Unit = {},
) {
    Surface(
        modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        Column {
            SearchBar(
                modifier = Modifier
                    .fillMaxWidth(),
                onSearch = { query ->
                    searchState.search(query)
                },
                onBackClick = {
                    onBackClick()
                },
            )

            SearchResults(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(),
                screenState = searchState.screenState,
                contentPadding = searchState.searchBarPadding,
                onBusStopSelected = {
                    searchState.clear()
                    onBusStopSelected(it)
                }
            )
        }
    }
}