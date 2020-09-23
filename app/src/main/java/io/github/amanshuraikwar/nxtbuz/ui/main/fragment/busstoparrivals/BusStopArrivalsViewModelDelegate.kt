package io.github.amanshuraikwar.nxtbuz.ui.main.fragment.busstoparrivals

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.github.amanshuraikwar.multiitemadapter.RecyclerViewListItem
import io.github.amanshuraikwar.nxtbuz.R
import io.github.amanshuraikwar.nxtbuz.common.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.common.model.Arrivals
import io.github.amanshuraikwar.nxtbuz.common.model.BusArrival
import io.github.amanshuraikwar.nxtbuz.common.model.BusStop
import io.github.amanshuraikwar.nxtbuz.domain.busarrival.GetBusArrivalFlowUseCase
import io.github.amanshuraikwar.nxtbuz.domain.busarrival.StopBusArrivalFlowUseCase
import io.github.amanshuraikwar.nxtbuz.listitem.BusArrivalCompactItem
import io.github.amanshuraikwar.nxtbuz.listitem.BusArrivalErrorItem
import io.github.amanshuraikwar.nxtbuz.listitem.BusStopHeaderItem
import io.github.amanshuraikwar.nxtbuz.listitem.HeaderItem
import io.github.amanshuraikwar.nxtbuz.ui.main.fragment.Loading
import io.github.amanshuraikwar.nxtbuz.ui.main.fragment.ScreenState
import io.github.amanshuraikwar.nxtbuz.ui.main.fragment.map.MapViewModelDelegate
import io.github.amanshuraikwar.nxtbuz.ui.main.fragment.model.*
import io.github.amanshuraikwar.nxtbuz.util.post
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onCompletion
import javax.inject.Inject
import javax.inject.Named

@FlowPreview
@ExperimentalCoroutinesApi
class BusStopArrivalsViewModelDelegate @Inject constructor(
    private val getBusArrivalFlowUseCase: GetBusArrivalFlowUseCase,
    private val stopBusArrivalFlowUseCase: StopBusArrivalFlowUseCase,
    @Named("loading") private val _loading: MutableLiveData<Loading>,
    @Named("listItems") private val _listItems: MutableLiveData<List<RecyclerViewListItem>>,
    @Named("collapseBottomSheet") private val _collapseBottomSheet: MutableLiveData<Unit>,
    @Named("error") private val _error: MutableLiveData<Alert>,
    private val mapViewModelDelegate: MapViewModelDelegate,
    private val busStopArrivalsMapMarkerHelper: BusStopArrivalsMapMarkerHelper,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) {

    private lateinit var curBusStopState: ScreenState.BusStopState
    private lateinit var viewModelScope: CoroutineScope
    private lateinit var onStarToggle: (busStopCode: String, busArrival: BusArrival) -> Unit
    private lateinit var onBusServiceClicked: (busServiceNumber: String) -> Unit

    private var mapStateId: Int = 0

    private val errorHandler = CoroutineExceptionHandler { _, th ->
        Log.e(TAG, "errorHandler: $th", th)
        FirebaseCrashlytics.getInstance().recordException(th)
    }

    fun stop(busStopState: ScreenState.BusStopState) {
        if (busStopState == curBusStopState) {
            // todo make this safer by only destroying the service
            //  if the service is currently running for this bus stop
            stopBusArrivalFlowUseCase()
        }
    }

    @InternalCoroutinesApi
    suspend fun start(
        busStopState: ScreenState.BusStopState,
        onStarToggle: (busStopCode: String, busArrival: BusArrival) -> Unit,
        onBusServiceClicked: (busStop: BusStop, busServiceNumber: String) -> Unit,
        coroutineScope: CoroutineScope
    ) = coroutineScope.launch(dispatcherProvider.io) {
        viewModelScope = coroutineScope
        this@BusStopArrivalsViewModelDelegate.onStarToggle = onStarToggle
        this@BusStopArrivalsViewModelDelegate.onBusServiceClicked = { busServiceNumber ->
            onBusServiceClicked(busStopState.busStop, busServiceNumber)
        }
        _loading.postValue(
            Loading.Show(
                R.drawable.avd_anim_arrivals_loading_128,
                "Finding bus arrivals..."
            )
        )
        _collapseBottomSheet.post()
        curBusStopState = busStopState
        busStopArrivalsMapMarkerHelper.clear()

        mapStateId = mapViewModelDelegate.newState(::onMapMarkerClicked)
        busStopArrivalsMapMarkerHelper.mapStateId = mapStateId

        mapViewModelDelegate.pushMapEvent(
            mapStateId,
            MapEvent.ClearMap
        )
        mapViewModelDelegate.pushMapEvent(
            mapStateId,
            MapEvent.AddMapMarkers(
                listOf(
                    MapMarker(
                        curBusStopState.busStop.code,
                        curBusStopState.busStop.latitude,
                        curBusStopState.busStop.longitude,
                        R.drawable.ic_marker_bus_stop_48,
                        curBusStopState.busStop.description
                    )
                )
            )
        )
        mapViewModelDelegate.pushMapEvent(
            mapStateId,
            MapEvent.MoveCenter(
                curBusStopState.busStop.latitude,
                curBusStopState.busStop.longitude
            )
        )
        getBusArrivalFlowUseCase(busStopState.busStop.code)
            .catch { throwable ->
                FirebaseCrashlytics.getInstance().recordException(throwable)
                _error.postValue(Alert("Something went wrong. Please restart the app."))
            }
            .onCompletion {
                Log.i(TAG, "start: onCompletion")
            }
            .collect(
                object : FlowCollector<List<BusArrival>> {
                    override suspend fun emit(value: List<BusArrival>) {
                        Log.d(TAG, "emit: ")
                        handleBusArrivalList(
                            busStopState.busStop,
                            onStarToggle,
                            value
                        )
                    }
                }
            )
    }

    private suspend fun handleBusArrivalList(
        busStop: BusStop,
        onStarToggle: (busStopCode: String, busArrival: BusArrival) -> Unit,
        busArrivals: List<BusArrival>
    ) = withContext(dispatcherProvider.computation) {

        val listItems: MutableList<RecyclerViewListItem> =
            busArrivals
                .map {
                    if (it.arrivals is Arrivals.Arriving) {
                        BusArrivalCompactItem(
                            busStop.code,
                            it,
                            onStarToggle,
                            onBusServiceClicked
                        )
                    } else {
                        BusArrivalErrorItem(
                            busStop.code,
                            it,
                            onStarToggle
                        )
                    }
                }
                .toMutableList()

        listItems.add(
            0,
            BusStopHeaderItem(
                busStop,
                R.drawable.ic_bus_stop_24
            )
        )

        listItems.add(
            1,
            HeaderItem(
                "Arrivals"
            )
        )

        if (isActive) {
            _listItems.postValue(listItems)
            _loading.postValue(Loading.Hide)
            busStopArrivalsMapMarkerHelper.showMapMarkers(busArrivals)
        }
    }

    fun clear() {
        stopBusArrivalFlowUseCase()
    }

    private fun onMapMarkerClicked(markerId: String) = viewModelScope.launch(errorHandler) {
        onBusServiceClicked(markerId)
    }

    companion object {
        private const val TAG = "BusStopViewModelDelegat"
    }
}