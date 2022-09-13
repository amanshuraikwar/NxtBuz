package io.github.amanshuraikwar.nxtbuz.domain.train

import io.github.amanshuraikwar.nxtbuz.commonkmm.train.TrainDeparture
import io.github.amanshuraikwar.nxtbuz.repository.TrainStopRepository

open class GetTrainStopDeparturesUseCase constructor(
    private val trainStopRepositories: List<TrainStopRepository>
) {
    suspend operator fun invoke(trainStopCode: String): List<TrainDeparture> {
        for (repo in trainStopRepositories) {
            if (repo.containsStop(code = trainStopCode)) {
                return repo.getTrainDepartures(trainStopCode = trainStopCode)
            }
        }
        return emptyList()
    }
}