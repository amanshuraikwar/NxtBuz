package io.github.amanshuraikwar.howmuch.ui.search

import android.util.Log
import androidx.annotation.DrawableRes
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.MarkerOptions
import io.github.amanshuraikwar.howmuch.R
import io.github.amanshuraikwar.howmuch.data.di.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.howmuch.data.model.BusStop
import io.github.amanshuraikwar.howmuch.domain.busstop.*
import io.github.amanshuraikwar.howmuch.ui.list.BusStopItem
import io.github.amanshuraikwar.howmuch.util.asEvent
import io.github.amanshuraikwar.multiitemadapter.RecyclerViewListItem
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val TAG = "SearchViewModel"

class SearchViewModel @Inject constructor(
    private val getBusStopsUseCase: GetBusStopsUseCase,
    private val getBusStopsLimitUseCase: GetBusStopsLimitUseCase,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) : ViewModel() {

    private val _error = MutableLiveData<Alert>()
    val error = _error
        .map {
            Log.e(TAG, "onError: $it")
            it
        }
        .asEvent()

    private val errorHandler = CoroutineExceptionHandler { _, th ->
        Log.e(TAG, "errorHandler: $th", th)
        _error.postValue(Alert())
    }

    private val _busStops = MutableLiveData<MutableList<RecyclerViewListItem>>()
    val busStops = _busStops.map { it }

    private val _busStopClicked = MutableLiveData<BusStop>()
    val busStopClicked = _busStopClicked.asEvent()

    private val _loading = MutableLiveData<Boolean>()
    val loading = _loading.asEvent()

    fun searchBusStops(query: String) =
        viewModelScope.launch(dispatcherProvider.io + errorHandler) {
            if (query.isNotEmpty()) {
                _loading.postValue(true)
                val busStopList = getBusStopsUseCase(query, getBusStopsLimitUseCase())
                if (busStopList.isEmpty()) {
                    _error.postValue(Alert("No matching bus stops found.", R.drawable.ic_no_bus_stops_found_128))
                    // TODO: 5/4/20
                } else {
                    _busStops.postValue(getListItems(busStopList))
                    _loading.postValue(false)
                }
            }
        }

    private suspend fun getListItems(busStopList: List<BusStop>): MutableList<RecyclerViewListItem> =
        withContext(dispatcherProvider.computation) {
            val listItems = mutableListOf<RecyclerViewListItem>()
            //listItems.add(HeaderItem("Nearby Bus Stops"))
            busStopList.forEach {
                listItems.add(
                    BusStopItem(
                        it,
                        R.drawable.ic_bus_stop_128,
                        ::onBusStopClicked,
                        ::onGotoClicked
                    )
                )
            }
            listItems
        }

    private fun onBusStopClicked(busStop: BusStop) {
        _busStopClicked.postValue(busStop)
    }

    private fun onGotoClicked(busStop: BusStop) {
        // do nothing
    }
}

data class Alert(
    val msg: String = "Something went wrong.",
    @DrawableRes val iconResId: Int = R.drawable.ic_round_error_48
)