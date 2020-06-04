package io.github.amanshuraikwar.nxtbuz.ui.starred

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.github.amanshuraikwar.multiitemadapter.RecyclerViewListItem
import io.github.amanshuraikwar.nxtbuz.data.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.data.busarrival.model.Arrivals
import io.github.amanshuraikwar.nxtbuz.data.busarrival.model.StarredBusArrival
import io.github.amanshuraikwar.nxtbuz.data.busstop.model.BusStop
import io.github.amanshuraikwar.nxtbuz.domain.busstop.GetBusStopUseCase
import io.github.amanshuraikwar.nxtbuz.domain.starred.AttachStarredBusArrivalsUseCase
import io.github.amanshuraikwar.nxtbuz.ui.list.HeaderItem
import io.github.amanshuraikwar.nxtbuz.ui.list.StarredBusArrivalCompactSmallErrorItem
import io.github.amanshuraikwar.nxtbuz.ui.list.StarredBusArrivalCompactSmallItem
import io.github.amanshuraikwar.nxtbuz.ui.starred.model.StarredBusArrivalClicked
import io.github.amanshuraikwar.nxtbuz.util.asEvent
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class StarredBusArrivalsViewModel @Inject constructor(
    private val attachStarredBusArrivalsUseCase: AttachStarredBusArrivalsUseCase,
    private val getBusStopUseCase: GetBusStopUseCase,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) : ViewModel() {

    private val _listItems = MutableLiveData<MutableList<RecyclerViewListItem>>()
    val listItems = _listItems.map { it }

    private val _starredBusArrivalClicked = MutableLiveData<StarredBusArrivalClicked>()
    val starredBusArrivalClicked = _starredBusArrivalClicked.asEvent()

    private val errorHandler = CoroutineExceptionHandler { _, th ->
        Log.e(TAG, "errorHandler: $th", th)
        FirebaseCrashlytics.getInstance().recordException(th)
    }

    init {
        FirebaseCrashlytics.getInstance().setCustomKey("viewModel", TAG)
        start()
    }

    private fun start() =
        viewModelScope.launch(dispatcherProvider.io + errorHandler) {
            attachStarredBusArrivalsUseCase("starred-bus-arrivals-view-model")
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
        val listItems = mutableListOf<RecyclerViewListItem>()

        starredBusArrivalList
            .groupBy { it.busStopDescription }
            .forEach { (busStopDescription, starredBusArrivalList) ->

                listItems.add(HeaderItem(busStopDescription))

                starredBusArrivalList.forEach {
                    listItems.add(
                        if (it.arrivals is Arrivals.Arriving)
                            StarredBusArrivalCompactSmallItem(it, ::onStarredItemClicked)
                        else
                            StarredBusArrivalCompactSmallErrorItem(it)
                    )
                }
            }

        _listItems.postValue(listItems)
    }

    private fun onStarredItemClicked(busStopCode: String, busServiceNumber: String) {
        viewModelScope.launch(dispatcherProvider.io) {
            _starredBusArrivalClicked.postValue(
                StarredBusArrivalClicked(
                    getBusStopUseCase(busStopCode), busServiceNumber
                )
            )
        }
    }

    companion object {
        private const val TAG = "StarredBusArrivalsVm"
    }
}
