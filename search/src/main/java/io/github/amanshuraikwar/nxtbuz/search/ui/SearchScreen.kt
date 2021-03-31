package io.github.amanshuraikwar.nxtbuz.search.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import dev.chrisbanes.accompanist.insets.LocalWindowInsets
import dev.chrisbanes.accompanist.insets.statusBarsPadding
import io.github.amanshuraikwar.nxtbuz.common.model.BusStop
import io.github.amanshuraikwar.nxtbuz.search.ui.model.SearchScreenState
import io.github.amanshuraikwar.nxtbuz.search.ui.model.SearchState


@ExperimentalComposeUiApi
@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    searchState: SearchState,
    onBusStopSelected: (BusStop) -> Unit = {},
) {
    val density = LocalDensity.current
    val insets = LocalWindowInsets.current

    Box(
        modifier
    ) {
        if (searchState.screenState != SearchScreenState.Nothing) {
            Surface {
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

        SearchBar(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(16.dp)
                .onSizeChanged {
                    searchState.updateSearchBarPadding(
                        with(density) {
                            it.height.toDp() + insets.statusBars.top.toDp() + 32.dp
                        }
                    )
                },
            screenState = searchState.screenState,
            onSearch = { query ->
                searchState.search(query)
            },
            onBackClicked = {
                searchState.clear()
            }
        )
    }
}