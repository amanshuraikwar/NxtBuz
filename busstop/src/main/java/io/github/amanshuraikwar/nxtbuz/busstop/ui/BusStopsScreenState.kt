package io.github.amanshuraikwar.nxtbuz.busstop.ui

import androidx.annotation.StringRes
import io.github.amanshuraikwar.multiitemadapter.RecyclerViewListItem

sealed class BusStopsScreenState {
    data class Loading(@StringRes val loadingTitle: Int) : BusStopsScreenState()
    data class Success(val itemList: MutableList<RecyclerViewListItem>) : BusStopsScreenState()
    data class Failed(val error: Error = Error()) : BusStopsScreenState()
}