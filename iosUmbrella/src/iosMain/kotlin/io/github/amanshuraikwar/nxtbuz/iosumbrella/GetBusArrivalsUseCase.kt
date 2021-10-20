package io.github.amanshuraikwar.nxtbuz.iosumbrella

import co.touchlab.stately.freeze
import io.github.amanshuraikwar.nxtbuz.repository.BusArrivalRepository
import io.github.amanshuraikwar.nxtbuz.commonkmm.arrival.BusStopArrival
import io.github.amanshuraikwar.nxtbuz.commonkmm.exception.IllegalDbStateException
import io.github.amanshuraikwar.nxtbuz.commonkmm.starred.StarredBusService
import io.github.amanshuraikwar.nxtbuz.iosumbrella.model.IosBusStopArrival
import io.github.amanshuraikwar.nxtbuz.iosumbrella.model.IosBusStopArrivalOutput
import io.github.amanshuraikwar.nxtbuz.iosumbrella.model.IosResult
import io.github.amanshuraikwar.nxtbuz.repository.StarredBusArrivalRepository
import io.ktor.utils.io.errors.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class GetBusArrivalsUseCase constructor(
    private val busArrivalRepository: BusArrivalRepository,
    private val starredBusArrivalRepository: StarredBusArrivalRepository
) {
    suspend operator fun invoke(busStopCode: String): List<BusStopArrival> {
        return busArrivalRepository.getBusArrivals(busStopCode)
    }

    operator fun invoke(busStopCode: String, callback: (IosBusStopArrivalOutput) -> Unit) {
        IosDataCoroutineScopeProvider.coroutineScope.launch(
            CoroutineExceptionHandler { _, th ->
                callback(
                    IosBusStopArrivalOutput.Error(
                        th.message ?: "Something went wrong!"
                    ).freeze()
                )
            }
        ) {
            val starredBusServiceNumberSet =
                (starredBusArrivalRepository.getStarredBusServices() as List<StarredBusService>)
                    .filter {
                        it.busStopCode == busStopCode
                    }
                    .map {
                        it.busServiceNumber
                    }
                    .toSet()

            callback(
                IosBusStopArrivalOutput.Success(
                    invoke(busStopCode).map { busStopArrival ->
                        IosBusStopArrival(
                            busStopCode = busStopArrival.busStopCode,
                            busServiceNumber = busStopArrival.busServiceNumber,
                            operator = busStopArrival.operator,
                            direction = busStopArrival.direction,
                            stopSequence = busStopArrival.stopSequence,
                            distance = busStopArrival.distance,
                            busArrivals = busStopArrival.busArrivals,
                            starred = starredBusServiceNumberSet
                                .contains(busStopArrival.busServiceNumber)
                        )
                    }
                ).freeze()
            )
        }
    }

    suspend operator fun invoke(busStopCode: String, busServiceNumber: String): BusStopArrival {
        return busArrivalRepository.getBusArrivals(busStopCode, busServiceNumber)
    }

    operator fun invoke(
        busStopCode: String,
        busServiceNumber: String,
        callback: (IosResult<BusStopArrival>) -> Unit
    ) {
        IosDataCoroutineScopeProvider.coroutineScope.launch(
            CoroutineExceptionHandler { _, th ->
                println(th)
                callback(
                    IosResult.Error(
                        // TODO-amanshuraikwar (12 Oct 2021 11:24:30 PM): handle gracefully
                        when (th) {
                            is IllegalDbStateException -> {
                                "IllegalDbStateException"
                            }
                            is IOException -> {
                                "IOException"
                            }
                            else -> {
                                "Something went wrong."
                            }
                        }
                    )
                )
            }
        ) {
            callback(
                IosResult.Success(
                    busArrivalRepository.getBusArrivals(
                        busStopCode = busStopCode,
                        busServiceNumber = busServiceNumber
                    )
                )
            )
        }
    }
}