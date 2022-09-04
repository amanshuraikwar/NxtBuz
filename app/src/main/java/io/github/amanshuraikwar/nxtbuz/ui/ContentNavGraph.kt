package io.github.amanshuraikwar.nxtbuz.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.google.accompanist.insets.ExperimentalAnimatedInsets
import io.github.amanshuraikwar.nxtbuz.busroute.ui.BusRouteScreen
import io.github.amanshuraikwar.nxtbuz.busroute.ui.BusRouteViewModel
import io.github.amanshuraikwar.nxtbuz.busstop.arrivals.BusStopArrivalsScreen
import io.github.amanshuraikwar.nxtbuz.busstop.arrivals.BusStopArrivalsViewModel
import io.github.amanshuraikwar.nxtbuz.busstop.busstops.BusStopsScreen
import io.github.amanshuraikwar.nxtbuz.busstop.busstops.BusStopsViewModel
import io.github.amanshuraikwar.nxtbuz.ui.model.NavigationState

@ExperimentalAnimationApi
@ExperimentalAnimatedInsets
@ExperimentalMaterialApi
@Composable
fun ContentNavGraph(
    navigationState: NavigationState,
    showBottomSheet: Boolean,
    onBusStopClick: (busStopCode: String) -> Unit,
    onBusServiceClick: (busStopCode: String, busServiceNumber: String) -> Unit,
    bottomSheetBgOffset: Dp,
    busRouteViewModel: BusRouteViewModel,
    busStopArrivalsViewModel: BusStopArrivalsViewModel,
    busStopsViewModel: BusStopsViewModel,
) {
    when (navigationState) {
        is NavigationState.BusRoute -> {
            BusRouteScreen(
                vm = busRouteViewModel,
                busStopCode = navigationState.busStopCode,
                busServiceNumber = navigationState.busServiceNumber,
                bottomSheetBgOffset = bottomSheetBgOffset,
                showBottomSheet = showBottomSheet,
                modifier = Modifier.fillMaxSize(),
                onBusStopClick = onBusStopClick,
            )
        }
        is NavigationState.BusStopArrivals -> {
            BusStopArrivalsScreen(
                vm = busStopArrivalsViewModel,
                busStop = navigationState.busStop,
                onBusServiceClick = onBusServiceClick,
                bottomSheetBgOffset = bottomSheetBgOffset,
                showBottomSheet = showBottomSheet,
                modifier = Modifier.fillMaxSize(),
            )
        }
        is NavigationState.BusStops -> {
            BusStopsScreen(
                vm = busStopsViewModel,
                onBusStopClick = onBusStopClick,
                bottomSheetBgOffset = bottomSheetBgOffset,
                showBottomSheet = showBottomSheet,
                modifier = Modifier.fillMaxSize(),
            )
        }
        NavigationState.Search -> {
            // do nothing
        }
    }
}