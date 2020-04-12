package io.github.amanshuraikwar.nxtbuz.data.prefs.di

import dagger.Binds
import dagger.Module
import io.github.amanshuraikwar.nxtbuz.data.prefs.PreferenceStorage
import io.github.amanshuraikwar.nxtbuz.data.prefs.SharedPreferenceStorage

@Module
abstract class PrefsModuleBinds {

    @Binds
    abstract fun a(
        storage: SharedPreferenceStorage
    ): PreferenceStorage
}