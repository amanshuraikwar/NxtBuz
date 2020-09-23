package io.github.amanshuraikwar.nxtbuz.ui.main.fragment.busroute

import androidx.lifecycle.LiveData
import io.github.amanshuraikwar.nxtbuz.listitem.BusRoutePreviousAllItem
import io.github.amanshuraikwar.nxtbuz.listitem.BusRoutePreviousItem
import io.github.amanshuraikwar.nxtbuz.common.model.BusArrivalUpdate
import io.github.amanshuraikwar.nxtbuz.common.model.Event

interface BusRouteViewModelDelegate {
    suspend fun onBottomSheetCollapsed()
    val primaryBusArrivalUpdate: LiveData<BusArrivalUpdate>
    val previousBusStopItems: LiveData<Event<List<BusRoutePreviousItem>>>
    val hidePreviousBusStopItems: LiveData<Event<BusRoutePreviousAllItem>>
    val secondaryBusArrivalUpdate: LiveData<BusArrivalUpdate>
}