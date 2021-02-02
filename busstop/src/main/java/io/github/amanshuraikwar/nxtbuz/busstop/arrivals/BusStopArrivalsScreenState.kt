package io.github.amanshuraikwar.nxtbuz.busstop.arrivals

import androidx.annotation.StringRes
import io.github.amanshuraikwar.multiitemadapter.RecyclerViewListItem
import io.github.amanshuraikwar.nxtbuz.busstop.ui.Error

sealed class BusStopArrivalsScreenState {
    data class Loading(@StringRes val loadingTitle: Int) : BusStopArrivalsScreenState()
    data class Success(val itemList: MutableList<RecyclerViewListItem>) : BusStopArrivalsScreenState()
    data class Failed(val error: Error = Error()) : BusStopArrivalsScreenState()
    //data class Finish(val toBusStop: BusStop) : BusStopArrivalsScreenState()
}