package io.github.amanshuraikwar.nxtbuz.domain.busstop

import io.github.amanshuraikwar.nxtbuz.commonkmm.goinghome.DirectBus
import io.github.amanshuraikwar.nxtbuz.commonkmm.goinghome.DirectBusesResult
import io.github.amanshuraikwar.nxtbuz.commonkmm.goinghome.GoingHomeBusResult
import io.github.amanshuraikwar.nxtbuz.repository.BusArrivalRepository
import io.github.amanshuraikwar.nxtbuz.repository.BusRouteRepository
import io.github.amanshuraikwar.nxtbuz.repository.BusStopRepository
import io.github.amanshuraikwar.nxtbuz.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

open class GetNearbyGoingHomeBusesUseCase(
    private val userRepository: UserRepository,
    private val busStopRepository: BusStopRepository,
    private val busRouteRepository: BusRouteRepository,
    private val busArrivalRepository: BusArrivalRepository,
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
                    ?.let { busStop ->
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
                    ?: run {
                        emit(GoingHomeBusResult.HomeBusStopNotSet)
                        return@flow
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

            val goingHomeBusList = mutableListOf<DirectBus>()
            val busServiceNumberSet = mutableSetOf<String>()

            var comparisonCount = 0F

            homeBusStopList.forEach { homeBusStop ->
                sourceBusStopList.forEach busStop@{ busStop ->
                    when (
                        val directBusesResult = busStopRepository.getDirectBuses(
                            sourceBusStopCode = busStop.code,
                            destinationBusStopCode = homeBusStop.code
                        )
                    ) {
                        DirectBusesResult.NoDirectBuses -> {
                            // do nothing
                        }
                        DirectBusesResult.NotCachedYet -> {
                            val directBusList = mutableListOf<DirectBus>()

                            busStop.operatingBusList.forEach operatingBus@{ bus ->
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

                                directBusList.add(
                                    DirectBus(
                                        sourceBusStopDescription = busStop.description,
                                        sourceBusStopCode = busStop.code,
                                        destinationBusStopDescription = homeBusStop.description,
                                        destinationBusStopCode = homeBusStop.code,
                                        busServiceNumber = bus.serviceNumber,
                                        stops = diffBusStopNumber,
                                        distance = distance
                                    )
                                )

                                if (busServiceNumberSet.contains(bus.serviceNumber)) {
                                    return@operatingBus
                                }

                                busServiceNumberSet.add(bus.serviceNumber)

                                val busOperating =
                                    busArrivalRepository
                                        .isBusOperating(
                                            busStopCode = busStop.code,
                                            busServiceNumber = bus.serviceNumber
                                        )

                                // only add bus if its operating
                                if (busOperating) {
                                    goingHomeBusList.add(
                                        DirectBus(
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
                            }

                            if (directBusList.isEmpty()) {
                                busStopRepository.setNoDirectBusesFor(
                                    sourceBusStopCode = busStop.code,
                                    destinationBusStopCode = homeBusStop.code
                                )
                            } else {
                                busStopRepository.setDirectBuses(
                                    directBusList
                                )
                            }
                        }
                        is DirectBusesResult.Success -> {
                            directBusesResult.directBusList.forEach directBus@{ directBus ->
                                if (busServiceNumberSet.contains(directBus.busServiceNumber)) {
                                    return@directBus
                                }

                                busServiceNumberSet.add(directBus.busServiceNumber)

                                val busOperating =
                                    busArrivalRepository
                                        .isBusOperating(
                                            busStopCode = directBus.sourceBusStopCode,
                                            busServiceNumber = directBus.busServiceNumber
                                        )

                                // only add bus if its operating
                                if (busOperating) {
                                    goingHomeBusList.add(
                                        DirectBus(
                                            sourceBusStopDescription = directBus.sourceBusStopDescription,
                                            sourceBusStopCode = directBus.sourceBusStopCode,
                                            destinationBusStopDescription = directBus.destinationBusStopDescription,
                                            destinationBusStopCode = directBus.destinationBusStopCode,
                                            busServiceNumber = directBus.busServiceNumber,
                                            stops = directBus.stops,
                                            distance = directBus.distance
                                        )
                                    )
                                }
                            }
                        }
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
                if (goingHomeBusList.isNotEmpty()) {
                    GoingHomeBusResult
                        .Success(
                            directBuses = goingHomeBusList
                        )
                } else {
                    GoingHomeBusResult.NoBusesGoingHome(
                        homeBusStopCode = homeBusStopCode,
                        homeBusStopDescription = homeBusStopList[0].description
                    )
                }
            )
        }
    }
}

