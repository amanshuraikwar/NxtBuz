package io.github.amanshuraikwar.nxtbuz.search.ui

import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.ExperimentalAnimatedInsets
import io.github.amanshuraikwar.nxtbuz.commonkmm.BusStop
import io.github.amanshuraikwar.nxtbuz.search.ui.model.SearchState

@ExperimentalAnimatedInsets
@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    searchState: SearchState,
    onBusStopSelected: (BusStop) -> Unit = {},
    onBackClick: () -> Unit = {},
) {
    var alpha by remember {
        mutableStateOf(0f)
    }
    var offsetY by remember {
        mutableStateOf(128.dp)
    }

    LaunchedEffect(null) {
        animate(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = tween(300)
        ) { animatedValue, _ ->
            alpha = animatedValue
            offsetY = ((1 - animatedValue) * 128).dp
        }
    }

    Surface(
        modifier
            .alpha(alpha = alpha)
            .offset(y = offsetY)
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