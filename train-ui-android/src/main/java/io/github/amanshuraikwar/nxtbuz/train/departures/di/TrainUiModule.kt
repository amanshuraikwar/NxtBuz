package io.github.amanshuraikwar.nxtbuz.train.departures.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.github.amanshuraikwar.nxtbuz.common.di.ViewModelKey
import io.github.amanshuraikwar.nxtbuz.train.departures.TrainDeparturesViewModel

@Module
abstract class TrainUiModule {

    @Binds
    @IntoMap
    @ViewModelKey(TrainDeparturesViewModel::class)
    abstract fun provideTrainDeparturesViewModel(a: TrainDeparturesViewModel): ViewModel
}
