package io.github.amanshuraikwar.howmuch.ui.main.overview

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import io.github.amanshuraikwar.howmuch.data.di.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.howmuch.data.model.BusStop
import io.github.amanshuraikwar.howmuch.domain.busstop.GetBusStopsUseCase
import io.github.amanshuraikwar.howmuch.util.asEvent
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "OverviewViewModel"

class OverviewViewModel @Inject constructor(
    private val getBusStopsUseCase: GetBusStopsUseCase,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) : ViewModel() {

    var colorControlNormalResId: Int = 0

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

    private val _busStops = MutableLiveData<List<BusStop>>()
    val busStops = _busStops.map { it }

    init {
        fetchData()
    }

    private fun fetchData() = viewModelScope.launch(dispatcherProvider.main + errorHandler) {
        _busStops.postValue(
            getBusStopsUseCase(latitude = 1.3483859, longitude = 103.7710103, limit = 200)
        )
    }
}

data class Alert(
    val msg: String
)