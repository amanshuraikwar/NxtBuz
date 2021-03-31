package io.github.amanshuraikwar.nxtbuz.search.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.github.amanshuraikwar.nxtbuz.common.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.domain.search.SearchUseCase
import io.github.amanshuraikwar.nxtbuz.search.ui.model.SearchResult
import io.github.amanshuraikwar.nxtbuz.search.ui.model.SearchScreenState
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "SearchViewModel"

class SearchViewModel @Inject constructor(
    private val searchUseCase: SearchUseCase,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) : ViewModel() {

    private val errorHandler = CoroutineExceptionHandler { _, th ->
        Log.e(TAG, "errorHandler: $th", th)
        FirebaseCrashlytics.getInstance().recordException(th)
    }

//    private val _busStops = MutableLiveData<MutableList<RecyclerViewListItem>>()
//    val busStops = _busStops.map { it }
//
//    private val _busStopClicked = MutableLiveData<BusStop>()
//    val busStopClicked = _busStopClicked.asEvent()

//    private val _busServiceClicked = MutableLiveData<BusService>()
//    val busServiceClicked = _busServiceClicked.asEvent()


//    private val _loading = MutableLiveData<Boolean>()
//    val loading = _loading.asEvent()

    internal val screenState = MutableSharedFlow<SearchScreenState>()

    init {
        FirebaseCrashlytics.getInstance().setCustomKey(
            "viewModel",
            TAG
        )
    }

    fun searchBusStops(query: String) {
        viewModelScope.launch(dispatcherProvider.io + errorHandler) {
            if (query.isNotEmpty()) {
                val searchResult = searchUseCase(query, 20)
                if (searchResult.busStopList.isEmpty()) {
                    screenState.emit(
                        SearchScreenState.Failed(
                            "No matching bus stops found."
                        )
                    )
                } else {
                    screenState.emit(
                        SearchScreenState.Success(
                            searchResult.busStopList.map { busStop ->
                                SearchResult.BusStopResult(
                                    busStopDescription = busStop.description,
                                    busStopInfo = "${busStop.roadName} • ${busStop.code}",
                                    operatingBuses = busStop.operatingBusList
                                        .map { it.serviceNumber }
                                        .reduceRight { next, total -> "${if (total.length == 2) "$total  " else if (total.length == 3) "$total " else total}  ${if (next.length == 2) "$next  " else if (next.length == 3) "$next " else next}" },
                                    busStop = busStop
                                )
                            }
                        )
                    )
                }
            }
        }
    }

    fun clear() {
        viewModelScope.launch {
            screenState.emit(SearchScreenState.Nothing)
        }
    }
}