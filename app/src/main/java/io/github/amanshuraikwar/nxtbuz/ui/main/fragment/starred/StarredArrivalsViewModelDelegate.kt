package io.github.amanshuraikwar.nxtbuz.ui.main.fragment.starred

import androidx.lifecycle.LiveData
import io.github.amanshuraikwar.nxtbuz.data.busstop.model.BusStop
import io.github.amanshuraikwar.nxtbuz.domain.result.Event
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

interface StarredArrivalsViewModelDelegate {

    val startStarredBusArrivalActivity: LiveData<Event<Unit>>

    fun start(
        coroutineScope: CoroutineScope,
        onStarredItemClicked: (busStop: BusStop, busServiceNumber: String) -> Unit
    ): Job
}