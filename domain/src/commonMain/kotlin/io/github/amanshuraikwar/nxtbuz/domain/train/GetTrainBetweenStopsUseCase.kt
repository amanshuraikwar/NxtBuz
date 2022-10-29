package io.github.amanshuraikwar.nxtbuz.domain.train

import io.github.amanshuraikwar.nxtbuz.commonkmm.train.NextTrainBetweenStopsOutput
import io.github.amanshuraikwar.nxtbuz.repository.TrainStopRepository

open class GetTrainBetweenStopsUseCase constructor(
    private val trainStopRepositories: List<TrainStopRepository>
) {
    suspend operator fun invoke(
        fromTrainStopCode: String,
        toTrainStopCode: String
    ): NextTrainBetweenStopsOutput {
        if (fromTrainStopCode == toTrainStopCode) {
            return NextTrainBetweenStopsOutput.TrainStopsAreSame
        }

        for (repo in trainStopRepositories) {
            if (repo.supportsTrain(trainCode = fromTrainStopCode)) {
                val trainDetails = repo.getNextTrainBetween(
                    fromTrainStopCode = fromTrainStopCode,
                    toTrainStopCode = toTrainStopCode
                )

                return if (trainDetails == null) {
                    NextTrainBetweenStopsOutput.NoTrainFound
                } else {
                    NextTrainBetweenStopsOutput.TrainFound(
                        details = trainDetails
                    )
                }
            }
        }

        return NextTrainBetweenStopsOutput.NoTrainFound
    }
}