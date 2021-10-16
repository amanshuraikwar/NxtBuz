package io.github.amanshuraikwar.nxtbuz.starred

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.github.amanshuraikwar.nxtbuz.common.di.ViewModelKey

@Module
abstract class StarredModule {
    @Binds
    @IntoMap
    @ViewModelKey(StarredViewModel::class)
    internal abstract fun d(a: StarredViewModel): ViewModel

}
