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
import io.github.amanshuraikwar.howmuch.util.asEvent
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "BusStopViewModel"

class BusStopViewModel @Inject constructor(
    private val getArrivalsUseCase: GetArrivalsUseCase,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) : ViewModel() {

    var busStop: BusStop? = null

    private val _error = MutableLiveData<Exception>()
    val error = _error
        .map {
            Log.e(TAG, "onError: ", it)
            it.message ?: "Something went wrong."
        }
        .asEvent()

    private val errorHandler = CoroutineExceptionHandler { _, error ->
        _error.postValue(error as Exception)
    }

    private val _arrivals = MutableLiveData<List<BusArrival>>()
    val arrivals = _arrivals.map {
        it
    }

    init {
        start()
    }

    private fun start() = viewModelScope.launch(dispatcherProvider.computation + errorHandler) {
        while (isActive) {
            _arrivals.postValue(getArrivalsUseCase(busStop?.code ?: continue))
            delay(10000)
        }
    }
}