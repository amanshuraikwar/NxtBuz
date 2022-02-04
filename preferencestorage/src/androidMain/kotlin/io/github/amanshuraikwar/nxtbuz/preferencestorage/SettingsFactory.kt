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

    companion object {
        private const val PREFS_NAME = "io.github.amanshuraikwar.nxtbuz"
    }
}