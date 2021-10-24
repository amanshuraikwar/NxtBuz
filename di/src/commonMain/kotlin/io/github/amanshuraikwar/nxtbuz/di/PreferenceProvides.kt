package io.github.amanshuraikwar.nxtbuz.di

import io.github.amanshuraikwar.nxtbuz.preferencestorage.PreferenceStorage

expect object PreferenceProvides {
    fun providePreferenceStorage(
        preferenceStorageParams: PreferenceStorageParams
    ): PreferenceStorage
}