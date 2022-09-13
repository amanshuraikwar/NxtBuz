package io.github.amanshuraikwar.nxtbuz.domain.train

import io.github.amanshuraikwar.nxtbuz.commonkmm.train.TrainStop
import io.github.amanshuraikwar.nxtbuz.repository.TrainStopRepository

open class GetTrainStopsUseCase constructor(
    private val trainStopRepositories: List<TrainStopRepository>
) {
    suspend operator fun invoke(lat: Double, lon: Double, limit: Int): List<TrainStop> {
        val trainStops = mutableListOf<TrainStop>()

        for (repo in trainStopRepositories) {
            if (repo.supportsLocation(lat, lon)) {
                trainStops.addAll(
                    repo.getCloseTrainStops(
                        lat = lat,
                        lng = lon,
                        maxStops = limit,
                        maxDistanceMetres = 5_000
                    )
                )
            }
        }

        return trainStops
    }
}