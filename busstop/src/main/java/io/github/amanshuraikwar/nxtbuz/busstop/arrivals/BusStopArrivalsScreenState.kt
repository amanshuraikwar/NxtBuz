package io.github.amanshuraikwar.nxtbuz.busstop.arrivals

import io.github.amanshuraikwar.multiitemadapter.RecyclerViewListItem
import io.github.amanshuraikwar.nxtbuz.common.model.view.Error

sealed class BusStopArrivalsScreenState {
    data class Success(val itemList: MutableList<RecyclerViewListItem>) : BusStopArrivalsScreenState()
    data class Failed(val error: Error = Error()) : BusStopArrivalsScreenState()
}