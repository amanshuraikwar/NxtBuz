package io.github.amanshuraikwar.nxtbuz.busstop.busstops.model

sealed class BusStopsScreenState {
    object Fetching : BusStopsScreenState()
    object Failed : BusStopsScreenState()
    data class Success(val listItems: List<BusStopsItemData>) : BusStopsScreenState()
}