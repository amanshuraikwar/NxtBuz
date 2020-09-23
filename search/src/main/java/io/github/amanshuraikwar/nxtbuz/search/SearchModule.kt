package io.github.amanshuraikwar.nxtbuz.search

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.github.amanshuraikwar.nxtbuz.common.di.ViewModelKey

@Module
abstract class SearchModule {

    @Binds
    @IntoMap
    @ViewModelKey(SearchViewModel::class)
    internal abstract fun a(a: SearchViewModel): ViewModel

    @Binds
    internal abstract fun b(a: SearchActivity): AppCompatActivity
}
