package io.github.amanshuraikwar.howmuch.data.di

import dagger.Binds
import dagger.Module
import io.github.amanshuraikwar.howmuch.data.prefs.PreferenceStorage
import io.github.amanshuraikwar.howmuch.data.prefs.SharedPreferenceStorage

@Module
abstract class DataModuleBinds {

    @Binds
    abstract fun a(
        storage: SharedPreferenceStorage
    ): PreferenceStorage
}
