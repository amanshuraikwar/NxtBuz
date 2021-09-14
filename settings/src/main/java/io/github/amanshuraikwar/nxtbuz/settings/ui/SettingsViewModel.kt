package io.github.amanshuraikwar.nxtbuz.settings.ui

import android.util.Log
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.github.amanshuraikwar.nxtbuz.common.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.commonkmm.NxtBuzTheme
import io.github.amanshuraikwar.nxtbuz.common.util.NavigationUtil
import io.github.amanshuraikwar.nxtbuz.domain.busstop.BusStopsQueryLimitUseCase
import io.github.amanshuraikwar.nxtbuz.domain.map.ShouldShowMapUseCase
import io.github.amanshuraikwar.nxtbuz.domain.starred.ShowErrorStarredBusArrivalsUseCase
import io.github.amanshuraikwar.nxtbuz.domain.user.*
import io.github.amanshuraikwar.nxtbuz.settings.ui.delegate.AppThemeDelegate
import io.github.amanshuraikwar.nxtbuz.settings.ui.delegate.AppThemeDelegateImpl
import io.github.amanshuraikwar.nxtbuz.settings.ui.model.SettingsItemData
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

private const val TAG = "SettingsViewModel"

class SettingsViewModel @Inject constructor(
    private val busStopsQueryLimitUseCase: BusStopsQueryLimitUseCase,
    private val showErrorStarredBusArrivalsUseCase: ShowErrorStarredBusArrivalsUseCase,
    private val shouldShowMapUseCase: ShouldShowMapUseCase,
    private val getForcedThemeUseCase: GetForcedThemeUseCase,
    private val setForcedThemeUseCase: SetForcedThemeUseCase,
    private val getUseSystemThemeUseCase: GetUseSystemThemeUseCase,
    private val setUseSystemThemeUseCase: SetUseSystemThemeUseCase,
    private val shouldStartPlayStoreReviewUseCase: ShouldStartPlayStoreReviewUseCase,
    private val updatePlayStoreReviewTimeUseCase: UpdatePlayStoreReviewTimeUseCase,
    private val navigationUtil: NavigationUtil,
    appThemeDelegateImpl: AppThemeDelegateImpl,
    dispatcherProvider: CoroutinesDispatcherProvider
) : ViewModel(), AppThemeDelegate by appThemeDelegateImpl {

    @Inject
    @Named("appVersionInfo")
    lateinit var appVersionInfo: String

    private val errorHandler = CoroutineExceptionHandler { _, th ->
        Log.e(TAG, "errorHandler: $th", th)
        FirebaseCrashlytics.getInstance().recordException(th)
    }
    private val coroutineContext = errorHandler + dispatcherProvider.computation

    val listItemsFlow = MutableStateFlow<SnapshotStateList<SettingsItemData>>(SnapshotStateList())

    init {
        FirebaseCrashlytics.getInstance().setCustomKey("viewModel", TAG)
    }

    private inline fun <reified T : SettingsItemData> List<SettingsItemData>.findFirst(
        id: String
    ): Pair<Int, T>? {
        return indexOfFirst {
            it is T &&
                    it.id == id
        }.let { index ->
            if (index == -1) {
                null
            } else {
                (get(index) as? T)?.let {
                    Pair(index, it)
                }
            }
        }
    }

    fun fetchSettings() {
        viewModelScope.launch(coroutineContext) {
            refreshTheme()

            val listItems = SnapshotStateList<SettingsItemData>()

            listItems.add(
                SettingsItemData.About(
                    appName = "Next Bus SG",
                    versionName = appVersionInfo
                )
            )

            listItems.add(
                SettingsItemData.Header(
                    "Appearance"
                )
            )

            val useSystemTheme = getUseSystemThemeUseCase()

            listItems.add(
                SettingsItemData.Switch(
                    id = "use-system-theme",
                    title = "Use System Theme",
                    onDescription = "The app will follow the System Theme",
                    offDescription = "The app will use the Theme set in the App",
                    on = useSystemTheme,
                    onClick = { newValue ->
                        viewModelScope.launch {
                            listItems
                                .findFirst<SettingsItemData.Switch>("use-system-theme")
                                ?.let { (index, item) ->
                                    listItems[index] = item.copy(
                                        on = newValue,
                                        enabled = item.enabled
                                    )
                                }

                            listItems
                                .findFirst<SettingsItemData.Switch>("enable-dark-mode")
                                ?.let { (index, item) ->
                                    listItems[index] = item.copy(on = item.on, enabled = !newValue)
                                }

                            setUseSystemThemeUseCase(newValue)
                            delay(300)
                            refreshTheme()
                        }
                    }
                )
            )

            val theme = getForcedThemeUseCase()

            listItems.add(
                SettingsItemData.Switch(
                    id = "enable-dark-mode",
                    enabled = !useSystemTheme,
                    title = "Come to the \"Dark\" side padawan :)",
                    onDescription = "The app will be in dark theme",
                    offDescription = "The app will be in light theme",
                    on = theme == NxtBuzTheme.DARK,
                    onClick = { newValue ->
                        viewModelScope.launch {
                            listItems
                                .findFirst<SettingsItemData.Switch>("enable-dark-mode")
                                ?.let { (index, item) ->
                                    listItems[index] = item.copy(
                                        on = newValue,
                                        enabled = item.enabled
                                    )
                                }

                            setForcedThemeUseCase(
                                if (newValue) {
                                    NxtBuzTheme.DARK
                                } else {
                                    NxtBuzTheme.LIGHT
                                }
                            )
                            delay(300)
                            refreshTheme()
                        }
                    }
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
                    id = "bus-stops-query-limit",
                    title = "Bus stops query limit",
                    description = "Maximum number of bus stops fetched while searching",
                    options = listOf("10 bus stops", "50 bus stops", "100 bus stops"),
                    selectedIndex = checkedItemIndex,
                    onClick = { selectedIndex ->
                        viewModelScope.launch {
                            val newValue = when (selectedIndex) {
                                0 -> 10
                                1 -> 50
                                2 -> 100
                                else -> 50
                            }
                            listItems
                                .findFirst<SettingsItemData.RadioGroup>("bus-stops-query-limit")
                                ?.let { (index, item) ->
                                    listItems[index] = item.copy(selectedIndex = selectedIndex)
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
                    id = "starred-buses-not-arriving",
                    title = "Show starred buses that are not arriving",
                    onDescription = "Starred buses that are not arriving will be shown on the home screen",
                    offDescription = "Only starred buses that are arriving will be shown on the home screen",
                    on = shouldShowStarredBusArrivals,
                    onClick = { newValue ->
                        viewModelScope.launch {
                            listItems
                                .findFirst<SettingsItemData.Switch>("starred-buses-not-arriving")
                                ?.let { (index, item) ->
                                    listItems[index] = item.copy(
                                        on = newValue,
                                        enabled = item.enabled
                                    )
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
                    id = "show-map",
                    title = "Show map for easy navigation",
                    onDescription =
                    "Map will be shown to help locate bus stops and routes, " +
                            "might impact the app performance",
                    offDescription =
                    "I'm a SingaPro! " +
                            "I want performance, not some silly lil map",
                    on = shouldShowMap,
                    onClick = { newValue ->
                        viewModelScope.launch {
                            listItems
                                .findFirst<SettingsItemData.Switch>("show-map")
                                ?.let { (index, item) ->
                                    listItems[index] = item.copy(
                                        on = newValue,
                                        enabled = item.enabled
                                    )
                                }
                            shouldShowMapUseCase(newValue)
                        }
                    }
                )
            )

            listItems.add(
                SettingsItemData.Header(title = "")
            )

            listItems.add(
                SettingsItemData.RateOnPlayStore
            )

            listItems.add(
                SettingsItemData.Oss
            )

            listItems.add(
                SettingsItemData.RequestFeature
            )

            listItems.add(
                SettingsItemData.MadeBy
            )

            listItems.add(
                SettingsItemData.MadeWith
            )

            listItemsFlow.emit(listItems)

            if (shouldStartPlayStoreReviewUseCase()) {
                navigationUtil.startPlayStoreReview()
                updatePlayStoreReviewTimeUseCase()
            }
        }
    }

    fun onOssClick() {
        navigationUtil.goToOssActivity()
    }

    fun onRequestFeatureClick() {
        navigationUtil.goToEmail(
            address = "amanshuraikwar.dev@gmail.com",
            subject = "Next Bus SG Feature Request",
        )
    }

    fun onMadeByClick() {
        navigationUtil.goToTwitter(
            username = "amanshuraikwar_",
        )
    }

    fun onRateOnPlayStoreClick() {
        viewModelScope.launch(coroutineContext) {
            navigationUtil.goToPlayStoreListing()
        }
    }
}
