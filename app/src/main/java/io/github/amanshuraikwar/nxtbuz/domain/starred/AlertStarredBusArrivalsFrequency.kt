package io.github.amanshuraikwar.nxtbuz.domain.starred

import io.github.amanshuraikwar.nxtbuz.common.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.common.model.AlertFrequency
import io.github.amanshuraikwar.nxtbuz.data.prefs.PreferenceStorage
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