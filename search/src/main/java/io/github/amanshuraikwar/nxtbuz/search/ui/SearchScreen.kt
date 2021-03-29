package io.github.amanshuraikwar.nxtbuz.search.ui

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
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

@OptIn(ExperimentalAnimationApi::class)
@ExperimentalComposeUiApi
@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    vm: SearchViewModel,
    onBusStopSelected: (BusStop) -> Unit = {}
) {
    val screenState by vm.screenState.collectAsState(initial = null)
    var padding by remember {
        mutableStateOf(0.dp)
    }
    val density = LocalDensity.current
    val insets = LocalWindowInsets.current

    Box(
        modifier
    ) {
        if (screenState != null) {
            Surface {
                SearchResults(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(),
                    screenState = screenState,
                    contentPadding = padding,
                    onBusStopSelected = {
                        vm.clear()
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
                    padding =
                        with(density) { it.height.toDp() + insets.statusBars.top.toDp() + 32.dp }
                },
            screenState = screenState,
            onSearch = { query ->
                vm.searchBusStops(query)
            },
            onBack = {
                vm.clear()
            }
        )
    }
}