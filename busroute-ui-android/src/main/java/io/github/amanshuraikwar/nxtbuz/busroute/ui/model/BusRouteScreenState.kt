package io.github.amanshuraikwar.nxtbuz.busroute.ui.model

import androidx.compose.runtime.MutableState

sealed class BusRouteScreenState {
    object Fetching : BusRouteScreenState()
    data class Failed(
        val header: BusRouteHeaderData?,
    ) : BusRouteScreenState()

    data class Success(
        val header: BusRouteHeaderData,
        val currentBusStopArrivalsData: MutableState<CurrentBusStopArrivalsData>,
        val listItems: List<BusRouteListItemData>
    ) : BusRouteScreenState()
}