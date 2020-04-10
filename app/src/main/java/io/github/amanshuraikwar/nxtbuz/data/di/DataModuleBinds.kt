package io.github.amanshuraikwar.nxtbuz.data.di

import dagger.Binds
import dagger.Module
import io.github.amanshuraikwar.nxtbuz.data.prefs.PreferenceStorage
import io.github.amanshuraikwar.nxtbuz.data.prefs.SharedPreferenceStorage

@Module
abstract class DataModuleBinds {

    @Binds
    abstract fun a(
        storage: SharedPreferenceStorage
    ): PreferenceStorage
}
