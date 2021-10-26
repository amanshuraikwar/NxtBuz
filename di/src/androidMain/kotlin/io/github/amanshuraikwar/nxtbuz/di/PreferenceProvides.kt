package io.github.amanshuraikwar.nxtbuz.di

import io.github.amanshuraikwar.nxtbuz.preferencestorage.PreferenceStorage
import io.github.amanshuraikwar.nxtbuz.preferencestorage.SettingsFactory

actual object PreferenceProvides {
    actual fun providePreferenceStorage(
        preferenceStorageParams: PreferenceStorageParams
    ): PreferenceStorage {
        return SettingsFactory(preferenceStorageParams.context).createPreferenceStorage()
    }
}