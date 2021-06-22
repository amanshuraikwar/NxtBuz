package io.github.amanshuraikwar.nxtbuz.ui.di

import android.app.Activity
import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.github.amanshuraikwar.nxtbuz.common.di.ViewModelKey
import io.github.amanshuraikwar.nxtbuz.ui.MainActivity
import io.github.amanshuraikwar.nxtbuz.ui.MainViewModel

@Module
internal abstract class MainModule {

    @Binds
    internal abstract fun activity(a: MainActivity): Activity

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    internal abstract fun mainViewModel(a: MainViewModel): ViewModel
}
