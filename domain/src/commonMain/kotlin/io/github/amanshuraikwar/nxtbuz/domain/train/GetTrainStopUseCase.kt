package io.github.amanshuraikwar.nxtbuz.domain.train

import io.github.amanshuraikwar.nxtbuz.commonkmm.train.TrainStop
import io.github.amanshuraikwar.nxtbuz.repository.TrainStopRepository

open class GetTrainStopUseCase constructor(
    private val trainStopRepositories: List<TrainStopRepository>
) {
    suspend operator fun invoke(trainStopCode: String): TrainStop? {
        for (repo in trainStopRepositories) {
            repo.getTrainStop(code = trainStopCode)?.let {
                return it
            }
        }
        return null
    }
}