package io.github.amanshuraikwar.nxtbuz.busstop.busstops

import android.util.Log
import androidx.annotation.WorkerThread
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.github.amanshuraikwar.nxtbuz.busstop.busstops.model.BusStopsItemData
import io.github.amanshuraikwar.nxtbuz.busstop.busstops.model.BusStopsScreenState
import io.github.amanshuraikwar.nxtbuz.common.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.common.model.BusStop
import io.github.amanshuraikwar.nxtbuz.domain.busstop.BusStopsQueryLimitUseCase
import io.github.amanshuraikwar.nxtbuz.domain.busstop.GetBusStopsUseCase
import io.github.amanshuraikwar.nxtbuz.domain.location.GetLocationUpdatesUseCase
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

private const val TAG = "BusStopsViewModel"

class BusStopsViewModel @Inject constructor(
    private val getLocationUpdatesUseCase: GetLocationUpdatesUseCase,
    private val getBusStopsUseCase: GetBusStopsUseCase,
    private val busStopsQueryLimitUseCase: BusStopsQueryLimitUseCase,
    dispatcherProvider: CoroutinesDispatcherProvider
) : ViewModel() {
    private val errorHandler = CoroutineExceptionHandler { _, th ->
        Log.e(TAG, "errorHandler: $th", th)
        FirebaseCrashlytics.getInstance().recordException(th)
        failed()
    }
    private val coroutineContext = errorHandler + dispatcherProvider.computation

    private val listItems = SnapshotStateList<BusStopsItemData>()
    private val listItemsLock = Mutex()

    private val _screenState = MutableStateFlow<BusStopsScreenState>(BusStopsScreenState.Fetching)
    val screenState: StateFlow<BusStopsScreenState> = _screenState

//    init {
//        fetchBusStops()
//    }

    fun fetchBusStops() {
        viewModelScope.launch(coroutineContext) {
            if (listItems.isNotEmpty()) return@launch
            _screenState.emit(BusStopsScreenState.Fetching)
            val location = getLocationUpdatesUseCase().first()
            val busStopList = getBusStopsUseCase(
                lat = location.lat,
                lon = location.lng,
                limit = busStopsQueryLimitUseCase()
            )
            listItemsLock.withLock {
                updateListItems(busStopList)
            }
            _screenState.emit(BusStopsScreenState.Success(listItems))
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

    private fun failed() {
        viewModelScope.launch(coroutineContext) {
            _screenState.emit(BusStopsScreenState.Failed)
        }
    }
}