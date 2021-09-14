package io.github.amanshuraikwar.nxtbuz.preferencestorage

actual class SettingsFactory {
    actual fun createPreferenceStorage(): PreferenceStorage {
        return PreferenceStorageImpl()
    }
}