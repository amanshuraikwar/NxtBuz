package io.github.amanshuraikwar.nxtbuz.search.ui.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import io.github.amanshuraikwar.nxtbuz.search.ui.SearchViewModel

@Composable
fun rememberSearchState(vm: SearchViewModel): SearchState {
    val screenState by vm.screenState.collectAsState(initial = SearchScreenState.Nothing)

    return SearchState(
        screenState = screenState,
        onClear = { vm.clear() },
        onSearch = { vm.searchBusStops(query = it) }
    )
}

class SearchState internal constructor(
    val screenState: SearchScreenState,
    val onClear: () -> Unit,
    val onSearch: (query: String) -> Unit,
) {
    fun clear() {
        onClear()
    }

    internal fun search(query: String) {
        onSearch(query)
    }
}