package io.github.amanshuraikwar.nxtbuz.data.busarrival.delegates

import android.util.Log
import io.github.amanshuraikwar.nxtbuz.common.model.*
import io.github.amanshuraikwar.nxtbuz.common.model.arrival.ArrivingBus
import io.github.amanshuraikwar.nxtbuz.common.model.arrival.BusArrivals
import io.github.amanshuraikwar.nxtbuz.common.model.arrival.BusStopArrival
import io.github.amanshuraikwar.nxtbuz.common.util.getArrivalTimeStr
import io.github.amanshuraikwar.nxtbuz.data.room.dao.BusOperatorDao
import io.github.amanshuraikwar.nxtbuz.data.room.dao.BusRouteDao
import io.github.amanshuraikwar.nxtbuz.data.room.dao.BusStopDao
import io.github.amanshuraikwar.nxtbuz.data.room.dao.StarredBusStopsDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Named

//class BusArrivalStateFlowDelegateImpl @Inject constructor(
//    private val starredBusStopsDao: StarredBusStopsDao,
//    private val busOperatorDao: BusOperatorDao,
//    private val busStopDao: BusStopDao,
//    private val busRouteDao: BusRouteDao,
//    @Named("busArrivalStateFlow") _busArrivalStateFlow: MutableStateFlow<BusArrivalsState>,
//) : BusArrivalStateFlowDelegate {
//
//    private val busArrivalStateFlow: MutableStateFlow<BusArrivalsState> = _busArrivalStateFlow
//
//    override fun getBusArrivalsFlow(
//        busStopCode: String
//    ): Flow<List<BusStopArrival>> {
//        return busArrivalStateFlow
//            .filter { it.busStopCode == busStopCode }
//            .map { it.busArrivalEntityList }
//            .filter { it.isNotEmpty() }
//            .map { busArrivalEntityList ->
//
//                val starredBusServiceNumberSet =
//                    starredBusStopsDao
//                        .findByBusStopCode(busStopCode)
//                        .map { it.busServiceNumber }
//                        .toSet()
//
//                val busArrivalList = mutableListOf<BusStopArrival>()
//                val errorBusArrivalList = mutableListOf<BusStopArrival>()
//
//                busArrivalEntityList
//                    .groupBy { it.busServiceNumber }
//                    .forEach forEachBusService@{ (busServiceNumber, busArrivalEntityList) ->
//
//                        if (busArrivalEntityList.size != 3) {
//                            throw Exception(
//                                "Bus arrival entity list size is not 3 for " +
//                                        "bus service number $busServiceNumber at " +
//                                        "bus stop code $busStopCode"
//                            )
//                        }
//
//                        val (_, _, direction, stopSequence, distance) =
//                            busRouteDao
//                                .findByBusServiceNumberAndBusStopCode(
//                                    busServiceNumber,
//                                    busStopCode
//                                )
//                                .takeIf { it.isNotEmpty() }
//                                ?.get(0)
//                                ?: throw Exception(
//                                    "No bus route row found for service number " +
//                                            "$busServiceNumber at bus stop code " +
//                                            "$busStopCode in local DB."
//                                )
//
//                        // if the status if first arriving bus is not arriving
//                        // we assume the rest of them are also not arriving
//
//                        if (busArrivalEntityList[0].busArrivalStatus
//                            == BusArrivalStatus.NOT_OPERATING
//                        ) {
//                            errorBusArrivalList.add(
//                                BusStopArrival(
//                                    busServiceNumber = busServiceNumber,
//                                    operator = "N/A",
//                                    originBusStopDescription = "N/A",
//                                    destinationBusStopDescription = "N/A",
//                                    direction = direction,
//                                    stopSequence = stopSequence,
//                                    distance = distance,
//                                    starred = starredBusServiceNumberSet.contains(busServiceNumber),
//                                    BusArrivals.NotOperating
//                                )
//                            )
//
//                            return@forEachBusService
//                        }
//
//                        if (busArrivalEntityList[0].busArrivalStatus
//                            == BusArrivalStatus.NO_DATA
//                        ) {
//                            errorBusArrivalList.add(
//                                BusStopArrival(
//                                    busServiceNumber = busServiceNumber,
//                                    operator = "N/A",
//                                    originBusStopDescription = "N/A",
//                                    destinationBusStopDescription = "N/A",
//                                    direction = direction,
//                                    stopSequence = stopSequence,
//                                    distance = distance,
//                                    starred = starredBusServiceNumberSet.contains(busServiceNumber),
//                                    BusArrivals.DataNotAvailable
//                                )
//                            )
//
//                            return@forEachBusService
//                        }
//
//                        if (busArrivalEntityList[0].busArrivalStatus
//                            == BusArrivalStatus.ARRIVING
//                        ) {
//
//                            val operator =
//                                busOperatorDao
//                                    .findByBusServiceNumberAndBusStopCode(
//                                        busServiceNumber = busServiceNumber,
//                                        busStopCode = busStopCode
//                                    )
//                                    .firstOrNull()
//                                    ?.operator
//                                    ?: throw Exception(
//                                        "No operator row found for bus service $busServiceNumber " +
//                                                "at bus stop $busStopCode even if the first row " +
//                                                "BusArrivalEntity is ARRIVING."
//                                    )
//
//                            val originArrivingBusStop =
//                                busStopDao
//                                    .findByCode(busArrivalEntityList[0].originCode)
//                                    .firstOrNull()
//                                    ?.let { busStopEntity ->
//                                        ArrivingBusStop(
//                                            busStopCode = busStopEntity.code,
//                                            roadName = busStopEntity.roadName,
//                                            busStopDescription = busStopEntity.description
//                                        )
//                                    }
//                                    ?: throw Exception(
//                                        "No bus stop row found for stop code " +
//                                                "${busArrivalEntityList[0].originCode} (origin) " +
//                                                "in local DB."
//                                    )
//
//                            val destinationArrivingBusStop =
//                                busStopDao
//                                    .findByCode(busArrivalEntityList[0].destinationCode)
//                                    .firstOrNull()
//                                    ?.let { busStopEntity ->
//                                        ArrivingBusStop(
//                                            busStopCode = busStopEntity.code,
//                                            roadName = busStopEntity.roadName,
//                                            busStopDescription = busStopEntity.description
//                                        )
//                                    }
//                                    ?: throw Exception(
//                                        "No bus stop row found for stop code " +
//                                                "${busArrivalEntityList[0].destinationCode} (destination) " +
//                                                "in local DB."
//                                    )
//
//                            val nextArrivingBus = ArrivingBus(
//                                origin = originArrivingBusStop,
//                                destination = destinationArrivingBusStop,
//                                arrival = busArrivalEntityList[0].getArrivalTimeStr(),
//                                latitude = busArrivalEntityList[0].latitude,
//                                longitude = busArrivalEntityList[0].longitude,
//                                visitNumber = busArrivalEntityList[0].visitNumber,
//                                load = busArrivalEntityList[0].load,
//                                feature = busArrivalEntityList[0].feature,
//                                type = busArrivalEntityList[0].type
//                            )
//
//                            val followingArrivingBusList =
//                                mutableListOf<ArrivingBus>()
//
//                            if (busArrivalEntityList[1].busArrivalStatus
//                                == BusArrivalStatus.ARRIVING
//                            ) {
//                                followingArrivingBusList.add(
//                                    ArrivingBus(
//                                        origin = originArrivingBusStop,
//                                        destination = destinationArrivingBusStop,
//                                        arrival = busArrivalEntityList[1].getArrivalTimeStr(),
//                                        latitude = busArrivalEntityList[1].latitude,
//                                        longitude = busArrivalEntityList[1].longitude,
//                                        visitNumber = busArrivalEntityList[1].visitNumber,
//                                        load = busArrivalEntityList[1].load,
//                                        feature = busArrivalEntityList[1].feature,
//                                        type = busArrivalEntityList[1].type
//                                    )
//                                )
//                            }
//
//                            if (busArrivalEntityList[2].busArrivalStatus
//                                == BusArrivalStatus.ARRIVING
//                            ) {
//                                followingArrivingBusList.add(
//                                    ArrivingBus(
//                                        origin = originArrivingBusStop,
//                                        destination = destinationArrivingBusStop,
//                                        arrival = busArrivalEntityList[2].getArrivalTimeStr(),
//                                        latitude = busArrivalEntityList[2].latitude,
//                                        longitude = busArrivalEntityList[2].longitude,
//                                        visitNumber = busArrivalEntityList[2].visitNumber,
//                                        load = busArrivalEntityList[2].load,
//                                        feature = busArrivalEntityList[2].feature,
//                                        type = busArrivalEntityList[2].type
//                                    )
//                                )
//                            }
//
//                            busArrivalList.add(
//                                BusStopArrival(
//                                    busServiceNumber = busServiceNumber,
//                                    operator = operator,
//                                    originBusStopDescription = originArrivingBusStop.busStopDescription,
//                                    destinationBusStopDescription = destinationArrivingBusStop.busStopDescription,
//                                    direction = direction,
//                                    stopSequence = stopSequence,
//                                    distance = distance,
//                                    starred = starredBusServiceNumberSet.contains(busServiceNumber),
//                                    BusArrivals.Arriving(
//                                        nextArrivingBus,
//                                        followingArrivingBusList
//                                    )
//                                )
//                            )
//
//                            return@forEachBusService
//                        }
//
//                        Log.wtf(
//                            TAG,
//                            "getBusArrivalsFlow: Bus Arrival Status is " +
//                                    "neither ARRIVING nor NOT_OPERATING or NO_DATA " +
//                                    "for bus service $busServiceNumber at bus stop $busStopCode",
//                        )
//                    }
//
//                busArrivalList.addAll(errorBusArrivalList)
//                busArrivalList
//            }
//    }
//
//    companion object {
//        private const val TAG = "BusArrivalStateFlowDele"
//    }
//}