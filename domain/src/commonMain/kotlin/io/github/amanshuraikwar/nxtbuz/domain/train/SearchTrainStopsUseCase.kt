package io.github.amanshuraikwar.nxtbuz.domain.train

import io.github.amanshuraikwar.nxtbuz.commonkmm.train.TrainStop
import io.github.amanshuraikwar.nxtbuz.repository.TrainStopRepository

open class SearchTrainStopsUseCase constructor(
    private val trainStopRepositories: List<TrainStopRepository>
) {
    suspend operator fun invoke(trainStopName: String): List<TrainStop> {
        for (repo in trainStopRepositories) {
            return repo.searchTrainStops(
                trainStopName = trainStopName
            )
        }

        return emptyList()
    }
}