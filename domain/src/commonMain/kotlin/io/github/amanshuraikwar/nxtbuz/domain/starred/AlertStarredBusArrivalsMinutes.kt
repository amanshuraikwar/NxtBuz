package io.github.amanshuraikwar.nxtbuz.domain.starred

import io.github.amanshuraikwar.nxtbuz.commonkmm.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.preferencestorage.PreferenceStorage
import kotlinx.coroutines.withContext

class AlertStarredBusArrivalsMinutes constructor(
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