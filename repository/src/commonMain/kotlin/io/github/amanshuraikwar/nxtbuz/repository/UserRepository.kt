package io.github.amanshuraikwar.nxtbuz.repository

import io.github.amanshuraikwar.nxtbuz.commonkmm.NxtBuzTheme
import io.github.amanshuraikwar.nxtbuz.commonkmm.user.UserState
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUseSystemThemeSync(): Boolean

    fun getUseSystemThemeUpdates(): Flow<Boolean>

    suspend fun getUserState(): UserState

    suspend fun markSetupComplete()

    suspend fun markSetupIncomplete()

    suspend fun shouldStartPlayStoreReview(): Boolean

    suspend fun updatePlayStoreReviewTime()

    fun getTheme(): NxtBuzTheme

    suspend fun setForcedTheme(theme: NxtBuzTheme)

    suspend fun getUseSystemTheme(): Boolean

    suspend fun setUseSystemTheme(useSystemTheme: Boolean)

    suspend fun refreshTheme()

    suspend fun getForcedTheme(): NxtBuzTheme

    suspend fun setHomeBusStopCode(busStopCode: String)

    suspend fun getHomeBusStopCode(): String?
}