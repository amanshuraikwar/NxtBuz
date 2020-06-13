package io.github.amanshuraikwar.nxtbuz.ui.main.fragment.busstoparrivals

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.github.amanshuraikwar.multiitemadapter.RecyclerViewListItem
import io.github.amanshuraikwar.nxtbuz.R
import io.github.amanshuraikwar.nxtbuz.data.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.data.busarrival.model.Arrivals
import io.github.amanshuraikwar.nxtbuz.data.busarrival.model.BusArrival
import io.github.amanshuraikwar.nxtbuz.data.busstop.model.BusStop
import io.github.amanshuraikwar.nxtbuz.domain.busarrival.GetBusArrivalFlowUseCase
import io.github.amanshuraikwar.nxtbuz.domain.busarrival.StopBusArrivalFlowUseCase
import io.github.amanshuraikwar.nxtbuz.ui.list.BusArrivalCompactItem
import io.github.amanshuraikwar.nxtbuz.ui.list.BusArrivalErrorItem
import io.github.amanshuraikwar.nxtbuz.ui.list.BusStopHeaderItem
import io.github.amanshuraikwar.nxtbuz.ui.list.HeaderItem
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
    private val dispatcherProvider: CoroutinesDispatcherProvider
) {

    private lateinit var curBusStopState: ScreenState.BusStopState
    private lateinit var viewModelScope: CoroutineScope
    private lateinit var onStarToggle: (busStopCode: String, busArrival: BusArrival) -> Unit
    private lateinit var onBusServiceClicked: (busServiceNumber: String) -> Unit

    private val serviceNumberMapMarkerMap =
        mutableMapOf<String, ArrivingBusMapMarker>()

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
        serviceNumberMapMarkerMap.clear()
        mapViewModelDelegate.pushMapEvent(
            MapEvent.ClearMap
        )
        mapViewModelDelegate.pushMapEvent(
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

            val busAddList = mutableListOf<ArrivingBusMapMarker>()
            val busDeleteList = mutableListOf<String>()
            val busUpdateList = mutableListOf<MapUpdate>()

            busArrivals
                .forEach { busArrival ->
                    when (busArrival.arrivals) {
                        is Arrivals.Arriving -> {
                            serviceNumberMapMarkerMap[busArrival.serviceNumber]
                                ?.let { mapMarker ->
                                    if (busArrival.arrivals.nextArrivingBus.latitude != mapMarker.lat
                                        || busArrival.arrivals.nextArrivingBus.longitude != mapMarker.lng
                                    ) {
                                        busUpdateList.add(
                                            MapUpdate(
                                                mapMarker.id,
                                                busArrival.arrivals.nextArrivingBus.latitude,
                                                busArrival.arrivals.nextArrivingBus.longitude
                                            )
                                        )
                                        serviceNumberMapMarkerMap[busArrival.serviceNumber] =
                                            mapMarker.copy(
                                                lat = busArrival.arrivals.nextArrivingBus.latitude,
                                                lng = busArrival.arrivals.nextArrivingBus.longitude
                                            )
                                    }
                                }
                                ?: run {
                                    val mapMarker = ArrivingBusMapMarker(
                                        busArrival.serviceNumber,
                                        busArrival.arrivals.nextArrivingBus.latitude,
                                        busArrival.arrivals.nextArrivingBus.longitude,
                                        if ((busArrival.arrivals).nextArrivingBus.arrival == "Arr") {
                                            "ARRIVING"
                                        } else {
                                            "${(busArrival.arrivals).nextArrivingBus.arrival} MINS"
                                        },
                                        busServiceNumber = busArrival.serviceNumber,
                                    )
                                    serviceNumberMapMarkerMap[busArrival.serviceNumber] =
                                        mapMarker
                                    busAddList.add(mapMarker)
                                }
                        }
                        is Arrivals.DataNotAvailable,
                        is Arrivals.NotOperating -> {
                            serviceNumberMapMarkerMap[busArrival.serviceNumber]
                                ?.let { mapMarker ->
                                    busDeleteList.add(mapMarker.id)
                                    serviceNumberMapMarkerMap.remove(mapMarker.id)
                                }
                                ?: run {
                                    // just ignore
                                }
                        }
                    }
                }

            withContext(dispatcherProvider.main) {
                if (busAddList.isNotEmpty()) {
                    mapViewModelDelegate.pushMapEvent(
                        MapEvent.AddMapMarkers(
                            busAddList
                        )
                    )
                }
                if (busDeleteList.isNotEmpty()) {
                    mapViewModelDelegate.pushMapEvent(
                        MapEvent.DeleteMarker(
                            busDeleteList
                        )
                    )
                }
                if (busUpdateList.isNotEmpty()) {
                    mapViewModelDelegate.pushMapEvent(
                        MapEvent.UpdateMapMarkers(
                            busUpdateList
                        )
                    )
                }
            }

            Log.i(TAG, "startArrivalsLoop: add ${busAddList.size}")
            Log.i(TAG, "startArrivalsLoop: delete ${busDeleteList.size}")
            Log.i(TAG, "startArrivalsLoop: update ${busUpdateList.size}")

            _loading.postValue(Loading.Hide)
        }
    }

    fun clear() {
        stopBusArrivalFlowUseCase()
    }

    companion object {
        private const val TAG = "BusStopViewModelDelegat"
    }
}