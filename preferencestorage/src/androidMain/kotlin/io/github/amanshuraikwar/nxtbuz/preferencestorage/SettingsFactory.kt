package io.github.amanshuraikwar.nxtbuz.preferencestorage

import android.content.Context
import com.russhwolf.settings.AndroidSettings
import com.russhwolf.settings.Settings

actual class SettingsFactory(
    private val context: Context,
    private val name: String? = null
) {
    actual fun createPreferenceStorage(): PreferenceStorage {
        return PreferenceStorageImpl {
            AndroidSettings.Factory(context)
                .create(
                    PREFS_NAME + (name?.let { ".$it" } ?: "")
                )
        }
    }

    companion object {
        private const val PREFS_NAME = "io.github.amanshuraikwar.nxtbuz"
    }

    actual fun createSettings(): Settings {
        return AndroidSettings.Factory(context)
            .create(
                PREFS_NAME + (name?.let { ".$it" } ?: "")
            )
    }
}