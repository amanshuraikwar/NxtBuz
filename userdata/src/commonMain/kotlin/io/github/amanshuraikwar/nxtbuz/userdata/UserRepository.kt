package io.github.amanshuraikwar.nxtbuz.userdata

import io.github.amanshuraikwar.nxtbuz.commonkmm.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.commonkmm.NxtBuzTheme
import io.github.amanshuraikwar.nxtbuz.commonkmm.user.UserState
import io.github.amanshuraikwar.nxtbuz.preferencestorage.PreferenceStorage
import io.github.amanshuraikwar.nxtbuz.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock

class UserRepositoryImpl constructor(
    private val preferenceStorage: PreferenceStorage,
    private val dispatcherProvider: CoroutinesDispatcherProvider,
    private val systemThemeHelper: SystemThemeHelper
) : UserRepository {
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

    private val useSystemTheme = MutableStateFlow(preferenceStorage.useSystemTheme)

    override fun getUseSystemThemeSync(): Boolean {
        return useSystemTheme.value
    }

    override fun getUseSystemThemeUpdates(): Flow<Boolean> {
        return useSystemTheme
    }

    override suspend fun getUserState(): UserState = withContext(dispatcherProvider.io) {
        return@withContext if (preferenceStorage.onboardingCompleted) {
            UserState.SetupComplete
        } else {
            UserState.New
        }
    }

    override suspend fun markSetupComplete() = withContext(dispatcherProvider.io) {
        updatePlayStoreReviewTime()
        preferenceStorage.onboardingCompleted = true
    }

    override suspend fun markSetupIncomplete() = withContext(dispatcherProvider.io) {
        preferenceStorage.playStoreReviewTimeMillis = -1L
        preferenceStorage.onboardingCompleted = false
    }

    override suspend fun shouldStartPlayStoreReview(): Boolean {
        return withContext(dispatcherProvider.io) {
            preferenceStorage.playStoreReviewTimeMillis != -1L &&
                    ((Clock.System.now().toEpochMilliseconds() -
                            preferenceStorage.playStoreReviewTimeMillis) /
                            (1000 * 60 * 60 * 24 * 7) > 1)
        }
    }

    override suspend fun updatePlayStoreReviewTime() {
        withContext(dispatcherProvider.io) {
            preferenceStorage.playStoreReviewTimeMillis = Clock.System.now().toEpochMilliseconds()
        }
    }

    override fun getTheme(): NxtBuzTheme {
        return theme
    }

    override suspend fun setForcedTheme(theme: NxtBuzTheme) = withContext(dispatcherProvider.io) {
        if (!preferenceStorage.useSystemTheme) {
            this@UserRepositoryImpl.theme = theme
        }
        preferenceStorage.theme = theme
    }

    override suspend fun getUseSystemTheme(): Boolean {
        return withContext(dispatcherProvider.io) {
            return@withContext preferenceStorage.useSystemTheme
        }
    }

    override suspend fun setUseSystemTheme(useSystemTheme: Boolean) {
        withContext(dispatcherProvider.io) {
            if (useSystemTheme) {
                this@UserRepositoryImpl.theme = if (systemThemeHelper.isSystemInDarkTheme()) {
                    NxtBuzTheme.DARK
                } else {
                    NxtBuzTheme.LIGHT
                }
            } else {
                this@UserRepositoryImpl.theme = preferenceStorage.theme
            }
            preferenceStorage.useSystemTheme = useSystemTheme
            this@UserRepositoryImpl.useSystemTheme.emit(useSystemTheme)
        }
    }

    override suspend fun refreshTheme() {
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

    override suspend fun getForcedTheme(): NxtBuzTheme {
        return withContext(dispatcherProvider.io) {
            return@withContext preferenceStorage.theme
        }
    }

    override suspend fun setHomeBusStopCode(busStopCode: String) {
        withContext(dispatcherProvider.io) {
            preferenceStorage.homeBusStopCode = busStopCode
        }
    }

    override suspend fun getHomeBusStopCode(): String? {
        return withContext(dispatcherProvider.io) {
            preferenceStorage.homeBusStopCode.takeIf { it != "" }
        }
    }
}