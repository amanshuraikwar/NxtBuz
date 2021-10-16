package io.github.amanshuraikwar.nxtbuz.iosumbrella

import io.github.amanshuraikwar.nxtbuz.busroutedata.BusRouteRepository
import io.github.amanshuraikwar.nxtbuz.busstopdata.BusStopRepository
import io.github.amanshuraikwar.nxtbuz.iosumbrella.model.IosResult
import io.github.amanshuraikwar.nxtbuz.userdata.UserRepository

class GetNearbyGoingHomeBusesUseCase(
    private val userRepository: UserRepository,
    private val busStopRepository: BusStopRepository,
    private val busRouteRepository: BusRouteRepository,
) {
    operator fun invoke(
        lat: Double,
        lng: Double,
        completion: (IosResult<List<GoingHomeBus>>) -> Unit
    ) {
        returnIosResult(
            completion
        ) {
            val homeBusStopCode = userRepository.getHomeBusStopCode()
                ?: return@returnIosResult emptyList()

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

            val busStopList = busStopRepository.getCloseBusStops(
                latitude = lat,
                longitude = lng,
                5
            )

            val goingHomeBusList = mutableListOf<GoingHomeBus>()
            val busServiceNumberSet = mutableSetOf<String>()

            homeBusStopList.forEach { homeBusStop ->
                busStopList.forEach busStop@{ busStop ->
                    if (busStop.code == homeBusStop.code) {
                        return@busStop
                    }

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
                                busServiceNumber = bus.serviceNumber,
                                stops = diffBusStopNumber,
                                distance = distance
                            )
                        )
                    }
                }
            }

            goingHomeBusList
        }
    }
}

data class GoingHomeBus(
    val sourceBusStopDescription: String,
    val sourceBusStopCode: String,
    val destinationBusStopDescription: String,
    val busServiceNumber: String,
    val stops: Int,
    val distance: Double
)