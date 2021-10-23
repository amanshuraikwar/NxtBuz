package io.github.amanshuraikwar.nxtbuz.busstop.arrivals.model

sealed class BusStopArrivalsScreenState {
    object Fetching : BusStopArrivalsScreenState()
    data class Failed(
            val header: BusStopArrivalListItemData.BusStopHeader?,
    ) : BusStopArrivalsScreenState()
    data class Success(
        val header: BusStopArrivalListItemData.BusStopHeader,
        val listItems: List<BusStopArrivalListItemData>
    ) : BusStopArrivalsScreenState()
}