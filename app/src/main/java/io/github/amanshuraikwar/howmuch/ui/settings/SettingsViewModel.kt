package io.github.amanshuraikwar.howmuch.ui.settings

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import io.github.amanshuraikwar.howmuch.data.di.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.howmuch.domain.busstop.BusStopsQueryLimitUseCase
import io.github.amanshuraikwar.howmuch.domain.busstop.DefaultLocationUseCase
import io.github.amanshuraikwar.howmuch.domain.busstop.MaxDistanceOfClosesBusStopUseCase
import io.github.amanshuraikwar.howmuch.ui.list.SettingsItem
import io.github.amanshuraikwar.howmuch.ui.list.VersionItem
import io.github.amanshuraikwar.multiitemadapter.BuildConfig
import io.github.amanshuraikwar.multiitemadapter.RecyclerViewListItem
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "SettingsViewModel"

class SettingsViewModel @Inject constructor(
    private val busStopsQueryLimitUseCase: BusStopsQueryLimitUseCase,
    private val defaultLocationUseCase: DefaultLocationUseCase,
    private val maxDistanceOfClosesBusStopUseCase: MaxDistanceOfClosesBusStopUseCase,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) : ViewModel() {

    private val errorHandler = CoroutineExceptionHandler { _, th ->
        Log.e(TAG, "errorHandler: $th", th)
    }

    private val _listItems = MutableLiveData<MutableList<RecyclerViewListItem>>()
    val listItems = _listItems.map { it }

    init {
        fetchSettings()
    }

    private fun fetchSettings() =
        viewModelScope.launch(dispatcherProvider.io + errorHandler) {
            val listItems = listOf(
                async {
                    SettingsItem(
                        "Bus stops query limit",
                        "Maximum number of bus stops fetched while searching.",
                        "${busStopsQueryLimitUseCase()} bus stops"
                    ) as RecyclerViewListItem
                },
                async {
                    val (lat, lon) = defaultLocationUseCase()
                    SettingsItem(
                        "Default location",
                        "Default location to be displayed on map when gps not available.",
                        "$lat • $lon"
                    ) as RecyclerViewListItem
                },
                async {
                    SettingsItem(
                        "Maximum bus stop distance",
                        "Maximum distance of the closest bus stop before the you are to far away.",
                        "${maxDistanceOfClosesBusStopUseCase()} metres"
                    ) as RecyclerViewListItem
                }
            ).awaitAll().toMutableList()
            listItems.add(
                VersionItem(
                    "V${BuildConfig.VERSION_NAME}  •  ${BuildConfig.VERSION_CODE}  •  ${BuildConfig.BUILD_TYPE.toUpperCase()}"
                )
            )
            _listItems.postValue(listItems)
        }
}
