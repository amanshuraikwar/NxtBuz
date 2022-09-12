package io.github.amanshuraikwar.nxtbuz.preferencestorage

import com.russhwolf.settings.Settings

expect class SettingsFactory {
    fun createPreferenceStorage(): PreferenceStorage

    fun createSettings(): Settings
}