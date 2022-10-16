package io.github.amanshuraikwar.nxtbuz.preferencestorage

import com.russhwolf.settings.AppleSettings
import com.russhwolf.settings.Settings
import platform.Foundation.NSUserDefaults

// TODO-amanshuraikwar (16 Oct 2022 08:32:36 PM):
//  for some reason cast from AppleSettings to Settings produces error
@Suppress("CAST_NEVER_SUCCEEDS")
actual class SettingsFactory(
    private val settingsSuiteName: String
) {
    actual fun createPreferenceStorage(): PreferenceStorage {
        return PreferenceStorageImpl {
            AppleSettings(NSUserDefaults(suiteName = settingsSuiteName)) as Settings
        }
    }

    actual fun createSettings(): Settings {
        return AppleSettings(NSUserDefaults(suiteName = settingsSuiteName)) as Settings
    }
}