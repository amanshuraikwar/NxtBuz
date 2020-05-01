package io.github.amanshuraikwar.nxtbuz.ui.main.fragment.starred

import android.util.Log
import androidx.lifecycle.MutableLiveData
import io.github.amanshuraikwar.multiitemadapter.RecyclerViewListItem
import io.github.amanshuraikwar.nxtbuz.data.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.data.busarrival.model.Arrivals
import io.github.amanshuraikwar.nxtbuz.data.busstop.model.BusStop
import io.github.amanshuraikwar.nxtbuz.domain.busarrival.GetStarredBusStopsArrivalsUseCase
import io.github.amanshuraikwar.nxtbuz.domain.busstop.GetBusStopUseCase
import io.github.amanshuraikwar.nxtbuz.ui.list.*
import kotlinx.coroutines.*
import javax.inject.Inject
import javax.inject.Named

class StarredArrivalsViewModelDelegate @Inject constructor(
    private val getStarredBusStopsArrivalsUseCase: GetStarredBusStopsArrivalsUseCase,
    private val getBusStopUseCase: GetBusStopUseCase,
    @Named("starredListItems") private val _starredListItems: MutableLiveData<MutableList<RecyclerViewListItem>>,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) {

    private lateinit var viewModelScope: CoroutineScope
    private lateinit var onStarredItemClicked: (busStop: BusStop, busServiceNumber: String) -> Unit
    private var arrivalsLoopJob: Job? = null

    private val arrivalsLoopErrorHandler = CoroutineExceptionHandler { _, _ ->
        Log.i(TAG, "arrivalsLoopErrorHandler: Exception thrown.")
        startStarredBusArrivalsLoopDelayed()
    }

    fun start(
        coroutineScope: CoroutineScope,
        onStarredItemClicked: (busStop: BusStop, busServiceNumber: String) -> Unit
    ) = coroutineScope.launch(dispatcherProvider.io) {
        this@StarredArrivalsViewModelDelegate.onStarredItemClicked = onStarredItemClicked
        viewModelScope = coroutineScope
        arrivalsLoopJob?.cancel()
        startStarredBusArrivalsLoop()
    }

    private fun startStarredBusArrivalsLoop() {
        arrivalsLoopJob = viewModelScope.launch(arrivalsLoopErrorHandler) {
            startArrivalsLoop()
        }
    }

    private fun startStarredBusArrivalsLoopDelayed() {
        arrivalsLoopJob = viewModelScope.launch(arrivalsLoopErrorHandler) {
            startArrivalsLoop(REFRESH_DELAY)
        }
    }

    private suspend fun startArrivalsLoop(initialDelay: Long = 0) =
        withContext(dispatcherProvider.computation) {

            delay(initialDelay)

            while (isActive) {

                _starredListItems.postValue(
                    getStarredBusStopsArrivalsUseCase()
                        .map {
                            if (it.arrivals is Arrivals.Arriving)
                                StarredBusArrivalItem(it, ::onStarredItemClicked)
                            else
                                StarredBusArrivalErrorItem(it)
                        }
                        .toMutableList()
                )

                delay(REFRESH_DELAY)
            }
        }

    private fun onStarredItemClicked(busStopCode: String, busServiceNumber: String) {
        viewModelScope.launch(dispatcherProvider.io) {
            onStarredItemClicked(getBusStopUseCase(busStopCode), busServiceNumber)
        }
    }

    companion object {
        private const val REFRESH_DELAY = 10000L
        private const val TAG = "StarredArrivalsViewMode"
    }
}