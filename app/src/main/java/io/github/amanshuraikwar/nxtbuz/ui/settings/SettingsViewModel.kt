package io.github.amanshuraikwar.nxtbuz.ui.settings

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.github.amanshuraikwar.multiitemadapter.RecyclerViewListItem
import io.github.amanshuraikwar.nxtbuz.data.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.domain.busstop.BusStopsQueryLimitUseCase
import io.github.amanshuraikwar.nxtbuz.domain.busstop.MaxDistanceOfClosesBusStopUseCase
import io.github.amanshuraikwar.nxtbuz.domain.location.DefaultLocationUseCase
import io.github.amanshuraikwar.nxtbuz.domain.starred.ShowErrorStarredBusArrivalsUseCase
import io.github.amanshuraikwar.nxtbuz.ui.list.BooleanSettingsItem
import io.github.amanshuraikwar.nxtbuz.ui.list.SettingsHeadingItem
import io.github.amanshuraikwar.nxtbuz.ui.list.SettingsItem
import io.github.amanshuraikwar.nxtbuz.ui.list.VersionItem
import io.github.amanshuraikwar.nxtbuz.ui.settings.dialog.DialogViewModelDelegate
import io.github.amanshuraikwar.nxtbuz.util.Util
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val TAG = "SettingsViewModel"

@ExperimentalCoroutinesApi
class SettingsViewModel @Inject constructor(
    private val busStopsQueryLimitUseCase: BusStopsQueryLimitUseCase,
    private val maxDistanceOfClosesBusStopUseCase: MaxDistanceOfClosesBusStopUseCase,
    private val showErrorStarredBusArrivalsUseCase: ShowErrorStarredBusArrivalsUseCase,
    private val dialogViewModelDelegate: DialogViewModelDelegate,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) : ViewModel() {

    private val errorHandler = CoroutineExceptionHandler { _, th ->
        Log.e(TAG, "errorHandler: $th", th)
        FirebaseCrashlytics.getInstance().recordException(th)
    }

    private val _listItems = MutableLiveData<MutableList<RecyclerViewListItem>>()
    val listItems = _listItems.map { it }

    init {
        FirebaseCrashlytics.getInstance().setCustomKey("viewModel", TAG)
        fetchSettings()
    }

    private fun fetchSettings() =
        viewModelScope.launch(dispatcherProvider.io + errorHandler) {

            val listItems = mutableListOf<RecyclerViewListItem>()

            listItems.add(SettingsHeadingItem("BASIC"))

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

            listItems.add(SettingsHeadingItem("ABOUT"))
            listItems.add(VersionItem(Util.getVersionInfo()))

            _listItems.postValue(listItems)
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
}
