package io.github.amanshuraikwar.nxtbuz.ui.starred.options

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.github.amanshuraikwar.nxtbuz.common.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.common.model.BusStop
import io.github.amanshuraikwar.nxtbuz.domain.starred.ToggleBusStopStarUseCase
import io.github.amanshuraikwar.nxtbuz.util.asEvent
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

class StarredBusArrivalOptionsViewModel @Inject constructor(
    private val toggleBusStopStar: ToggleBusStopStarUseCase,
    @Named("starred-bus-arrival-removed") private val _remove: MutableLiveData<Pair<BusStop, String>>,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) : ViewModel() {

    private val errorHandler = CoroutineExceptionHandler { _, th ->
        Log.e(TAG, "errorHandler: $th", th)
        FirebaseCrashlytics.getInstance().recordException(th)
        _error.postValue(th)
    }

    private val _error = MutableLiveData<Throwable>()
    val error =
        _error
            .map {
                "Something went wrong. Please try again."
            }
            .asEvent()

    fun onUnStarClicked(busStop: BusStop, busServiceNumber: String) {
        _remove.postValue(busStop to busServiceNumber)
        viewModelScope.launch(dispatcherProvider.io + errorHandler) {
            toggleBusStopStar(busStop.code, busServiceNumber, false)
        }
    }

    companion object {
        private const val TAG = "StarredBusArrivalOptsVm"
    }
}