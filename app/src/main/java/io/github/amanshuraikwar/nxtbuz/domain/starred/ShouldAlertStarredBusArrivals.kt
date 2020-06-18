package io.github.amanshuraikwar.nxtbuz.domain.starred

import io.github.amanshuraikwar.nxtbuz.data.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.data.prefs.PreferenceStorage
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ShouldAlertStarredBusArrivals @Inject constructor(
    private val preferenceStorage: PreferenceStorage,
    private val dispatcherProvider: CoroutinesDispatcherProvider,
) {

    suspend operator fun invoke(): Boolean = withContext(dispatcherProvider.io) {
        preferenceStorage.alertStarredBusArrivals
    }

    suspend operator fun invoke(newVal: Boolean) = withContext(dispatcherProvider.io) {
        preferenceStorage.alertStarredBusArrivals = newVal
    }
}