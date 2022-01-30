package io.github.amanshuraikwar.nxtbuz.preferencestorage

import com.russhwolf.settings.AppleSettings
import platform.Foundation.NSUserDefaults

actual class SettingsFactory(
    private val settingsSuiteName: String
) {
    actual fun createPreferenceStorage(): PreferenceStorage {
        return PreferenceStorageImpl {
            AppleSettings(NSUserDefaults(suiteName = settingsSuiteName))
        }
    }

    actual companion object {
        private const val PREF_ONBOARDING = "pref_onboarding"
        actual fun prefOnboardingKey(): String {
            return PREF_ONBOARDING
        }
    }
}