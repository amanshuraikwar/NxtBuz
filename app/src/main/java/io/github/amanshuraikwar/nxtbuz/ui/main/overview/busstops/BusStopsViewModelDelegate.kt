package io.github.amanshuraikwar.nxtbuz.ui.main.overview.busstops

import androidx.lifecycle.MutableLiveData
import io.github.amanshuraikwar.multiitemadapter.RecyclerViewListItem
import io.github.amanshuraikwar.nxtbuz.R
import io.github.amanshuraikwar.nxtbuz.data.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.data.busstop.model.BusStop
import io.github.amanshuraikwar.nxtbuz.domain.busstop.BusStopsQueryLimitUseCase
import io.github.amanshuraikwar.nxtbuz.domain.busstop.GetBusStopsUseCase
import io.github.amanshuraikwar.nxtbuz.ui.list.BusStopItem
import io.github.amanshuraikwar.nxtbuz.ui.main.overview.model.MapEvent
import io.github.amanshuraikwar.nxtbuz.ui.main.overview.model.MapMarker
import io.github.amanshuraikwar.nxtbuz.ui.main.overview.Loading
import io.github.amanshuraikwar.nxtbuz.ui.main.overview.ScreenState
import io.github.amanshuraikwar.nxtbuz.ui.main.overview.map.MapViewModelDelegate
import io.github.amanshuraikwar.nxtbuz.util.MapUtil
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named

class BusStopsViewModelDelegate @Inject constructor(
    private val getBusStopsUseCase: GetBusStopsUseCase,
    private val busStopsQueryLimitUseCase: BusStopsQueryLimitUseCase,
    @Named("listItems") private val _listItems: MutableLiveData<List<RecyclerViewListItem>>,
    @Named("loading") private val _loading: MutableLiveData<Loading>,
    private val mapViewModelDelegate: MapViewModelDelegate,
    private val mapUtil: MapUtil,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) {

    private lateinit var curBusStopsState: ScreenState.BusStopsState

    suspend fun start(
        busStopsState: ScreenState.BusStopsState,
        onBusStopClicked: (BusStop) -> Unit
    ) = withContext(dispatcherProvider.io) {
        curBusStopsState = busStopsState
        mapViewModelDelegate.pushMapEvent(
            MapEvent.MoveCenter(curBusStopsState.lat, curBusStopsState.lng)
        )
        mapViewModelDelegate.pushMapEvent(
                MapEvent.AddMapMarkers(
                    listOf(
                        MapMarker(
                            "center",
                            curBusStopsState.lat,
                            curBusStopsState.lng,
                            R.drawable.ic_location_marker_24_32,
                            "center"
                        )
                    )
                )
        )
        val busStopList = getBusStopsUseCase(
            lat = curBusStopsState.lat,
            lon = curBusStopsState.lng,
            limit = busStopsQueryLimitUseCase()
        )
        mapViewModelDelegate.pushMapEvent(
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
        mapViewModelDelegate.pushMapEvent(
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
        val listItems = getListItems(busStopList, onBusStopClicked)
        _listItems.postValue(listItems)
        _loading.postValue(Loading.Hide)
    }

    private suspend fun measureDistanceMetres(
        lat1: Double, lon1: Double, lat2: Double, lon2: Double
    ) = withContext(dispatcherProvider.computation) {
        mapUtil.measureDistanceMetres(lat1, lon1, lat2, lon2)
    }

    private suspend fun getListItems(
        busStopList: List<BusStop>,
        onBusStopClicked: (BusStop) -> Unit
    ): MutableList<RecyclerViewListItem> =
        withContext(dispatcherProvider.computation) {
            val listItems = mutableListOf<RecyclerViewListItem>()
            busStopList.forEach {
                listItems.add(
                    BusStopItem(
                        it,
                        R.drawable.ic_bus_stop_24,
                        onBusStopClicked,
                        // todo remove this variable
                        ::onGotoClicked
                    )
                )
            }
            listItems
        }

    // todo remove this
    private fun onGotoClicked(busStop: BusStop) {
    }
}