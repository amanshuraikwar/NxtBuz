package io.github.amanshuraikwar.nxtbuz.settings.ui

import android.util.Log
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.github.amanshuraikwar.nxtbuz.common.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.common.model.NxtBuzTheme
import io.github.amanshuraikwar.nxtbuz.common.util.NavigationUtil
import io.github.amanshuraikwar.nxtbuz.domain.busstop.BusStopsQueryLimitUseCase
import io.github.amanshuraikwar.nxtbuz.domain.map.ShouldShowMapUseCase
import io.github.amanshuraikwar.nxtbuz.domain.starred.ShowErrorStarredBusArrivalsUseCase
import io.github.amanshuraikwar.nxtbuz.domain.user.GetForcedThemeUseCase
import io.github.amanshuraikwar.nxtbuz.domain.user.GetUseSystemThemeUseCase
import io.github.amanshuraikwar.nxtbuz.domain.user.SetForcedThemeUseCase
import io.github.amanshuraikwar.nxtbuz.domain.user.SetUseSystemThemeUseCase
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
                    onDescription = "Starred buses that are not arriving will be shown on the home screen",
                    offDescription = "Only starred buses that are arriving will be shown on the home screen",
                    on = shouldShowStarredBusArrivals,
                    onClick = { newValue ->
                        viewModelScope.launch {
                            (listItems[4] as? SettingsItemData.Switch)?.let {
                                listItems[4] = it.copy(on = newValue)
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
                    onDescription =
                    "Map will be shown to help locate bus stops and routes, " +
                            "might impact the app performance",
                    offDescription =
                    "I'm a SingaPro! " +
                            "I want performance, not some silly lil map",
                    on = shouldShowMap,
                    onClick = { newValue ->
                        viewModelScope.launch {
                            (listItems[6] as? SettingsItemData.Switch)?.let {
                                listItems[6] = it.copy(on = newValue)
                            }
                            shouldShowMapUseCase(newValue)
                        }
                    }
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
                    title = "Use System Theme",
                    onDescription = "The app will follow the System Theme",
                    offDescription = "The app will use the Theme set in the App",
                    on = useSystemTheme,
                    onClick = { newValue ->
                        viewModelScope.launch {
                            (listItems[8] as? SettingsItemData.Switch)?.let {
                                listItems[8] = it.copy(on = newValue)
                            }
                            (listItems[9] as? SettingsItemData.Switch)?.let {
                                listItems[9] = it.copy(enabled = !newValue)
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
                    enabled = !useSystemTheme,
                    title = "Enable \"Dark\" Mode :)",
                    onDescription = "The app will be in dark theme",
                    offDescription = "The app will be in light theme",
                    on = theme == NxtBuzTheme.DARK,
                    onClick = { newValue ->
                        viewModelScope.launch {
                            (listItems[9] as? SettingsItemData.Switch)?.let {
                                listItems[9] = it.copy(on = newValue)
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
                SettingsItemData.Header(title = "")
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
            navigationUtil.startPlayStoreReview()
        }
    }
}
