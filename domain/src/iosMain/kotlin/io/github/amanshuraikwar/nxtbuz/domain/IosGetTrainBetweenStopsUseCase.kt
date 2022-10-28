package io.github.amanshuraikwar.nxtbuz.domain

import io.github.amanshuraikwar.nxtbuz.commonkmm.train.TrainDetails
import io.github.amanshuraikwar.nxtbuz.domain.model.IosResult
import io.github.amanshuraikwar.nxtbuz.domain.train.GetTrainBetweenStopsUseCase
import io.github.amanshuraikwar.nxtbuz.repository.TrainStopRepository

open class IosGetTrainBetweenStopsUseCase constructor(
    trainStopRepository: TrainStopRepository
) : GetTrainBetweenStopsUseCase(listOf(trainStopRepository)) {
    fun invoke1(
        fromTrainStopCode: String,
        toTrainStopCode: String,
        callback: (IosResult<List<TrainDetails>>) -> Unit
    ) {
        callback from {
            invoke(
                fromTrainStopCode = fromTrainStopCode,
                toTrainStopCode = toTrainStopCode
            )
        }
    }
}