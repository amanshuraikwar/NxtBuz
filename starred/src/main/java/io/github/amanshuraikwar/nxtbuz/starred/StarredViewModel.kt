package io.github.amanshuraikwar.nxtbuz.starred

import android.util.Log
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.github.amanshuraikwar.nxtbuz.common.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.common.model.StarredBusArrival
import io.github.amanshuraikwar.nxtbuz.domain.busstop.GetBusStopUseCase
import io.github.amanshuraikwar.nxtbuz.domain.starred.AttachStarredBusArrivalsUseCase
import io.github.amanshuraikwar.nxtbuz.domain.starred.ToggleBusStopStarUseCase
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

private const val TAG = "StarredBusArrivalsVM"

class StarredViewModel @Inject constructor(
    private val attachStarredBusArrivalsUseCase: AttachStarredBusArrivalsUseCase,
    private val getBusStopUseCase: GetBusStopUseCase,
    private val toggleStar: ToggleBusStopStarUseCase,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) : ViewModel() {

    private val errorHandler = CoroutineExceptionHandler { _, th ->
        Log.e(TAG, "errorHandler: $th", th)
        FirebaseCrashlytics.getInstance().recordException(th)
    }

    private val coroutineContext = errorHandler + dispatcherProvider.computation

    private val busArrivalListLock = Mutex()

    var listItemsFlow = MutableStateFlow(SnapshotStateList<StarredBusArrivalData>())

    fun start() {
        viewModelScope.launch(coroutineContext) {
            busArrivalListLock.withLock {
                listItemsFlow.value = SnapshotStateList()
            }

            attachStarredBusArrivalsUseCase(
                "main-fragment-view-model",
                considerFilteringError = false
            )
                .catch { throwable ->
                    FirebaseCrashlytics.getInstance().recordException(throwable)
                }.collect {
                    Log.d(TAG, "collect: lock = ${busArrivalListLock.isLocked} data = $it")
                    handleStarredBusArrivalList(it)
                }

        }
    }

    private suspend fun handleStarredBusArrivalList(starredBusArrivalList: List<StarredBusArrival>) {
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
                            busStopDescription = starredBusArrival.busStopDescription,
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
}