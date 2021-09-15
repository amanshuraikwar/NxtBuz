package io.github.amanshuraikwar.nxtbuz.userdata

import io.github.amanshuraikwar.nxtbuz.commonkmm.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.commonkmm.NxtBuzTheme
import io.github.amanshuraikwar.nxtbuz.commonkmm.user.UserState
import io.github.amanshuraikwar.nxtbuz.preferencestorage.PreferenceStorage
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock

class UserRepository constructor(
    private val preferenceStorage: PreferenceStorage,
    private val dispatcherProvider: CoroutinesDispatcherProvider,
    private val systemThemeHelper: SystemThemeHelper
) {
    private var theme: NxtBuzTheme

    init {
        theme = if (preferenceStorage.useSystemTheme) {
            if (systemThemeHelper.isSystemInDarkTheme()) {
                NxtBuzTheme.DARK
            } else {
                NxtBuzTheme.LIGHT
            }
        } else {
            preferenceStorage.theme
        }
    }

    suspend fun getUserState(): UserState = withContext(dispatcherProvider.io) {
        return@withContext if (preferenceStorage.onboardingCompleted) {
            UserState.SetupComplete
        } else {
            UserState.New
        }
    }

    suspend fun markSetupComplete() = withContext(dispatcherProvider.io) {
        updatePlayStoreReviewTime()
        preferenceStorage.onboardingCompleted = true
    }

    suspend fun markSetupIncomplete() = withContext(dispatcherProvider.io) {
        preferenceStorage.playStoreReviewTimeMillis = -1L
        preferenceStorage.onboardingCompleted = false
    }

    suspend fun shouldStartPlayStoreReview(): Boolean {
        return withContext(dispatcherProvider.io) {
            preferenceStorage.playStoreReviewTimeMillis != -1L &&
                    ((Clock.System.now().toEpochMilliseconds() -
                            preferenceStorage.playStoreReviewTimeMillis) /
                            (1000 * 60 * 60 * 24 * 7) > 1)
        }
    }

    suspend fun updatePlayStoreReviewTime() {
        withContext(dispatcherProvider.io) {
            preferenceStorage.playStoreReviewTimeMillis = Clock.System.now().toEpochMilliseconds()
        }
    }

    fun getTheme(): NxtBuzTheme {
        return theme
    }

    suspend fun setForcedTheme(theme: NxtBuzTheme) = withContext(dispatcherProvider.io) {
        if (!preferenceStorage.useSystemTheme) {
            this@UserRepository.theme = theme
        }
        preferenceStorage.theme = theme
    }

    suspend fun getUseSystemTheme(): Boolean {
        return withContext(dispatcherProvider.io) {
            return@withContext preferenceStorage.useSystemTheme
        }
    }

    suspend fun setUseSystemTheme(useSystemTheme: Boolean) {
        withContext(dispatcherProvider.io) {
            if (useSystemTheme) {
                this@UserRepository.theme = if (systemThemeHelper.isSystemInDarkTheme()) {
                    NxtBuzTheme.DARK
                } else {
                    NxtBuzTheme.LIGHT
                }
            } else {
                this@UserRepository.theme = preferenceStorage.theme
            }
            preferenceStorage.useSystemTheme = useSystemTheme
        }
    }

    suspend fun refreshTheme() {
        withContext(dispatcherProvider.io) {
            theme = if (preferenceStorage.useSystemTheme) {
                if (systemThemeHelper.isSystemInDarkTheme()) {
                    NxtBuzTheme.DARK
                } else {
                    NxtBuzTheme.LIGHT
                }
            } else {
                preferenceStorage.theme
            }
        }
    }

    suspend fun getForcedTheme(): NxtBuzTheme {
        return withContext(dispatcherProvider.io) {
            return@withContext preferenceStorage.theme
        }
    }
}