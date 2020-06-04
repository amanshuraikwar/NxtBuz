package io.github.amanshuraikwar.nxtbuz.ui.main.fragment.starred

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.github.amanshuraikwar.multiitemadapter.RecyclerViewListItem
import io.github.amanshuraikwar.nxtbuz.data.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.data.busarrival.model.Arrivals
import io.github.amanshuraikwar.nxtbuz.data.busarrival.model.StarredBusArrival
import io.github.amanshuraikwar.nxtbuz.data.busstop.model.BusStop
import io.github.amanshuraikwar.nxtbuz.domain.starred.AttachStarredBusArrivalsUseCase
import io.github.amanshuraikwar.nxtbuz.domain.busstop.GetBusStopUseCase
import io.github.amanshuraikwar.nxtbuz.ui.list.*
import io.github.amanshuraikwar.nxtbuz.ui.main.fragment.busstoparrivals.BusStopArrivalsViewModelDelegate
import io.github.amanshuraikwar.nxtbuz.ui.main.fragment.model.Alert
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onCompletion
import javax.inject.Inject
import javax.inject.Named

@ExperimentalCoroutinesApi
class StarredArrivalsViewModelDelegate @Inject constructor(
    private val attachStarredBusArrivalsUseCase: AttachStarredBusArrivalsUseCase,
    private val getBusStopUseCase: GetBusStopUseCase,
    @Named("starredListItems") private val _starredListItems: MutableLiveData<MutableList<RecyclerViewListItem>>,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) {

    private lateinit var viewModelScope: CoroutineScope
    private lateinit var onStarredItemClicked: (busStop: BusStop, busServiceNumber: String) -> Unit

    @InternalCoroutinesApi
    fun start(
        coroutineScope: CoroutineScope,
        onStarredItemClicked: (busStop: BusStop, busServiceNumber: String) -> Unit
    ) = coroutineScope.launch(dispatcherProvider.io) {
        this@StarredArrivalsViewModelDelegate.onStarredItemClicked = onStarredItemClicked
        this@StarredArrivalsViewModelDelegate.viewModelScope = coroutineScope
        attachStarredBusArrivalsUseCase("main-fragment-view-model")
            .catch { throwable ->
                FirebaseCrashlytics.getInstance().recordException(throwable)
            }
            .onCompletion {
                Log.i(TAG, "start: onCompletion")
            }
            .collect(
                object : FlowCollector<List<StarredBusArrival>> {
                    override suspend fun emit(value: List<StarredBusArrival>) {
                        Log.d(TAG, "emit: ")
                        handleStarredBusArrivalList(value)
                    }
                }
            )
    }

    private fun handleStarredBusArrivalList(starredBusArrivalList: List<StarredBusArrival>) {
        _starredListItems.postValue(
            starredBusArrivalList
                .map {
                    if (it.arrivals is Arrivals.Arriving)
                        StarredBusArrivalItem(it, ::onStarredItemClicked)
                    else
                        StarredBusArrivalErrorItem(it)
                }
                .toMutableList()
        )
    }

    private fun onStarredItemClicked(busStopCode: String, busServiceNumber: String) {
        viewModelScope.launch(dispatcherProvider.io) {
            onStarredItemClicked(getBusStopUseCase(busStopCode), busServiceNumber)
        }
    }

    companion object {
        private const val TAG = "StarredArrivalsViewMode"
    }
}