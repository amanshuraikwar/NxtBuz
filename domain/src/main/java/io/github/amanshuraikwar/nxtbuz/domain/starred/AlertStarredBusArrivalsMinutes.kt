package io.github.amanshuraikwar.nxtbuz.domain.starred

import io.github.amanshuraikwar.nxtbuz.common.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.data.prefs.PreferenceStorage
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AlertStarredBusArrivalsMinutes @Inject constructor(
    private val preferenceStorage: PreferenceStorage,
    private val dispatcherProvider: CoroutinesDispatcherProvider,
) {
    suspend operator fun invoke(): Int = withContext(dispatcherProvider.io) {
        preferenceStorage.alertStarredBusArrivalsMinutes
    }

    suspend operator fun invoke(newVal: Int) = withContext(dispatcherProvider.io) {
        preferenceStorage.alertStarredBusArrivalsMinutes = newVal
    }
}