package io.github.amanshuraikwar.nxtbuz.domain.busstop

import io.github.amanshuraikwar.nxtbuz.repository.BusRouteRepository
import io.github.amanshuraikwar.nxtbuz.repository.BusStopRepository
import io.github.amanshuraikwar.nxtbuz.commonkmm.goinghome.GoingHomeBus
import io.github.amanshuraikwar.nxtbuz.commonkmm.goinghome.GoingHomeBusResult
import io.github.amanshuraikwar.nxtbuz.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

open class GetNearbyGoingHomeBusesUseCase(
    private val userRepository: UserRepository,
    private val busStopRepository: BusStopRepository,
    private val busRouteRepository: BusRouteRepository,
) {
    operator fun invoke(
        lat: Double,
        lng: Double,
    ): Flow<GoingHomeBusResult> {
        return flow {
            emit(
                GoingHomeBusResult.Processing(
                    progress = 0,
                    "Getting home bus stop..."
                )
            )

            val homeBusStopCode =
                userRepository.getHomeBusStopCode()
                    ?: run {
                        emit(GoingHomeBusResult.HomeBusStopNotSet)
                        return@flow
                    }

            emit(
                GoingHomeBusResult.Processing(
                    progress = 0,
                    "Analysing bus stops..."
                )
            )

            val homeBusStopList =
                busStopRepository
                    .getBusStop(homeBusStopCode)
                    .let { busStop ->
                        mutableListOf(busStop)
                            .apply {
                                addAll(
                                    busStopRepository
                                        .getCloseBusStops(
                                            latitude = busStop.latitude,
                                            longitude = busStop.longitude,
                                            limit = 5
                                        )
                                )
                            }
                    }

            val sourceBusStopList = busStopRepository.getCloseBusStops(
                latitude = lat,
                longitude = lng,
                5
            )

            if (sourceBusStopList.isEmpty()) {
                emit(GoingHomeBusResult.NoBusStopsNearby)
                return@flow
            }

            // if home and nearby bus stops have something in common
            // we are too close to suggest
            homeBusStopList.forEach { x ->
                sourceBusStopList.forEach { y ->
                    if (x.code == y.code) {
                        emit(
                            GoingHomeBusResult.TooCloseToHome(
                                homeBusStopCode = homeBusStopList[0].code,
                                homeBusStopDescription = homeBusStopList[0].description
                            )
                        )
                        return@flow
                    }
                }
            }

            val totalComparisons = (homeBusStopList.size * sourceBusStopList.size).toFloat()

            emit(
                GoingHomeBusResult.Processing(
                    progress = 10,
                    """
                        Analysing bus stops (0/${totalComparisons.toInt()})...
                    """.trimIndent()
                )
            )

            val goingHomeBusList = mutableListOf<GoingHomeBus>()
            val busServiceNumberSet = mutableSetOf<String>()

            var comparisonCount = 0F

            homeBusStopList.forEach { homeBusStop ->
                sourceBusStopList.forEach busStop@{ busStop ->
                    busStop.operatingBusList.forEach operatingBus@{ bus ->
                        if (busServiceNumberSet.contains(bus.serviceNumber)) {
                            return@operatingBus
                        }

                        val busRoute = busRouteRepository.getBusRoute(
                            busServiceNumber = bus.serviceNumber,
                            busStopCode = busStop.code
                        )

                        val homeBusStopNode =
                            busRoute.busRouteNodeList
                                .find {
                                    it.busStopCode == homeBusStop.code
                                }
                                ?: return@operatingBus

                        val busStopNode =
                            busRoute.busRouteNodeList
                                .find { busRouteNode ->
                                    busRouteNode.busStopCode == busStop.code
                                }
                                ?: return@operatingBus

                        if (homeBusStopNode.direction != busStopNode.direction) {
                            return@operatingBus
                        }

                        if (homeBusStopNode.stopSequence < busStopNode.stopSequence) {
                            return@operatingBus
                        }

                        val diffBusStopNumber =
                            homeBusStopNode.stopSequence - busStopNode.stopSequence
                        val distance = homeBusStopNode.distance - busStopNode.distance

                        busServiceNumberSet.add(bus.serviceNumber)

                        goingHomeBusList.add(
                            GoingHomeBus(
                                sourceBusStopDescription = busStop.description,
                                sourceBusStopCode = busStop.code,
                                destinationBusStopDescription = homeBusStop.description,
                                destinationBusStopCode = homeBusStop.code,
                                busServiceNumber = bus.serviceNumber,
                                stops = diffBusStopNumber,
                                distance = distance
                            )
                        )
                    }

                    emit(
                        GoingHomeBusResult.Processing(
                            progress = 10 + ((++comparisonCount / totalComparisons) * 90).toInt(),
                            """
                                Analysing bus stops (${comparisonCount.toInt()}/${totalComparisons.toInt()})...
                            """.trimIndent()
                        )
                    )
                }
            }

            emit(
                GoingHomeBusResult
                    .Success(
                        goingHomeBuses = goingHomeBusList
                    )
            )
        }
    }
}

