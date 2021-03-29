package io.github.amanshuraikwar.nxtbuz.search

import android.util.Log
import androidx.annotation.DrawableRes
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.github.amanshuraikwar.multiitemadapter.RecyclerViewListItem
import io.github.amanshuraikwar.nxtbuz.common.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.common.model.BusService
import io.github.amanshuraikwar.nxtbuz.common.model.BusStop
import io.github.amanshuraikwar.nxtbuz.common.util.asEvent
import io.github.amanshuraikwar.nxtbuz.domain.search.SearchUseCase
import io.github.amanshuraikwar.nxtbuz.listitem.HeaderItem
import io.github.amanshuraikwar.nxtbuz.listitem.SearchBusServiceItem
import io.github.amanshuraikwar.nxtbuz.listitem.SearchBusStopItem
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val TAG = "SearchViewModel"

//class SearchViewModel @Inject constructor(
//    private val searchUseCase: SearchUseCase,
//    private val dispatcherProvider: CoroutinesDispatcherProvider
//) : ViewModel() {
//
//    private val _error = MutableLiveData<Alert>()
//    val error = _error
//        .map {
//            Log.e(TAG, "onError: $it")
//            it
//        }
//        .asEvent()
//
//    private val errorHandler = CoroutineExceptionHandler { _, th ->
//        Log.e(TAG, "errorHandler: $th", th)
//        FirebaseCrashlytics.getInstance().recordException(th)
//        _error.postValue(Alert())
//    }
//
//    private val _busStops = MutableLiveData<MutableList<RecyclerViewListItem>>()
//    val busStops = _busStops.map { it }
//
//    private val _busStopClicked = MutableLiveData<BusStop>()
//    val busStopClicked = _busStopClicked.asEvent()
//
//    private val _busServiceClicked = MutableLiveData<BusService>()
//    val busServiceClicked = _busServiceClicked.asEvent()
//
//
//    private val _loading = MutableLiveData<Boolean>()
//    val loading = _loading.asEvent()
//
//    init {
//        FirebaseCrashlytics.getInstance().setCustomKey("viewModel",
//            TAG
//        )
//    }
//
//    fun searchBusStops(query: String) =
//        viewModelScope.launch(dispatcherProvider.io + errorHandler) {
//            if (query.isNotEmpty()) {
//                _loading.postValue(true)
//                val searchResult = searchUseCase(query, 5)
//                if (searchResult.busServiceList.isEmpty() && searchResult.busStopList.isEmpty()) {
//                    _error.postValue(
//                        Alert(
//                            "No matching bus stops and bus services found."
//                        )
//                    )
//                } else {
//
//                    val listItems = mutableListOf<RecyclerViewListItem>()
//
//                    if (searchResult.busServiceList.isNotEmpty()) {
//                        listItems.add(HeaderItem("Bus Services"))
//                        listItems.addAll(
//                            searchResult.busServiceList.map {
//                                SearchBusServiceItem(it, ::onBusServiceClicked)
//                            }
//                        )
//                    }
//
//                    if (searchResult.busStopList.isNotEmpty()) {
//                        listItems.add(HeaderItem("Bus Stops"))
//                        listItems.addAll(
//                            getListItems(searchResult.busStopList)
//                        )
//                    }
//
//                    _busStops.postValue(listItems)
//                    _loading.postValue(false)
//                }
//            }
//        }
//
//    private suspend fun getListItems(busStopList: List<BusStop>): MutableList<RecyclerViewListItem> =
//        withContext(dispatcherProvider.computation) {
//            val listItems = mutableListOf<RecyclerViewListItem>()
//            //listItems.add(HeaderItem("Nearby Bus Stops"))
//            busStopList.forEach {
//                listItems.add(
//                    SearchBusStopItem(
//                        it,
//                        R.drawable.ic_bus_stop_24,
//                        ::onBusStopClicked,
//                        ::onGotoClicked
//                    )
//                )
//            }
//            listItems
//        }
//
//    private fun onBusStopClicked(busStop: BusStop) {
//        _busStopClicked.postValue(busStop)
//    }
//
//    private fun onBusServiceClicked(busService: BusService) {
//        _busServiceClicked.postValue(busService)
//    }
//
//    private fun onGotoClicked(busStop: BusStop) {
//        // do nothing
//    }
//}
//
//data class Alert(
//    val msg: String = "Something went wrong.",
//    @DrawableRes val iconResId: Int = R.drawable.ic_error_128
//)