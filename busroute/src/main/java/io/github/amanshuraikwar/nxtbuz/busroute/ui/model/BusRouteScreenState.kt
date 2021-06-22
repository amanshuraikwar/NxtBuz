package io.github.amanshuraikwar.nxtbuz.busroute.ui.model

sealed class BusRouteScreenState {
    object Fetching : BusRouteScreenState()
    data class Failed(
            val header: BusRouteHeaderData?,
    ) : BusRouteScreenState()
    data class Success(
        val header: BusRouteHeaderData,
        val listItems: List<BusRouteListItemData>
    ) : BusRouteScreenState()
}