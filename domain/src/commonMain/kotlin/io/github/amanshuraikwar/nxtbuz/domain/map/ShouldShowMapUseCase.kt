package io.github.amanshuraikwar.nxtbuz.domain.map

import io.github.amanshuraikwar.nxtbuz.commonkmm.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.preferencestorage.PreferenceStorage
import kotlinx.coroutines.withContext

class ShouldShowMapUseCase constructor(
    private val preferenceStorage: PreferenceStorage,
    private val dispatcherProvider: CoroutinesDispatcherProvider,
) {
    suspend operator fun invoke(): Boolean {
        return withContext(dispatcherProvider.io) {
            preferenceStorage.showMap
        }
    }

    suspend operator fun invoke(newValue: Boolean) {
        return withContext(dispatcherProvider.io) {
            preferenceStorage.showMap = newValue
        }
    }
}