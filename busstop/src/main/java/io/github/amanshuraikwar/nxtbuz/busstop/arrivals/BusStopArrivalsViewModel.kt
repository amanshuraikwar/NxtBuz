package io.github.amanshuraikwar.nxtbuz.busstop.arrivals

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.github.amanshuraikwar.multiitemadapter.RecyclerViewListItem
import io.github.amanshuraikwar.nxtbuz.busstop.R
import io.github.amanshuraikwar.nxtbuz.busstop.ui.Error
import io.github.amanshuraikwar.nxtbuz.common.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.common.model.Arrivals
import io.github.amanshuraikwar.nxtbuz.common.model.BusArrival
import io.github.amanshuraikwar.nxtbuz.common.model.BusStop
import io.github.amanshuraikwar.nxtbuz.common.model.screenstate.ScreenState
import io.github.amanshuraikwar.nxtbuz.domain.busarrival.GetBusArrivalFlowUseCase
import io.github.amanshuraikwar.nxtbuz.listitem.BusArrivalCompactItem
import io.github.amanshuraikwar.nxtbuz.listitem.BusArrivalErrorItem
import io.github.amanshuraikwar.nxtbuz.listitem.BusStopHeaderItem
import io.github.amanshuraikwar.nxtbuz.listitem.HeaderItem
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collect
import javax.inject.Inject
import javax.inject.Named

private const val TAG = "BusStopArrivalsVM"

