package io.github.amanshuraikwar.nxtbuz.settings.ui

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.github.amanshuraikwar.multiitemadapter.RecyclerViewListItem
import io.github.amanshuraikwar.nxtbuz.common.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.common.model.AlertFrequency
import io.github.amanshuraikwar.nxtbuz.domain.busstop.BusStopsQueryLimitUseCase
import io.github.amanshuraikwar.nxtbuz.domain.busstop.MaxDistanceOfClosesBusStopUseCase
import io.github.amanshuraikwar.nxtbuz.domain.starred.AlertStarredBusArrivalsFrequency
import io.github.amanshuraikwar.nxtbuz.domain.starred.AlertStarredBusArrivalsMinutes
import io.github.amanshuraikwar.nxtbuz.domain.starred.ShouldAlertStarredBusArrivals
import io.github.amanshuraikwar.nxtbuz.domain.starred.ShowErrorStarredBusArrivalsUseCase
import io.github.amanshuraikwar.nxtbuz.listitem.BooleanSettingsItem
import io.github.amanshuraikwar.nxtbuz.listitem.SettingsHeadingItem
import io.github.amanshuraikwar.nxtbuz.listitem.SettingsItem
import io.github.amanshuraikwar.nxtbuz.listitem.VersionItem
import io.github.amanshuraikwar.nxtbuz.settings.ui.dialog.DialogViewModelDelegate
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.IllegalArgumentException
import javax.inject.Inject
import javax.inject.Named

private const val TAG = "SettingsViewModel"

