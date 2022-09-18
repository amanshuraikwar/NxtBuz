package io.github.amanshuraikwar.nxtbuz.train.details

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.github.amanshuraikwar.nxtbuz.commonkmm.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.domain.train.GetTrainDetailsUseCase
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

private const val TAG = "TrainDetailsViewModel"

class TrainDetailsViewModel @Inject constructor(
    private val dispatcherProvider: CoroutinesDispatcherProvider,
    private val getTrainDetailsUseCase: GetTrainDetailsUseCase,
) : ViewModel() {
    private val errorHandler = CoroutineExceptionHandler { _, th ->
        Log.e(TAG, "errorHandler: $th", th)
        FirebaseCrashlytics.getInstance().recordException(th)
    }
    private val coroutineContext = errorHandler + dispatcherProvider.computation

    private val _screenState = MutableStateFlow<ScreenState>(ScreenState.Fetching)
    internal val screenState = _screenState.asStateFlow()

    fun init(trainCode: String) {
        viewModelScope.launch(coroutineContext) {
            withContext(dispatcherProvider.main) {
                _screenState.emit(ScreenState.Fetching)
            }

            val trainDetails = getTrainDetailsUseCase(trainCode = trainCode)!!

            var rollingStockInfoDisplayed = false

            val rollingStockImages = trainDetails
                .rollingStock
                .mapNotNull { trainRollingStock ->
                    val imageUrl = trainRollingStock.imageUrl
                    if (imageUrl != null) {
                        var displayInfo = false
                        // assuming if facilities is empty then its an engine
                        if (!rollingStockInfoDisplayed &&
                            trainRollingStock.facilities.isNotEmpty()
                        ) {
                            // show it at first carriage with facilities
                            displayInfo = true
                            rollingStockInfoDisplayed = true
                        }

                        RollingStockImage(
                            imageUrl = imageUrl,
                            width = trainRollingStock.width,
                            height = trainRollingStock.height,
                            displayInfo = displayInfo
                        )
                    } else {
                        null
                    }
                }


            withContext(dispatcherProvider.main) {
                _screenState.emit(
                    ScreenState.Success(
                        header = TrainHeader(
                            trainCode = trainDetails.trainCode,
                            trainCategoryName = trainDetails.trainCategoryName,
                            sourceTrainStopName = trainDetails.sourceTrainStopName,
                            destinationTrainStopName = trainDetails.destinationTrainStopName,
                            facilities = trainDetails.facilities,
                            rollingStockImages = rollingStockImages,
                            length = trainDetails.length,
                            lengthInMeters = trainDetails.lengthInMeters,
                        ),
                        listItems = emptyList()
                    )
                )
            }
        }
    }

    fun onTrainClick(trainCode: String) {
        viewModelScope.launch(coroutineContext) {
            Log.i(TAG, "onTrainClick: ${getTrainDetailsUseCase(trainCode)}")
        }
    }

    /*
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
            actualDeparture = actualDepartureInstant.formatArrivalInstant(),
            delayedByMinutes = delayedByMinutes,
        )
    }
     */

    private fun Instant.formatArrivalInstant(): String {
        val datetimeInSystemZone = toLocalDateTime(TimeZone.currentSystemDefault())
        return DateTimeFormatter.ofPattern("hh:mm a")
            .format(datetimeInSystemZone.toJavaLocalDateTime())
    }
}