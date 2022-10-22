package io.github.amanshuraikwar.nxtbuz.domain.train

import io.github.amanshuraikwar.nxtbuz.commonkmm.train.TrainDetails
import io.github.amanshuraikwar.nxtbuz.repository.TrainStopRepository

open class GetTrainBetweenStopsUseCase constructor(
    private val trainStopRepositories: List<TrainStopRepository>
) {
    suspend operator fun invoke(
        fromTrainStopCode: String,
        toTrainStopCode: String
    ): List<TrainDetails> {
        for (repo in trainStopRepositories) {
            if (repo.supportsTrain(trainCode = fromTrainStopCode)) {
                return repo.getTrainsBetween(
                    fromTrainStopCode = fromTrainStopCode,
                    toTrainStopCode = toTrainStopCode
                )
            }
        }
        return emptyList()
    }
}