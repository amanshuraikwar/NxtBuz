package io.github.amanshuraikwar.nxtbuz.ui.main

import androidx.lifecycle.MutableLiveData
import dagger.Module
import dagger.Provides
import io.github.amanshuraikwar.nxtbuz.di.ActivityScoped
import javax.inject.Named

@Module
internal class BackPressedProvides {
    @Provides
    @Named("onBackPressed")
    @ActivityScoped
    fun onBackPressed(): MutableLiveData<Unit> {
        return MutableLiveData<Unit>()
    }
}