class BusStopArrivalsViewModel @Inject constructor(
    private val getBusArrivalFlowUseCase: GetBusArrivalFlowUseCase,
    //private val stopBusArrivalFlowUseCase: StopBusArrivalFlowUseCase,
//    @Named("loading") private val _loading: MutableLiveData<Loading>,
//    @Named("listItems") private val _listItems: MutableLiveData<List<RecyclerViewListItem>>,
    //@Named("collapseBottomSheet") private val _collapseBottomSheet: MutableLiveData<Unit>,
    //@Named("error") private val _error: MutableLiveData<Alert>,
    //private val mapViewModelDelegate: MapViewModelDelegate,
    private val busStopArrivalsMapMarkerHelper: BusStopArrivalsMapMarkerHelper,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) : ViewModel() {

    private val onStarToggle: (busStopCode: String, busArrival: BusArrival) -> Unit = { _, _ ->

    }
    private val onBusServiceClicked: (busServiceNumber: String) -> Unit = {
        // TODO-amanshuraikwar (27 Jan 2021 08:36:07 PM):
    }

    private val _busStopArrivalsScreenState =
        MutableSharedFlow<BusStopArrivalsScreenState>(replay = 1)
    val busStopArrivalsScreenState: SharedFlow<BusStopArrivalsScreenState> =
        _busStopArrivalsScreenState

    private val errorHandler = CoroutineExceptionHandler { _, th ->
        Log.e(TAG, "errorHandler: $th", th)
        FirebaseCrashlytics.getInstance().recordException(th)
        failed(Error())
    }
    private val coroutineContext = errorHandler + dispatcherProvider.computation

    fun init(busStop: BusStop) {
        viewModelScope.launch(coroutineContext) {
            getBusArrivalFlowUseCase(busStop.code)
                .collect { busArrivalList ->
                    handleBusArrivalList(
                        busStop,
                        busArrivalList
                    )
                }
        }
    }

    private fun failed(error: Error) {
        viewModelScope.launch {
            _busStopArrivalsScreenState.emit(BusStopArrivalsScreenState.Failed(error))
        }
    }

    fun stop() {
        // todo make this safer by only destroying the service
        //  if the service is currently running for this bus stop
        //stopBusArrivalFlowUseCase()
    }

    @InternalCoroutinesApi
    suspend fun start(
        busStopState: ScreenState.BusStopState,
        onStarToggle: (busStopCode: String, busArrival: BusArrival) -> Unit,
        onBusServiceClicked: (busStop: BusStop, busServiceNumber: String) -> Unit,
        coroutineScope: CoroutineScope
    ) = coroutineScope.launch(dispatcherProvider.io) {
//        viewModelScope = coroutineScope
//        this@BusStopArrivalsViewModelDelegate.onStarToggle = onStarToggle
//        this@BusStopArrivalsViewModelDelegate.onBusServiceClicked = { busServiceNumber ->
//            onBusServiceClicked(busStopState.busStop, busServiceNumber)
//        }
//        _loading.postValue(
//            Loading.Show(
//                R.drawable.avd_anim_arrivals_loading_128,
//                R.string.bus_stop_message_loading_arrivals
//            )
//        )
//        _collapseBottomSheet.post()
//        curBusStopState = busStopState
//        busStopArrivalsMapMarkerHelper.clear()

//        mapStateId = mapViewModelDelegate.newState(::onMapMarkerClicked)
//        busStopArrivalsMapMarkerHelper.mapStateId = mapStateId

//        mapViewModelDelegate.pushMapEvent(
//            mapStateId,
//            MapEvent.ClearMap
//        )
//        mapViewModelDelegate.pushMapEvent(
//            mapStateId,
//            MapEvent.AddMapMarkers(
//                listOf(
//                    MapMarker(
//                        curBusStopState.busStop.code,
//                        curBusStopState.busStop.latitude,
//                        curBusStopState.busStop.longitude,
//                        R.drawable.ic_marker_bus_stop_48,
//                        curBusStopState.busStop.description
//                    )
//                )
//            )
//        )
//        mapViewModelDelegate.pushMapEvent(
//            mapStateId,
//            MapEvent.MoveCenter(
//                curBusStopState.busStop.latitude,
//                curBusStopState.busStop.longitude
//            )
//        )
//        getBusArrivalFlowUseCase(busStopState.busStop.code)
//            .catch { throwable ->
//                FirebaseCrashlytics.getInstance().recordException(throwable)
//                _error.postValue(
//                    Alert(
//                        "Something went wrong. Please restart the app."
//                    )
//                )
//            }
//            .collect(
//                object : FlowCollector<List<BusArrival>> {
//                    override suspend fun emit(value: List<BusArrival>) {
//                        handleBusArrivalList(
//                            busStopState.busStop,
//                            onStarToggle,
//                            value
//                        )
//                    }
//                }
//            )
    }

    private suspend fun handleBusArrivalList(
        busStop: BusStop,
        busArrivals: List<BusArrival>
    ) = withContext(dispatcherProvider.computation) {

//        _busStopArrivalsScreenState.emit(
//            BusStopArrivalsScreenState.Loading(R.string.bus_stop_message_loading_arrivals)
//        )

        val listItems = mutableListOf<RecyclerViewListItem>()

        listItems.add(
            HeaderItem("Bus Stop")
        )

        listItems.add(
            BusStopHeaderItem(
                busStop,
                R.drawable.ic_bus_stop_24
            )
        )

        val arrivingBusListItems = mutableListOf<RecyclerViewListItem>()
        val notArrivingBusListItems = mutableListOf<RecyclerViewListItem>()

        busArrivals.forEach {
            if (it.arrivals is Arrivals.Arriving) {
                arrivingBusListItems.add(
                    BusArrivalCompactItem(
                        busStop.code,
                        it,
                        onStarToggle,
                        onBusServiceClicked
                    )
                )
            } else {
                notArrivingBusListItems.add(
                    BusArrivalErrorItem(
                        busStop.code,
                        it,
                        onStarToggle
                    )
                )
            }
        }

        listItems.addAll(arrivingBusListItems)

        if (notArrivingBusListItems.isNotEmpty()) {
            listItems.add(
                HeaderItem(
                    "Not Arriving"
                )
            )
            listItems.addAll(notArrivingBusListItems)
        }

        if (isActive) {
            _busStopArrivalsScreenState.emit(BusStopArrivalsScreenState.Success(listItems))
//            _listItems.postValue(listItems)
//            _loading.postValue(Loading.Hide)
            //busStopArrivalsMapMarkerHelper.showMapMarkers(busArrivals)
        }
    }

    fun clear() {
        //stopBusArrivalFlowUseCase()
    }

    private fun onMapMarkerClicked(markerId: String) {
        viewModelScope.launch(errorHandler) {
            onBusServiceClicked(markerId)
        }
    }

    companion object {
        private const val TAG = "BusStopArrvlsVmDelegate"
    }
}