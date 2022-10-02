package io.github.amanshuraikwar.nxtbuz.train.details

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.github.amanshuraikwar.nxtbuz.common.util.NavigationUtil
import io.github.amanshuraikwar.nxtbuz.commonkmm.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.commonkmm.train.TrainRouteNode
import io.github.amanshuraikwar.nxtbuz.commonkmm.train.TrainRouteNodeType
import io.github.amanshuraikwar.nxtbuz.domain.train.GetTrainDetailsUseCase
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named

private const val TAG = "TrainDetailsViewModel"

class TrainDetailsViewModel @Inject constructor(
    private val dispatcherProvider: CoroutinesDispatcherProvider,
    private val getTrainDetailsUseCase: GetTrainDetailsUseCase,
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
                        listItems = trainDetails.route.mapNotNull { trainRouteNode ->
                            trainRouteNode.toListItemData()
                        }
                    )
                )
            }
        }
    }

    private fun TrainRouteNode.toListItemData(): ListItemData? {
        return when (val type = type) {
            is TrainRouteNodeType.Destination -> {
                ListItemData.RouteNodeDestination(
                    trainStopCode = trainStopCode,
                    trainStopName = trainStopName,
                    crowdStatus = crowdStatus,
                    type = type
                )
            }

            is TrainRouteNodeType.Origin -> {
                ListItemData.RouteNodeOrigin(
                    trainStopCode = trainStopCode,
                    trainStopName = trainStopName,
                    crowdStatus = crowdStatus,
                    type = type
                )
            }

            TrainRouteNodeType.Passing -> null
            is TrainRouteNodeType.Stop -> {
                ListItemData.RouteNodeMiddle(
                    trainStopCode = trainStopCode,
                    trainStopName = trainStopName,
                    crowdStatus = crowdStatus,
                    type = type
                )
            }
        }
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