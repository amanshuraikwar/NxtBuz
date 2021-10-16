package io.github.amanshuraikwar.nxtbuz.data.di

import android.content.Context
import dagger.Module
import dagger.Provides
import io.github.amanshuraikwar.nxtbuz.common.di.ApplicationContext
import io.github.amanshuraikwar.nxtbuz.preferencestorage.PreferenceStorage
import io.github.amanshuraikwar.nxtbuz.preferencestorage.SettingsFactory
import javax.inject.Singleton

@Module
class PreferenceProvides {
    @Singleton
    @Provides
    fun providePreferenceStorage(
        @ApplicationContext context: Context
    ): PreferenceStorage {
        return SettingsFactory(context).createPreferenceStorage()
    }
}