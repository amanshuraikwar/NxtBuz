package io.github.amanshuraikwar.nxtbuz.ui.main.fragment.starred

import androidx.lifecycle.LiveData
import io.github.amanshuraikwar.nxtbuz.common.model.BusStop
import io.github.amanshuraikwar.nxtbuz.common.model.Event
import io.github.amanshuraikwar.nxtbuz.ui.starred.model.StarredBusArrivalClicked
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

interface StarredArrivalsViewModelDelegate {

    val startStarredBusArrivalActivity: LiveData<Event<Unit>>
    val starredBusArrivalRemoved: LiveData<Event<Pair<BusStop, String>>>

    fun start(
        coroutineScope: CoroutineScope,
        onStarredItemClicked: (busStop: BusStop, busServiceNumber: String) -> Unit
    ): Job

    val starredBusArrivalOptionsDialog: LiveData<Event<StarredBusArrivalClicked>>
}