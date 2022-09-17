package io.github.amanshuraikwar.nxtbuz.domain.train

import io.github.amanshuraikwar.nxtbuz.commonkmm.train.TrainDetails
import io.github.amanshuraikwar.nxtbuz.repository.TrainStopRepository

open class GetTrainDetailsUseCase constructor(
    private val trainStopRepositories: List<TrainStopRepository>
) {
    suspend operator fun invoke(trainCode: String): TrainDetails? {
        for (repo in trainStopRepositories) {
            if (repo.supportsTrain(trainCode = trainCode)) {
                return repo.getTrainDetails(trainCode = trainCode)
            }
        }
        return null
    }
}