@ExperimentalCoroutinesApi
class SettingsViewModel @Inject constructor(
    private val busStopsQueryLimitUseCase: BusStopsQueryLimitUseCase,
    private val maxDistanceOfClosesBusStopUseCase: MaxDistanceOfClosesBusStopUseCase,
    private val showErrorStarredBusArrivalsUseCase: ShowErrorStarredBusArrivalsUseCase,
    private val shouldAlertStarredBusArrivals: ShouldAlertStarredBusArrivals,
    private val alertStarredBusArrivalsMinutes: AlertStarredBusArrivalsMinutes,
    private val alertStarredBusArrivalsFrequency: AlertStarredBusArrivalsFrequency,
    private val dialogViewModelDelegate: DialogViewModelDelegate,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) : ViewModel() {

    @field:[Inject Named("appVersionInfo")]
    lateinit var appVersionInfo: String

    private val errorHandler = CoroutineExceptionHandler { _, th ->
        Log.e(TAG, "errorHandler: $th", th)
        FirebaseCrashlytics.getInstance().recordException(th)
    }

    private val _listItems = MutableLiveData<MutableList<RecyclerViewListItem>>()
    val listItems = _listItems.map { it }

    init {
        FirebaseCrashlytics.getInstance().setCustomKey("viewModel",
            TAG
        )
        fetchSettings()
    }

    private fun fetchSettings() =
        viewModelScope.launch(dispatcherProvider.io + errorHandler) {

            val listItems = mutableListOf<RecyclerViewListItem>()

            listItems.add(
                SettingsHeadingItem(
                    "BASIC"
                )
            )

            listItems.add(
                SettingsItem(
                    "Bus stops query limit",
                    "Maximum number of bus stops fetched while searching",
                    ::onBusStopQueryLimitClicked
                )
            )

            listItems.add(
                SettingsItem(
                    "Closest bus stop's max distance",
                    "Maximum distance of the closest bus stop before you are too far away",
                    ::onMaximumBusStopDistanceClicked,
                )
            )

            /*
            listItems.add(
                SettingsItem(
                    "Default location",
                    "Default location when gps not available",
                    ::onDefaultLocationClicked,
                    last = true
                )
            )
             */

            val shouldShowStarredBusArrivals = showErrorStarredBusArrivalsUseCase()

            listItems.add(
                BooleanSettingsItem(
                    "Show starred buses that are not arriving",
                    { value ->
                        if (value)
                            "Starred buses that are not arriving will be shown on the home screen"
                        else
                            "Only starred buses that are arriving will be shown on the home screen"
                    },
                    shouldShowStarredBusArrivals,
                    ::onShouldShowStarredBusArrivalsClicked,
                    last = true,
                )
            )

            listItems.add(
                SettingsHeadingItem(
                    "NOTIFICATIONS"
                )
            )

            val shouldAlertStarredBusArrivals = shouldAlertStarredBusArrivals()

            listItems.add(
                BooleanSettingsItem(
                    "Notify starred buses arriving",
                    { value ->
                        if (value)
                            "You will be notified of the arriving starred buses at the current bus stop"
                        else
                            "You will be not be notified of the arriving starred buses"
                    },
                    shouldAlertStarredBusArrivals,
                    ::onShouldAlertStarredBusArrivalsClicked,
                )
            )

            listItems.add(
                SettingsItem(
                    "Starred buses arriving notify time",
                    "You will be notified if the bus is arriving in set minutes.",
                    ::alertStarredBusArrivalsMinutesClicked,
                )
            )

            listItems.add(
                SettingsItem(
                    "Starred buses arriving notify frequency",
                    "Frequency at which you will be notified of the arriving buses",
                    ::alertStarredBusArrivalsFrequencyClicked,
                    last = true,
                )
            )

            listItems.add(
                SettingsHeadingItem(
                    "ABOUT"
                )
            )
            listItems.add(
                VersionItem(
                    appVersionInfo
                )
            )

            _listItems.postValue(listItems)
        }

    private fun alertStarredBusArrivalsMinutesClicked() =
        viewModelScope.launch(dispatcherProvider.io + errorHandler) {

            val checkedItemIndex = when (alertStarredBusArrivalsMinutes()) {
                0 -> 0
                2 -> 1
                5 -> 2
                else -> 0
            }

            val newValue = withContext(dispatcherProvider.main) {
                dialogViewModelDelegate.showSingleChoiceIndexed(
                    "Notify me if the bus is arriving",
                    listOf(0, 2, 5),
                    checkedItemIndex,
                ) {
                    when (it) {
                        0 -> "Now"
                        2 -> "In 2 minutes"
                        5 -> "In 5 minutes"
                        else -> throw IllegalArgumentException("Item cannot be $it.")
                    }
                }
            }

            alertStarredBusArrivalsMinutes(newValue)
        }


    private fun alertStarredBusArrivalsFrequencyClicked() =
        viewModelScope.launch(dispatcherProvider.io + errorHandler) {

            val checkedItemIndex = when (alertStarredBusArrivalsFrequency()) {
                AlertFrequency.ONCE -> 0
                AlertFrequency.EVERY_TIME_BUS_GETS_CLOSER -> 1
            }

            val newValue = withContext(dispatcherProvider.main) {
                dialogViewModelDelegate.showSingleChoiceIndexed(
                    "Notify me about arriving bus",
                    listOf(AlertFrequency.ONCE, AlertFrequency.EVERY_TIME_BUS_GETS_CLOSER),
                    checkedItemIndex,
                ) {
                    when (it) {
                        AlertFrequency.ONCE -> AlertFrequency.ONCE.title
                        AlertFrequency.EVERY_TIME_BUS_GETS_CLOSER -> AlertFrequency.EVERY_TIME_BUS_GETS_CLOSER.title
                    }
                }
            }

            alertStarredBusArrivalsFrequency(newValue)
        }

    private fun onBusStopQueryLimitClicked() =
        viewModelScope.launch(dispatcherProvider.io + errorHandler) {

            val checkedItemIndex = when (busStopsQueryLimitUseCase()) {
                10 -> 0
                50 -> 1
                100 -> 2
                else -> 1
            }

            val newValue = withContext(dispatcherProvider.main) {
                dialogViewModelDelegate.showSingleChoice(
                    "Bus stops query limit",
                    listOf("10 bus stops", "50 bus stops", "100 bus stops"),
                    checkedItemIndex,
                ) {
                    it.substringBefore(" ").toInt()
                }
            }

            busStopsQueryLimitUseCase(newValue)
        }

    private fun onDefaultLocationClicked() {
        // do nothing
    }

    private fun onMaximumBusStopDistanceClicked() =
        viewModelScope.launch(dispatcherProvider.io + errorHandler) {

            val checkedItemIndex = when (maxDistanceOfClosesBusStopUseCase()) {
                10000 -> 0
                30000 -> 1
                50000 -> 2
                else -> 1
            }

            val newValue = withContext(dispatcherProvider.main) {
                dialogViewModelDelegate.showSingleChoice(
                    "Closest bus stop's max distance",
                    listOf("10 KM", "30 KM", "50 KM"),
                    checkedItemIndex,
                ) {
                    it.substringBefore(" ").toInt()
                }
            }

            maxDistanceOfClosesBusStopUseCase(newValue * 1000)

        }

    private fun onShouldShowStarredBusArrivalsClicked(newVal: Boolean) =
        viewModelScope.launch(dispatcherProvider.io) {
            showErrorStarredBusArrivalsUseCase(newVal)
        }

    private fun onShouldAlertStarredBusArrivalsClicked(newVal: Boolean) =
        viewModelScope.launch(dispatcherProvider.io) {
            shouldAlertStarredBusArrivals(newVal)
        }
}
