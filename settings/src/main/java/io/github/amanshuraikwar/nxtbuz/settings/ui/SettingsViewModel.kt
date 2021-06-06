package io.github.amanshuraikwar.nxtbuz.settings.ui

import android.util.Log
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.github.amanshuraikwar.nxtbuz.common.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.domain.busstop.BusStopsQueryLimitUseCase
import io.github.amanshuraikwar.nxtbuz.domain.map.ShouldShowMapUseCase
import io.github.amanshuraikwar.nxtbuz.domain.starred.ShowErrorStarredBusArrivalsUseCase
import io.github.amanshuraikwar.nxtbuz.settings.ui.model.SettingsItemData
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

private const val TAG = "SettingsViewModel"

class SettingsViewModel @Inject constructor(
    private val busStopsQueryLimitUseCase: BusStopsQueryLimitUseCase,
    private val showErrorStarredBusArrivalsUseCase: ShowErrorStarredBusArrivalsUseCase,
    private val shouldShowMapUseCase: ShouldShowMapUseCase,
    dispatcherProvider: CoroutinesDispatcherProvider
) : ViewModel() {

    @Inject
    @Named("appVersionInfo")
    lateinit var appVersionInfo: String

    private val errorHandler = CoroutineExceptionHandler { _, th ->
        Log.e(TAG, "errorHandler: $th", th)
        FirebaseCrashlytics.getInstance().recordException(th)
    }
    private val coroutineContext = errorHandler + dispatcherProvider.computation

    val listItemsFlow = MutableSharedFlow<SnapshotStateList<SettingsItemData>>(replay = 1)
    private var listItems = SnapshotStateList<SettingsItemData>()

    init {
        FirebaseCrashlytics.getInstance().setCustomKey("viewModel", TAG)
        fetchSettings()
    }

    private fun fetchSettings() =
        viewModelScope.launch(coroutineContext) {

            val listItems = SnapshotStateList<SettingsItemData>()

            listItems.add(
                SettingsItemData.About(
                    appName = "NxtBuz",
                    versionName = appVersionInfo
                )
            )

            listItems.add(
                SettingsItemData.Header(
                    "Bus Stops"
                )
            )

            val checkedItemIndex = when (busStopsQueryLimitUseCase()) {
                10 -> 0
                50 -> 1
                100 -> 2
                else -> 1
            }

            listItems.add(
                SettingsItemData.RadioGroup(
                    title = "Bus stops query limit",
                    description = "Maximum number of bus stops fetched while searching",
                    options = listOf("10 bus stops", "50 bus stops", "100 bus stops"),
                    selectedIndex = checkedItemIndex,
                    onClick = { index ->
                        viewModelScope.launch {
                            val newValue = when (index) {
                                0 -> 10
                                1 -> 50
                                2 -> 100
                                else -> 50
                            }
                            (listItems[2] as? SettingsItemData.RadioGroup)?.let {
                                listItems[2] = it.copy(selectedIndex = index)
                            }
                            busStopsQueryLimitUseCase(newValue)
                        }
                    }
                )
            )

            listItems.add(
                SettingsItemData.Header(
                    "Starred"
                )
            )

            val shouldShowStarredBusArrivals = showErrorStarredBusArrivalsUseCase()

            listItems.add(
                SettingsItemData.Switch(
                    title = "Show starred buses that are not arriving",
                    enabledDescription = "Starred buses that are not arriving will be shown on the home screen",
                    disabledDescription = "Only starred buses that are arriving will be shown on the home screen",
                    enabled = shouldShowStarredBusArrivals,
                    onClick = { newValue ->
                        viewModelScope.launch {
                            (listItems[4] as? SettingsItemData.Switch)?.let {
                                listItems[4] = it.copy(enabled = newValue)
                            }
                            showErrorStarredBusArrivalsUseCase(newValue)
                        }
                    }
                )
            )

            listItems.add(
                SettingsItemData.Header(
                    "Map"
                )
            )

            val shouldShowMap = shouldShowMapUseCase()

            listItems.add(
                SettingsItemData.Switch(
                    title = "Show map for easy navigation",
                    enabledDescription =
                    "Map will be shown to help locate bus stops and routes, " +
                            "might impact the app performance",
                    disabledDescription =
                    "I'm a SingaPro! " +
                            "I want performance, not some silly lil map",
                    enabled = shouldShowMap,
                    onClick = { newValue ->
                        viewModelScope.launch {
                            (listItems[6] as? SettingsItemData.Switch)?.let {
                                listItems[6] = it.copy(enabled = newValue)
                            }
                            shouldShowMapUseCase(newValue)
                        }
                    }
                )
            )

            this@SettingsViewModel.listItems = listItems
            listItemsFlow.emit(listItems)
        }
}
