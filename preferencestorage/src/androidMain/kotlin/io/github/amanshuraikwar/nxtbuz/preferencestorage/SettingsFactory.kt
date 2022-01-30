package io.github.amanshuraikwar.nxtbuz.preferencestorage

import android.content.Context
import com.russhwolf.settings.AndroidSettings

actual class SettingsFactory(
    private val context: Context
) {
    actual fun createPreferenceStorage(): PreferenceStorage {
        return PreferenceStorageImpl {
            AndroidSettings.Factory(context).create(PREFS_NAME)
        }
    }

    actual companion object {
        private const val PREFS_NAME = "io.github.amanshuraikwar.nxtbuz"
        // added _1 because we want to force rebuild the db
        private const val PREF_ONBOARDING = "pref_onboarding_1"
        actual fun prefOnboardingKey(): String {
            return PREF_ONBOARDING
        }
    }
}