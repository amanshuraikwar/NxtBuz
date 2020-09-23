package io.github.amanshuraikwar.nxtbuz.ui.starred

import android.util.Log
import androidx.lifecycle.*
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.github.amanshuraikwar.multiitemadapter.RecyclerViewListItem
import io.github.amanshuraikwar.nxtbuz.common.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.common.model.Arrivals
import io.github.amanshuraikwar.nxtbuz.common.model.BusStop
import io.github.amanshuraikwar.nxtbuz.common.model.Event
import io.github.amanshuraikwar.nxtbuz.common.model.StarredBusArrival
import io.github.amanshuraikwar.nxtbuz.domain.busstop.GetBusStopUseCase
import io.github.amanshuraikwar.nxtbuz.domain.starred.AttachStarredBusArrivalsUseCase
import io.github.amanshuraikwar.nxtbuz.listitem.HeaderItem
import io.github.amanshuraikwar.nxtbuz.listitem.StarredBusArrivalCompactSmallErrorItem
import io.github.amanshuraikwar.nxtbuz.listitem.StarredBusArrivalCompactSmallItem
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
import javax.inject.Named

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class StarredBusArrivalsViewModel @Inject constructor(
    private val attachStarredBusArrivalsUseCase: AttachStarredBusArrivalsUseCase,
    private val getBusStopUseCase: GetBusStopUseCase,
    @Named("starred-bus-arrival-removed-event") val remove: LiveData<Event<Pair<BusStop, String>>>,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) : ViewModel() {

    private val _listItems = MutableLiveData<MutableList<RecyclerViewListItem>>()
    val listItems = _listItems.map { it }

    private val _starredBusArrivalClicked = MutableLiveData<StarredBusArrivalClicked>()
    val starredBusArrivalClicked = _starredBusArrivalClicked.asEvent()

    private val _starredBusArrivalOptionsDialog = MutableLiveData<StarredBusArrivalClicked>()
    val starredBusArrivalOptionsDialog = _starredBusArrivalOptionsDialog.asEvent()

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

                listItems.add(
                    HeaderItem(
                        busStopDescription
                    )
                )

                starredBusArrivalList.forEach {
                    listItems.add(
                        if (it.arrivals is Arrivals.Arriving)
                            StarredBusArrivalCompactSmallItem(
                                it, ::onStarredItemClicked, ::onLongClick
                            )
                        else
                            StarredBusArrivalCompactSmallErrorItem(
                                it,
                                ::onLongClick
                            )
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

    private fun onLongClick(busStopCode: String, busServiceNumber: String) {
        viewModelScope.launch(dispatcherProvider.io) {
            _starredBusArrivalOptionsDialog.postValue(
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
