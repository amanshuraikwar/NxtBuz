package io.github.amanshuraikwar.nxtbuz.starred.data.di

import dagger.Module
import dagger.Provides
import io.github.amanshuraikwar.nxtbuz.common.model.StarToggleState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Named
import javax.inject.Singleton

@FlowPreview
@ExperimentalCoroutinesApi
@Module
class StarredBusArrivalProvides {

    @Provides
    @Singleton
    @Named("starToggleState")
    fun a(): MutableStateFlow<StarToggleState> {
        return MutableStateFlow(
            StarToggleState(
                "",
                "",
                false
            )
        )
    }
}