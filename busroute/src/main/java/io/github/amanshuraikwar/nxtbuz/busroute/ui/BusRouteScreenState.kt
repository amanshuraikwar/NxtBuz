package io.github.amanshuraikwar.nxtbuz.busroute.ui

import io.github.amanshuraikwar.multiitemadapter.RecyclerViewListItem
import io.github.amanshuraikwar.nxtbuz.common.model.view.Error

sealed class BusRouteScreenState {
    data class Success(val itemList: MutableList<RecyclerViewListItem>) : BusRouteScreenState()
    data class Failed(val error: Error = Error()) : BusRouteScreenState()
}