package io.github.amanshuraikwar.nxtbuz.busstop.arrivals.model

sealed class BusStopArrivalsScreenState {
    object Fetching : BusStopArrivalsScreenState()
    object Failed : BusStopArrivalsScreenState()
    data class Success(
        val header: BusStopArrivalListItemData.BusStopHeader,
        val listItems: List<BusStopArrivalListItemData>
    ) : BusStopArrivalsScreenState()
}