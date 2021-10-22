package io.github.amanshuraikwar.nxtbuz.domain.starred

import io.github.amanshuraikwar.nxtbuz.commonkmm.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.commonkmm.AlertFrequency
import io.github.amanshuraikwar.nxtbuz.preferencestorage.PreferenceStorage
import kotlinx.coroutines.withContext

class AlertStarredBusArrivalsFrequency constructor(
    private val preferenceStorage: PreferenceStorage,
    private val dispatcherProvider: CoroutinesDispatcherProvider,
) {
    suspend operator fun invoke(): AlertFrequency = withContext(dispatcherProvider.io) {
        preferenceStorage.alertStarredBusArrivalsFrequency
    }

    suspend operator fun invoke(newVal: AlertFrequency) = withContext(dispatcherProvider.io) {
        preferenceStorage.alertStarredBusArrivalsFrequency = newVal
    }
}