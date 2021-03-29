package io.github.amanshuraikwar.nxtbuz.busstop.busstops

import android.util.Log
import androidx.annotation.WorkerThread
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.Marker
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.github.amanshuraikwar.multiitemadapter.RecyclerViewListItem
import io.github.amanshuraikwar.nxtbuz.busstop.R
import io.github.amanshuraikwar.nxtbuz.busstop.busstops.model.BusStopsItemData
import io.github.amanshuraikwar.nxtbuz.common.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.common.model.BusStop
import io.github.amanshuraikwar.nxtbuz.common.model.map.MapEvent
import io.github.amanshuraikwar.nxtbuz.common.model.map.MapMarker
import io.github.amanshuraikwar.nxtbuz.common.model.map.MapResult
import io.github.amanshuraikwar.nxtbuz.common.model.view.Error
import io.github.amanshuraikwar.nxtbuz.domain.busstop.BusStopsQueryLimitUseCase
import io.github.amanshuraikwar.nxtbuz.domain.busstop.GetBusStopUseCase
import io.github.amanshuraikwar.nxtbuz.domain.busstop.GetBusStopsUseCase
import io.github.amanshuraikwar.nxtbuz.domain.location.GetLocationUpdatesUseCase
import io.github.amanshuraikwar.nxtbuz.domain.location.PushMapEventUseCase
import io.github.amanshuraikwar.nxtbuz.listitem.BusStopItem
import io.github.amanshuraikwar.nxtbuz.listitem.HeaderItem
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
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
    @Named("navigateToBusStopArrivals")
    private val navigateToBusStopArrivals: MutableSharedFlow<BusStop>,
    private val getBusStopUseCase: GetBusStopUseCase,
    dispatcherProvider: CoroutinesDispatcherProvider
) : ViewModel() {

    private val onBusStopClicked: (BusStop) -> Unit = {
        viewModelScope.launch(coroutineContext) {
            navigateToBusStopArrivals.emit(it)
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

    internal val listItems = SnapshotStateList<BusStopsItemData>()
    private val listItemsLock = Mutex()

    init {
        fetchBusStops()
        //collectMarkerClicks()
    }

    private fun fetchBusStops() {
        viewModelScope.launch(coroutineContext) {
            getLocationUpdatesUseCase().collect { location ->

                val busStopList = getBusStopsUseCase(
                    lat = location.lat,
                    lon = location.lng,
                    limit = busStopsQueryLimitUseCase()
                )

                //val listItems = getListItems(busStopList, onBusStopClicked)

                //_busStopScreenState.emit(BusStopsScreenState.Success(listItems))
                listItemsLock.withLock {
                    updateListItems(busStopList)
                }

                addBusStopMarkers(busStopList)
            }
        }
    }

    @WorkerThread
    private fun updateListItems(busStopList: List<BusStop>) {
        listItems.clear()

        listItems.add(
            BusStopsItemData.Header("Nearby Bus Stops")
        )

        listItems.addAll(
            busStopList.map { busStop ->
                BusStopsItemData.BusStop(
                    busStopDescription = busStop.description,
                    busStopInfo = "${busStop.roadName} • ${busStop.code}",
                    operatingBuses = busStop.operatingBusList
                        .map { it.serviceNumber }
                        .reduceRight { next, total -> "${if (total.length == 2) "$total  " else if (total.length == 3) "$total " else total}  ${if (next.length == 2) "$next  " else if (next.length == 3) "$next " else next}" },
                    busStop = busStop
                )
            }
        )
    }

    private suspend fun addBusStopMarkers(busStopList: List<BusStop>) {
        val mapResult = pushMapEventUseCase(
            MapEvent.AddMapMarkers(
                busStopList.map { busStop ->
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

    private fun collectMarkerClicks() {
        viewModelScope.launch(coroutineContext) {
            markerClickedFlow.collect { marker ->
                onMapMarkerClicked(markerId = marker?.id ?: return@collect)
            }
        }
    }

    private fun failed(error: Error) {
        viewModelScope.launch {
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