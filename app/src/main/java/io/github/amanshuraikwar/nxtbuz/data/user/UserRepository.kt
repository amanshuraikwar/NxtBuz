package io.github.amanshuraikwar.nxtbuz.data.user

import io.github.amanshuraikwar.nxtbuz.data.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.data.prefs.PreferenceStorage
import io.github.amanshuraikwar.nxtbuz.data.user.model.UserState
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val preferenceStorage: PreferenceStorage,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) {

    suspend fun getUserState(): UserState = withContext(dispatcherProvider.io) {
        return@withContext if (preferenceStorage.onboardingCompleted) {
            UserState.SetupComplete
        } else {
            UserState.New
        }
    }

    suspend fun markSetupComplete() = withContext(dispatcherProvider.io) {
        preferenceStorage.onboardingCompleted = true
    }

    suspend fun markSetupIncomplete() = withContext(dispatcherProvider.io) {
        preferenceStorage.onboardingCompleted = false
    }
}