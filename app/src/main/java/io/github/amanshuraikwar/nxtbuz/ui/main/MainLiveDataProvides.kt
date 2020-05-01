package io.github.amanshuraikwar.nxtbuz.ui.main

import androidx.lifecycle.MutableLiveData
import dagger.Module
import dagger.Provides
import io.github.amanshuraikwar.multiitemadapter.RecyclerViewListItem
import io.github.amanshuraikwar.nxtbuz.di.ActivityScoped
import io.github.amanshuraikwar.nxtbuz.ui.main.fragment.Loading
import javax.inject.Named

@Module
internal class MainLiveDataProvides {

    @Provides
    @Named("onBackPressed")
    @ActivityScoped
    fun a(): MutableLiveData<Unit> {
        return MutableLiveData<Unit>()
    }

    @Provides
    @Named("listItems")
    @ActivityScoped
    fun b(): MutableLiveData<MutableList<RecyclerViewListItem>> {
        return MutableLiveData<MutableList<RecyclerViewListItem>>()
    }

    @Provides
    @Named("loading")
    @ActivityScoped
    fun c(): MutableLiveData<Loading> {
        return MutableLiveData<Loading>()
    }
}