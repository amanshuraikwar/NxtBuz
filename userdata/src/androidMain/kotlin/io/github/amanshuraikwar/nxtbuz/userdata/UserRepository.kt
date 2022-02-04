package io.github.amanshuraikwar.nxtbuz.userdata

import io.github.amanshuraikwar.nxtbuz.commonkmm.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.commonkmm.SystemThemeHelper
import io.github.amanshuraikwar.nxtbuz.commonkmm.user.UserState
import io.github.amanshuraikwar.nxtbuz.preferencestorage.PreferenceStorage
import kotlinx.coroutines.withContext

class UserRepositoryAndroidImpl constructor(
    preferenceStorage: PreferenceStorage,
    dispatcherProvider: CoroutinesDispatcherProvider,
    systemThemeHelper: SystemThemeHelper
) : UserRepositoryImpl(
    preferenceStorage,
    dispatcherProvider,
    systemThemeHelper,
) {
    override suspend fun getUserState(): UserState {
        return withContext(dispatcherProvider.io) {
            if (preferenceStorage.onboardingCompleted
                && preferenceStorage.sqlDelightAndroidMigrationComplete
            ) {
                UserState.SetupComplete
            } else {
                UserState.New
            }
        }
    }

    override suspend fun markSetupComplete() {
        withContext(dispatcherProvider.io) {
            preferenceStorage.onboardingCompleted = true
            preferenceStorage.sqlDelightAndroidMigrationComplete = true
        }
    }

    override suspend fun markSetupIncomplete() {
        withContext(dispatcherProvider.io) {
            preferenceStorage.onboardingCompleted = false
        }
    }
}