package io.github.amanshuraikwar.nxtbuz.search.ui.model

import androidx.compose.runtime.*
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.amanshuraikwar.nxtbuz.search.ui.SearchViewModel

@Composable
fun rememberSearchState(vm: SearchViewModel): SearchState {
    val screenState by vm.screenState.collectAsState(initial = SearchScreenState.Nothing)
    val searchBarPadding = remember {
        mutableStateOf(0.dp)
    }

    return SearchState(
        screenState = screenState,
        searchBarPadding,
        onClear = { vm.clear() },
        onSearch = { vm.searchBusStops(query = it) }
    )
}

class SearchState internal constructor(
    val screenState: SearchScreenState,
    private val searchBarPaddingState: MutableState<Dp>,
    val onClear: () -> Unit,
    val onSearch: (query: String) -> Unit,
) {
    val searchBarPadding by searchBarPaddingState

    fun clear() {
        onClear()
    }

    internal fun search(query: String) {
        onSearch(query)
    }

    internal fun updateSearchBarPadding(padding: Dp) {
        searchBarPaddingState.value = padding
    }
}