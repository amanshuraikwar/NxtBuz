package io.github.amanshuraikwar.howmuch.ui.busstop

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import io.github.amanshuraikwar.howmuch.data.di.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.howmuch.data.model.BusArrival
import io.github.amanshuraikwar.howmuch.data.model.BusStop
import io.github.amanshuraikwar.howmuch.domain.busstop.GetArrivalsUseCase
import io.github.amanshuraikwar.howmuch.util.TimeUtil
import io.github.amanshuraikwar.howmuch.util.asEvent
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.format.DateTimeFormatter
import javax.inject.Inject

private const val TAG = "BusStopViewModel"
private const val REFRESH_DELAY = 10000L

class BusStopViewModel @Inject constructor(
    private val getArrivalsUseCase: GetArrivalsUseCase,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) : ViewModel() {

    internal var busStop: BusStop? = null
    private var lastUpdatedOn: String? = null

    private val _error = MutableLiveData<Exception>()
    val error = _error
        .map {
            Log.e(TAG, "onError: ", it)
            lastUpdatedOn?.let { "Last updated on $it." } ?: "Couldn't fetch arrivals."
        }
        .asEvent()

    private val errorHandler = CoroutineExceptionHandler { _, error ->
        _error.postValue(error as Exception)
        if (lastUpdatedOn != null) {
            startDelayed()
        }
    }

    private val _arrivals = MutableLiveData<List<BusArrival>>()
    val arrivals = _arrivals.map {
        it
    }

    init {
        start()
    }

    private fun startDelayed() {
        start(REFRESH_DELAY)
    }

    internal fun start(initialDelay: Long = 0) = viewModelScope.launch(dispatcherProvider.computation + errorHandler) {
        delay(initialDelay)
        while (isActive) {
            val busArrivalList = getArrivalsUseCase(busStop?.code ?: return@launch)
            lastUpdatedOn = OffsetDateTime.now().format(TimeUtil.TIME_READABLE_FORMATTER)
            _arrivals.postValue(busArrivalList)
            delay(REFRESH_DELAY)
        }
    }
}