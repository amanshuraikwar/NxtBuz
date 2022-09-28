package io.github.amanshuraikwar.nxtbuz.train.departures

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.github.amanshuraikwar.nxtbuz.common.util.NavigationUtil
import io.github.amanshuraikwar.nxtbuz.commonkmm.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.commonkmm.train.TrainDeparture
import io.github.amanshuraikwar.nxtbuz.domain.train.GetTrainStopDeparturesUseCase
import io.github.amanshuraikwar.nxtbuz.domain.train.GetTrainStopUseCase
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Named

private const val TAG = "TrainDeparturesVm"

class TrainDeparturesViewModel @Inject constructor(
    private val dispatcherProvider: CoroutinesDispatcherProvider,
    private val getTrainStopUseCase: GetTrainStopUseCase,
    private val getTrainStopDeparturesUseCase: GetTrainStopDeparturesUseCase,
    private val navigationUtil: NavigationUtil
) : ViewModel() {
    private val errorHandler = CoroutineExceptionHandler { _, th ->
        Log.e(TAG, "errorHandler: $th", th)
        _screenState.value = ScreenState.Error(
            message = "Something went wrong!",
            ableToReport = true,
            exception = th as Exception
        )
        FirebaseCrashlytics.getInstance().recordException(th)

    }
    private val coroutineContext = errorHandler + dispatcherProvider.computation

    private val _screenState = MutableStateFlow<ScreenState>(ScreenState.Fetching)
    internal val screenState = _screenState.asStateFlow()

    @Inject
    @Named("appVersionInfo")
    lateinit var appVersionInfo: String

    fun init(trainStopCode: String) {
        viewModelScope.launch(coroutineContext) {
            withContext(dispatcherProvider.main) {
                _screenState.emit(ScreenState.Fetching)
            }

            val trainStop = getTrainStopUseCase(trainStopCode = trainStopCode)
            if (trainStop == null) {
                _screenState.emit(
                    ScreenState.Error(
                        message = "Train stop not found!",
                        ableToReport = true,
                        exception = IllegalArgumentException(
                            "No train stop found for code $trainStopCode"
                        )
                    )
                )
                return@launch
            }

            val departures = getTrainStopDeparturesUseCase(trainStopCode = trainStopCode)
            val listItems = departures.map { it.toListItemData() }

            withContext(dispatcherProvider.main) {
                _screenState.emit(
                    ScreenState.Success(
                        header = TrainStopHeader(
                            code = trainStop.code,
                            codeToDisplay = trainStop.codeToDisplay,
                            hasFacilities = trainStop.hasFacilities,
                            hasDepartureTimes = trainStop.hasDepartureTimes,
                            hasTravelAssistance = trainStop.hasTravelAssistance,
                            name = trainStop.name,
                            lat = trainStop.lat,
                            lng = trainStop.lng,
                            starred = trainStop.starred,
                        ),
                        listItems = listItems
                    )
                )
            }
        }
    }

    private fun TrainDeparture.toListItemData(): ListItemData.Departure {
        return ListItemData.Departure(
            id = trainCode,
            destinationTrainStopName = destinationTrainStopName,
            track = track,
            trainCategoryName = trainCategoryName,
            departureStatus = departureStatus,
            plannedArrival = plannedArrivalInstant?.formatArrivalInstant(),
            actualArrival = actualArrivalInstant?.formatArrivalInstant(),
            plannedDeparture = plannedDepartureInstant.formatArrivalInstant(),
            actualDeparture = actualDepartureInstant?.formatArrivalInstant(),
            delayedByMinutes = delayedByMinutes,
        )
    }

    private fun Instant.formatArrivalInstant(): String {
        val datetimeInSystemZone = toLocalDateTime(TimeZone.currentSystemDefault())
        return DateTimeFormatter.ofPattern("hh:mm a")
            .format(datetimeInSystemZone.toJavaLocalDateTime())
    }

    fun onReportErrorClick(exception: Exception) {
        navigationUtil.goToEmail(
            address = "amanshuraikwar.dev@gmail.com",
            subject = "Next Bus SG Error",
            body = "Error occurred in $appVersionInfo.\n\nMessage = ${exception.message}"
        )
        FirebaseCrashlytics.getInstance().recordException(exception)
    }
}