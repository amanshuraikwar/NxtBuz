package io.github.amanshuraikwar.nxtbuz.starred.ui.delegate

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.github.amanshuraikwar.multiitemadapter.RecyclerViewListItem
import io.github.amanshuraikwar.nxtbuz.common.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.common.model.Arrivals
import io.github.amanshuraikwar.nxtbuz.common.model.BusStop
import io.github.amanshuraikwar.nxtbuz.common.model.Event
import io.github.amanshuraikwar.nxtbuz.common.model.StarredBusArrival
import io.github.amanshuraikwar.nxtbuz.domain.busstop.GetBusStopUseCase
import io.github.amanshuraikwar.nxtbuz.listitem.StarredBusArrivalBtnItem
import io.github.amanshuraikwar.nxtbuz.listitem.StarredBusArrivalErrorItem
import io.github.amanshuraikwar.nxtbuz.listitem.StarredBusArrivalItem
import io.github.amanshuraikwar.nxtbuz.common.model.StarredBusArrivalClicked
import io.github.amanshuraikwar.nxtbuz.common.util.asEvent
import io.github.amanshuraikwar.nxtbuz.common.util.post
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@ExperimentalCoroutinesApi
class StarredArrivalsViewModelDelegateImpl @Inject constructor(
    private val attachStarredBusArrivalsUseCase: io.github.amanshuraikwar.nxtbuz.starred.domain.AttachStarredBusArrivalsUseCase,
    private val getBusStopUseCase: GetBusStopUseCase,
    private val showErrorStarredBusArrivalsUseCase: io.github.amanshuraikwar.nxtbuz.starred.domain.ShowErrorStarredBusArrivalsUseCase,
    @Named("starredListItems") private val _starredListItems: MutableLiveData<MutableList<RecyclerViewListItem>>,
    @Named("startStarredBusArrivalActivity") private val _startStarredBusArrivalActivity: MutableLiveData<Unit>,
    @Named("starred-bus-arrival-removed-event") override val starredBusArrivalRemoved: LiveData<Event<Pair<BusStop, String>>>,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) : StarredArrivalsViewModelDelegate {

    private lateinit var viewModelScope: CoroutineScope
    private lateinit var onStarredItemClicked: (busStop: BusStop, busServiceNumber: String) -> Unit

    override val startStarredBusArrivalActivity = _startStarredBusArrivalActivity.asEvent()

    private val _starredBusArrivalOptionsDialog = MutableLiveData<StarredBusArrivalClicked>()
    override val starredBusArrivalOptionsDialog = _starredBusArrivalOptionsDialog.asEvent()

    @InternalCoroutinesApi
    override fun start(
        coroutineScope: CoroutineScope,
        onStarredItemClicked: (busStop: BusStop, busServiceNumber: String) -> Unit
    ) = coroutineScope.launch(dispatcherProvider.io) {
        this@StarredArrivalsViewModelDelegateImpl.onStarredItemClicked = onStarredItemClicked
        this@StarredArrivalsViewModelDelegateImpl.viewModelScope = coroutineScope
        attachStarredBusArrivalsUseCase(
            "main-fragment-view-model", considerFilteringError = true)
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
                        StarredBusArrivalItem(
                            it,
                            ::onStarredItemClicked,
                            ::onLongClick
                        )
                    else
                        StarredBusArrivalErrorItem(
                            it,
                            ::onLongClick
                        )
                }
                .toMutableList()
                .also {
                    if (it.isNotEmpty()) {
                        it.add(
                            StarredBusArrivalBtnItem(
                                "See All"
                            ) {
                                _startStarredBusArrivalActivity.post()
                            }
                        )
                    }
                }
        )
    }

    private fun onStarredItemClicked(busStopCode: String, busServiceNumber: String) {
        viewModelScope.launch(dispatcherProvider.io) {
            onStarredItemClicked(getBusStopUseCase(busStopCode), busServiceNumber)
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
        private const val TAG = "StarredArrivalsViewMode"
    }
}