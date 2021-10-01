package io.github.amanshuraikwar.nxtbuz.preferencestorage

import com.russhwolf.settings.AppleSettings
import platform.Foundation.NSUserDefaults

actual class SettingsFactory(
    private val settingsSuiteName: String
) {
    actual fun createPreferenceStorage(): PreferenceStorage {
        // TODO-amanshuraikwar (01 Oct 2021 09:08:45 PM):
        //  this is not working
        //  data is not being shared between app and widget
        return PreferenceStorageImpl {
            AppleSettings(NSUserDefaults(settingsSuiteName))
        }
    }
}