package io.github.amanshuraikwar.nxtbuz.domain.location

import io.github.amanshuraikwar.nxtbuz.common.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.data.prefs.PreferenceStorage
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LocationPermissionDeniedPermanentlyUseCase @Inject constructor(
    private val preferenceStorage: PreferenceStorage,
    private val dispatcherProvider: CoroutinesDispatcherProvider,
) {
    suspend operator fun invoke(): Boolean {
        return withContext(dispatcherProvider.io) {
            preferenceStorage.permissionDeniedPermanently
        }
    }

    suspend operator fun invoke(newValue: Boolean) {
        withContext(dispatcherProvider.io) {
            preferenceStorage.permissionDeniedPermanently = newValue
        }
    }
}


