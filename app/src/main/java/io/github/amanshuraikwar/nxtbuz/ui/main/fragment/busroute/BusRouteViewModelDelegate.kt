package io.github.amanshuraikwar.nxtbuz.ui.main.fragment.busroute

import androidx.lifecycle.LiveData
import io.github.amanshuraikwar.nxtbuz.domain.result.Event
import io.github.amanshuraikwar.nxtbuz.ui.list.BusRoutePreviousAllItem
import io.github.amanshuraikwar.nxtbuz.ui.list.BusRoutePreviousItem
import io.github.amanshuraikwar.nxtbuz.ui.main.fragment.busroute.domain.BusArrivalUpdate

interface BusRouteViewModelDelegate {
    suspend fun onBottomSheetCollapsed()
    val primaryBusArrivalUpdate: LiveData<BusArrivalUpdate>
    val previousBusStopItems: LiveData<Event<List<BusRoutePreviousItem>>>
    val hidePreviousBusStopItems: LiveData<Event<BusRoutePreviousAllItem>>
    val secondaryBusArrivalUpdate: LiveData<BusArrivalUpdate>
}