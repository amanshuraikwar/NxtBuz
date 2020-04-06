package io.github.amanshuraikwar.howmuch.ui.search

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.github.amanshuraikwar.howmuch.di.ViewModelKey

@Module
internal abstract class SearchModule {

    @Binds
    @IntoMap
    @ViewModelKey(SearchViewModel::class)
    internal abstract fun a(a: SearchViewModel): ViewModel

    @Binds
    internal abstract fun b(a: SearchActivity): AppCompatActivity
}
