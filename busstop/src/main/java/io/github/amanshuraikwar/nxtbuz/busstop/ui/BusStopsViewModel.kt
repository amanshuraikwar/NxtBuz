package io.github.amanshuraikwar.nxtbuz.busstop.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.github.amanshuraikwar.multiitemadapter.RecyclerViewListItem
import io.github.amanshuraikwar.nxtbuz.busstop.R
import io.github.amanshuraikwar.nxtbuz.common.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.common.model.BusStop
import io.github.amanshuraikwar.nxtbuz.domain.busstop.BusStopsQueryLimitUseCase
import io.github.amanshuraikwar.nxtbuz.domain.busstop.GetBusStopUseCase
import io.github.amanshuraikwar.nxtbuz.domain.busstop.GetBusStopsUseCase
import io.github.amanshuraikwar.nxtbuz.listitem.BusStopItem
import io.github.amanshuraikwar.nxtbuz.common.model.Loading
import io.github.amanshuraikwar.nxtbuz.common.model.screenstate.ScreenState
import io.github.amanshuraikwar.nxtbuz.common.model.map.MapEvent
import io.github.amanshuraikwar.nxtbuz.common.model.map.MapMarker
import io.github.amanshuraikwar.nxtbuz.common.util.lerp
import io.github.amanshuraikwar.nxtbuz.common.util.map.MapUtil
import io.github.amanshuraikwar.nxtbuz.common.util.post
import io.github.amanshuraikwar.nxtbuz.domain.location.GetLocationUpdatesUseCase
import io.github.amanshuraikwar.nxtbuz.domain.location.PushMapEventUseCase
import io.github.amanshuraikwar.nxtbuz.listitem.HeaderItem
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import javax.inject.Inject
import javax.inject.Named

private const val TAG = "BusStopsViewModel"

class BusStopsViewModel @Inject constructor(
    private val getLocationUpdatesUseCase: GetLocationUpdatesUseCase,
    private val getBusStopsUseCase: GetBusStopsUseCase,
    private val busStopsQueryLimitUseCase: BusStopsQueryLimitUseCase,
    @Named("bottomSheetSlideOffset")
    private val bottomSheetSlideOffsetFlow: MutableStateFlow<Float>,
//    private val getBusStopUseCase: GetBusStopUseCase,
//    private val pushMapEventUseCase: PushMapEventUseCase,
    //@Named("listItems") private val _listItems: MutableLiveData<List<RecyclerViewListItem>>,
    //@Named("loading") private val _loading: MutableLiveData<Loading>,
    //@Named("collapseBottomSheet") private val _collapseBottomSheet: MutableLiveData<Unit>,
//    private val mapViewModelDelegate: MapViewModelDelegate,
    //private val mapUtil: MapUtil,
    dispatcherProvider: CoroutinesDispatcherProvider
) : ViewModel() {

    private val _listItems = MutableLiveData<MutableList<RecyclerViewListItem>>()
    val listItems: LiveData<MutableList<RecyclerViewListItem>> = _listItems


    //private lateinit var curBusStopsState: ScreenState.BusStopsState
    //private lateinit var coroutineScope: CoroutineScope

    private var onBusStopClicked: (BusStop) -> Unit = {
        // TODO-amanshuraikwar (25 Jan 2021 10:07:46 PM):
    }

    //private val mapMarkerIdBusStopMap = mutableMapOf<String, BusStop>()

    private val errorHandler = CoroutineExceptionHandler { _, th ->
        Log.e(TAG, "errorHandler: $th", th)
        FirebaseCrashlytics.getInstance().recordException(th)
    }
    private val coroutineContext = errorHandler + dispatcherProvider.computation

    init {
        init()
    }

    private fun init() {
        viewModelScope.launch(coroutineContext) {
            getLocationUpdatesUseCase().collect { location ->
                val busStopList = getBusStopsUseCase(
                    lat = location.lat,
                    lon = location.lng,
                    limit = busStopsQueryLimitUseCase()
                )
                val listItems = getListItems(busStopList, onBusStopClicked)
                _listItems.postValue(listItems)
            }
        }
    }

    fun updateBottomSheetSlideOffset(slideOffset: Float) {
        viewModelScope.launch(coroutineContext) {
            bottomSheetSlideOffsetFlow.value = slideOffset
        }
    }

    /*
    suspend fun start(
        busStopsState: ScreenState.BusStopsState,
        onBusStopClicked: (BusStop) -> Unit,
        coroutineScope: CoroutineScope,
    ) = withContext(dispatcherProvider.io) {
        this@BusStopsViewModel.coroutineScope = coroutineScope
        this@BusStopsViewModel.onBusStopClicked = onBusStopClicked
        _loading.postValue(
            Loading.Show(
                R.drawable.avd_anim_nearby_bus_stops_loading_128,
                R.string.bus_stop_message_loading_nearby
            )
        )
        _collapseBottomSheet.post()
        curBusStopsState = busStopsState


        //val mapStateId = mapViewModelDelegate.newState(::onMapMarkerClicked)

//        pushMapEventUseCase(
//            MapEvent.ClearMap
//        )
//        pushMapEventUseCase(
//            MapEvent.MoveCenter(curBusStopsState.lat, curBusStopsState.lng)
//        )
//        pushMapEventUseCase(
//            MapEvent.AddMapMarkers(
//                listOf(
//                    MapMarker(
//                        "center",
//                        curBusStopsState.lat,
//                        curBusStopsState.lng,
//                        R.drawable.ic_location_marker_24_32,
//                        "center"
//                    )
//                )
//            )
//        )


        val busStopList = getBusStopsUseCase(
            lat = curBusStopsState.lat,
            lon = curBusStopsState.lng,
            limit = busStopsQueryLimitUseCase()
        )

        /*
        mapViewModelDelegate.pushMapEvent(
            mapStateId,
            MapEvent.MapCircle(
                curBusStopsState.lat,
                curBusStopsState.lng,
                measureDistanceMetres(
                    curBusStopsState.lat,
                    curBusStopsState.lng,
                    busStopList.last().latitude,
                    busStopList.last().longitude
                )
            )
        )
        */
//        pushMapEventUseCase(
//            MapEvent.AddMapMarkers(
//                busStopList.map { busStop ->
//                    mapMarkerIdBusStopMap[busStop.code] = busStop
//                    MapMarker(
//                        busStop.code,
//                        busStop.latitude,
//                        busStop.longitude,
//                        R.drawable.ic_marker_bus_stop_48,
//                        busStop.description
//                    )
//                }
//            )
//        )

        val listItems = getListItems(busStopList, onBusStopClicked)
        _listItems.postValue(listItems)
        _loading.postValue(Loading.Hide)
    }
     */

//    private suspend fun measureDistanceMetres(
//        lat1: Double, lon1: Double, lat2: Double, lon2: Double
//    ) = withContext(dispatcherProvider.computation) {
//        mapUtil.measureDistanceMetres(lat1, lon1, lat2, lon2)
//    }

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

    private fun onMapMarkerClicked(markerId: String) {
//        coroutineScope.launch(errorHandler) {
//            onBusStopClicked(getBusStopUseCase(markerId))
//        }
    }
}