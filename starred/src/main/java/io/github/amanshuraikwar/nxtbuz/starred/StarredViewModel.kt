package io.github.amanshuraikwar.nxtbuz.starred

import android.util.Log
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.github.amanshuraikwar.nxtbuz.commonkmm.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.commonkmm.starred.StarredBusArrival
import io.github.amanshuraikwar.nxtbuz.domain.busarrival.GetBusArrivalsUseCase
import io.github.amanshuraikwar.nxtbuz.domain.busstop.GetBusStopUseCase
import io.github.amanshuraikwar.nxtbuz.domain.starred.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

private const val TAG = "StarredBusArrivalsVM"

class StarredViewModel @Inject constructor(
    private val getStarredBusServicesUseCase: GetStarredBusServicesUseCase,
    private val getBusArrivalsUseCase: GetBusArrivalsUseCase,
    private val getBusStopUseCase: GetBusStopUseCase,
    private val toggleStar: ToggleBusStopStarUseCase,
    private val toggleStarUpdateUseCase: ToggleStarUpdateUseCase,
    private val showErrorStarredBusArrivalsUseCase: ShowErrorStarredBusArrivalsUseCase,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) : ViewModel() {

    private val errorHandler = CoroutineExceptionHandler { _, th ->
        Log.e(TAG, "errorHandler: $th", th)
        FirebaseCrashlytics.getInstance().recordException(th)
    }
    private val coroutineContext = errorHandler + dispatcherProvider.computation

    private val busArrivalListLock = Mutex()
    var listItemsFlow = MutableStateFlow(SnapshotStateList<StarredBusArrivalData>())
    private var loop: StarredBusArrivalsLoop? = null
    private var listenStarUpdatesJob: Job? = null
    private var listenShowErrorStarredBusArrivalsUpdateJob: Job? = null

    fun start() {
        viewModelScope.launch(coroutineContext) {
            busArrivalListLock.withLock {
                listItemsFlow.value = SnapshotStateList()
            }
            startListeningArrivals()
            listenToggleStarUpdate()
            listenShowErrorStarredBusArrivalsUpdate()
        }
    }

    @Synchronized
    private fun listenToggleStarUpdate() {
        listenStarUpdatesJob?.cancel()
        listenStarUpdatesJob = null
        listenStarUpdatesJob = viewModelScope.launch(coroutineContext) {
            toggleStarUpdateUseCase()
                .collect {
                    loop?.emitNow()
                }
        }
    }

    @Synchronized
    private fun listenShowErrorStarredBusArrivalsUpdate() {
        listenShowErrorStarredBusArrivalsUpdateJob?.cancel()
        listenShowErrorStarredBusArrivalsUpdateJob = null
        listenShowErrorStarredBusArrivalsUpdateJob = viewModelScope.launch(coroutineContext) {
            showErrorStarredBusArrivalsUseCase.updates()
                .collect {
                    loop?.emitNow()
                }
        }
    }

    @Synchronized
    private fun startListeningArrivals() {
        loop?.stop()
        loop = null
        loop = StarredBusArrivalsLoop(
            getStarredBusServicesUseCase = getStarredBusServicesUseCase,
            getBusArrivalsUseCase = getBusArrivalsUseCase,
            getBusStopUseCase = getBusStopUseCase,
            showErrorStarredBusArrivalsUseCase = showErrorStarredBusArrivalsUseCase,
            coroutineScope = viewModelScope,
            dispatcher = dispatcherProvider.pool8,
        )
        loop?.startAndCollect(coroutineContext = coroutineContext) { starredBusArrivalList ->
            handleStarredBusArrivalList(starredBusArrivalList)
        }
    }

    private suspend fun handleStarredBusArrivalList(
        starredBusArrivalList: List<StarredBusArrival>
    ) {
        withContext(coroutineContext) {
            if (!busArrivalListLock.tryLock()) return@withContext

            val listItems = listItemsFlow.value

            val starredHit = mutableSetOf<String>()

            starredBusArrivalList.forEach { starredBusArrival ->
                starredHit.add(
                    "${starredBusArrival.busServiceNumber}-${starredBusArrival.busStopCode}"

                )
                val listItemIndex = listItems.indexOfFirst {
                    it.busServiceNumber == starredBusArrival.busServiceNumber
                            && it.busStopCode == starredBusArrival.busStopCode
                }

                if (listItemIndex == -1) {
                    listItems.add(
                        StarredBusArrivalData(
                            busServiceNumber = starredBusArrival.busServiceNumber,
                            busStopDescription = starredBusArrival.busStop.description,
                            busArrivals = starredBusArrival.busArrivals,
                            busStopCode = starredBusArrival.busStopCode,
                        )
                    )
                } else {
                    listItems[listItemIndex] = listItems[listItemIndex].copy(
                        busArrivals = starredBusArrival.busArrivals
                    )
                }
            }

            listItems.removeAll { data ->
                !starredHit.contains("${data.busServiceNumber}-${data.busStopCode}")
            }

            if (listItems.isEmpty()) {
                listItemsFlow.value = SnapshotStateList()
            }

            busArrivalListLock.unlock()
        }
    }

    fun onUnStarClicked(busServiceNumber: String, busStopCode: String) {
        viewModelScope.launch(coroutineContext) {
            if (!busArrivalListLock.tryLock()) return@launch

            val listItems = listItemsFlow.value

            listItems.removeAll { data ->
                data.busStopCode == busStopCode && data.busServiceNumber == busServiceNumber
            }

            toggleStar(
                busServiceNumber = busServiceNumber,
                busStopCode = busStopCode,
                toggleTo = false
            )

            if (listItems.isEmpty()) {
                listItemsFlow.value = SnapshotStateList()
            }

            busArrivalListLock.unlock()
        }
    }

    fun onDispose() {
        loop?.stop()
        loop = null
        listenStarUpdatesJob?.cancel()
        listenStarUpdatesJob = null
        listenShowErrorStarredBusArrivalsUpdateJob?.cancel()
        listenShowErrorStarredBusArrivalsUpdateJob = null
    }
}