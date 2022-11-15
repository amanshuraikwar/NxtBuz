package io.github.amanshuraikwar.nxtbuz.domain

import io.github.amanshuraikwar.nxtbuz.commonkmm.train.TrainStop
import io.github.amanshuraikwar.nxtbuz.domain.model.IosResult
import io.github.amanshuraikwar.nxtbuz.domain.train.SearchTrainStopsUseCase
import io.github.amanshuraikwar.nxtbuz.repository.TrainStopRepository

open class IosSearchTrainStopsUseCase constructor(
    trainStopRepository: TrainStopRepository
) : SearchTrainStopsUseCase(listOf(trainStopRepository)) {
    fun invokeCallback(
        trainStopName: String,
        callback: (IosResult<List<TrainStop>>) -> Unit
    ) {
        callback from {
            invoke(trainStopName)
        }
    }
}