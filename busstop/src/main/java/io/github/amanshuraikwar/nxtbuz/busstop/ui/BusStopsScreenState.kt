package io.github.amanshuraikwar.nxtbuz.busstop.ui

import androidx.annotation.StringRes
import io.github.amanshuraikwar.multiitemadapter.RecyclerViewListItem
import io.github.amanshuraikwar.nxtbuz.common.model.BusStop

sealed class BusStopsScreenState {
    data class Loading(@StringRes val loadingTitle: Int) : BusStopsScreenState()
    data class Success(val itemList: MutableList<RecyclerViewListItem>) : BusStopsScreenState()
    data class Failed(val error: Error = Error()) : BusStopsScreenState()
    data class Finish(val toBusStop: BusStop) : BusStopsScreenState()
}