package io.github.amanshuraikwar.nxtbuz.busstop.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.Marker
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.github.amanshuraikwar.multiitemadapter.RecyclerViewListItem
import io.github.amanshuraikwar.nxtbuz.busstop.R
import io.github.amanshuraikwar.nxtbuz.common.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.common.model.BusStop
import io.github.amanshuraikwar.nxtbuz.common.model.map.MapEvent
import io.github.amanshuraikwar.nxtbuz.common.model.map.MapMarker
import io.github.amanshuraikwar.nxtbuz.common.model.map.MapResult
import io.github.amanshuraikwar.nxtbuz.domain.busstop.BusStopsQueryLimitUseCase
import io.github.amanshuraikwar.nxtbuz.domain.busstop.GetBusStopUseCase
import io.github.amanshuraikwar.nxtbuz.domain.busstop.GetBusStopsUseCase
import io.github.amanshuraikwar.nxtbuz.domain.location.GetLocationUpdatesUseCase
import io.github.amanshuraikwar.nxtbuz.domain.location.PushMapEventUseCase
import io.github.amanshuraikwar.nxtbuz.listitem.BusStopItem
import io.github.amanshuraikwar.nxtbuz.listitem.HeaderItem
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Named

private const val TAG = "BusStopsViewModel"

class BusStopsViewModel @Inject constructor(
    private val getLocationUpdatesUseCase: GetLocationUpdatesUseCase,
    private val getBusStopsUseCase: GetBusStopsUseCase,
    private val busStopsQueryLimitUseCase: BusStopsQueryLimitUseCase,
    private val pushMapEventUseCase: PushMapEventUseCase,
    @Named("bottomSheetSlideOffset")
    private val bottomSheetSlideOffsetFlow: MutableStateFlow<Float>,
    @Named("markerClicked") private val markerClickedFlow: MutableStateFlow<Marker?>,
    private val getBusStopUseCase: GetBusStopUseCase,
    dispatcherProvider: CoroutinesDispatcherProvider
) : ViewModel() {

    private val _busStopScreenState = MutableSharedFlow<BusStopsScreenState>(replay = 1)
    val busStopScreenState: SharedFlow<BusStopsScreenState> = _busStopScreenState

    private var onBusStopClicked: (BusStop) -> Unit = {
        viewModelScope.launch(coroutineContext) {
            // TODO-amanshuraikwar (27 Jan 2021 05:31:49 PM): redirect to bus stop screen
            _busStopScreenState.emit(
                BusStopsScreenState.Failed(Error(errorTitle = R.string.app_name))
            )
        }
    }

    private val markerIdMap = mutableMapOf<String, Marker>()
    private val markerIdBusStopCodeMap = mutableMapOf<String, String>()

    private val errorHandler = CoroutineExceptionHandler { _, th ->
        Log.e(TAG, "errorHandler: $th", th)
        FirebaseCrashlytics.getInstance().recordException(th)
        failed(
            Error()
        )
    }
    private val coroutineContext = errorHandler + dispatcherProvider.computation

    init {
        fetchBusStops()
        collectMarkerClicks()
    }

    private fun collectMarkerClicks() {
        viewModelScope.launch(coroutineContext) {
            markerClickedFlow.collect { marker ->
                onMapMarkerClicked(markerId = marker?.id ?: return@collect)
            }
        }
    }

    fun fetchBusStops() {
        viewModelScope.launch(coroutineContext) {
            _busStopScreenState.emit(
                BusStopsScreenState.Loading(R.string.bus_stop_message_loading_nearby)
            )
            getLocationUpdatesUseCase().collect { location ->
                val busStopList = getBusStopsUseCase(
                    lat = location.lat,
                    lon = location.lng,
                    limit = busStopsQueryLimitUseCase()
                )
                val listItems = getListItems(busStopList, onBusStopClicked)
                _busStopScreenState.emit(BusStopsScreenState.Success(listItems))
                val mapResult = pushMapEventUseCase(
                    MapEvent.AddMapMarkers(
                        busStopList.map { busStop ->
                            //mapMarkerIdBusStopMap[busStop.code] = busStop
                            MapMarker(
                                busStop.code,
                                busStop.latitude,
                                busStop.longitude,
                                R.drawable.ic_marker_bus_stop_48,
                                busStop.description
                            )
                        }
                    )
                )
                (mapResult as? MapResult.AddMapMarkersResult)?.markerList?.forEachIndexed { index, marker ->
                    markerIdMap[marker.id] = marker
                    markerIdBusStopCodeMap[marker.id] = busStopList[index].code
                }
            }
        }
    }

    private fun failed(error: Error) {
        viewModelScope.launch {
            _busStopScreenState.emit(BusStopsScreenState.Failed(error))
        }
    }

    fun updateBottomSheetSlideOffset(slideOffset: Float) {
        viewModelScope.launch(coroutineContext) {
            bottomSheetSlideOffsetFlow.value = slideOffset
        }
    }

    private fun getListItems(
        busStopList: List<BusStop>,
        onBusStopClicked: (BusStop) -> Unit
    ): MutableList<RecyclerViewListItem> {
        val listItems = mutableListOf<RecyclerViewListItem>(
            HeaderItem("Nearby Bus Stops")
        )
        busStopList.forEach {
            listItems.add(
                BusStopItem(
                    it,
                    R.drawable.ic_bus_stop_24,
                    onBusStopClicked,
                )
            )
        }
        return listItems
    }

    private suspend fun onMapMarkerClicked(markerId: String) {
        if (markerIdMap.containsKey(markerId)) {
            onBusStopClicked(getBusStopUseCase(markerIdBusStopCodeMap[markerId] ?: return))
        }
    }
}