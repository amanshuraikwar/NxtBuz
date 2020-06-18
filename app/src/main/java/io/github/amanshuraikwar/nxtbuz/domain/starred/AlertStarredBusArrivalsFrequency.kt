package io.github.amanshuraikwar.nxtbuz.domain.starred

import io.github.amanshuraikwar.nxtbuz.data.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.data.prefs.PreferenceStorage
import io.github.amanshuraikwar.nxtbuz.data.prefs.model.AlertFrequency
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AlertStarredBusArrivalsFrequency @Inject constructor(
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