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
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

private const val TAG = "StarredBusArrivalsVM"

class StarredViewModel @Inject constructor(
    private val attachStarredBusArrivalsUseCase: AttachStarredBusArrivalsUseCase,
    private val getBusStopUseCase: GetBusStopUseCase,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) : ViewModel() {

    private val errorHandler = CoroutineExceptionHandler { _, th ->
        Log.e(TAG, "errorHandler: $th", th)
        FirebaseCrashlytics.getInstance().recordException(th)
    }

    private val coroutineContext = errorHandler + dispatcherProvider.computation

    private val busArrivalListLock = Mutex()

    var listItems = SnapshotStateList<StarredBusArrivalData>()

    fun start() {
        viewModelScope.launch(coroutineContext) {
            busArrivalListLock.withLock {
                listItems = SnapshotStateList()
            }

            attachStarredBusArrivalsUseCase(
                "main-fragment-view-model",
                considerFilteringError = false
            )
                .catch { throwable ->
                    FirebaseCrashlytics.getInstance().recordException(throwable)
                }.collect {
                    handleStarredBusArrivalList(it)
                }

        }
    }

    private suspend fun handleStarredBusArrivalList(starredBusArrivalList: List<StarredBusArrival>) {
        withContext(dispatcherProvider.computation) {
            if (!busArrivalListLock.tryLock()) return@withContext

            val starredHit = mutableSetOf<String>()

            starredBusArrivalList.forEach { starredBusArrival ->
                starredHit.add(
                    "${starredBusArrival.busServiceNumber}-${starredBusArrival.busStopCode}"

                )
                val listItemIndex = listItems.indexOfFirst {
                    it.busServiceNumber == starredBusArrival.busServiceNumber
                }

                if (listItemIndex == -1) {
                    listItems.add(
                        StarredBusArrivalData(
                            busServiceNumber = starredBusArrival.busServiceNumber,
                            busStopDescription = starredBusArrival.busStopDescription,
                            arrivals = starredBusArrival.arrivals,
                            busStopCode = starredBusArrival.busStopCode,
                        )
                    )
                } else {
                    listItems[listItemIndex] = listItems[listItemIndex].copy(
                        arrivals = starredBusArrival.arrivals
                    )
                }
            }

            listItems.removeAll { data ->
                    !starredHit.contains("${data.busServiceNumber}-${data.busStopCode}")
            }

            if (listItems.isEmpty()) {
                listItems = SnapshotStateList()
            }

            busArrivalListLock.unlock()
        }
    }

    var markForCleanup = false
}