package io.github.amanshuraikwar.nxtbuz.ui.starred.di

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.Module
import dagger.Provides
import io.github.amanshuraikwar.nxtbuz.data.busstop.model.BusStop
import javax.inject.Named
import io.github.amanshuraikwar.nxtbuz.di.ActivityScoped
import io.github.amanshuraikwar.nxtbuz.domain.result.Event
import io.github.amanshuraikwar.nxtbuz.util.asEvent

@Module
class StarredBusArrivalsProvides {

     val a = MutableLiveData<Pair<BusStop, String>>()

    @Provides
    @ActivityScoped
    @Named("starred-bus-arrival-removed")
    fun starredBusArrivalRemoved(): MutableLiveData<Pair<BusStop, String>> = a

    @Provides
    @ActivityScoped
    @Named("starred-bus-arrival-removed-event")
    fun starredBusArrivalRemovedEvent(): LiveData<Event<Pair<BusStop, String>>> = a.asEvent()
}