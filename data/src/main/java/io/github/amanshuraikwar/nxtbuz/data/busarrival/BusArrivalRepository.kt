package io.github.amanshuraikwar.nxtbuz.data.busarrival

import io.github.amanshuraikwar.ltaapi.LtaApi
import io.github.amanshuraikwar.ltaapi.model.ArrivingBusItemDto
import io.github.amanshuraikwar.ltaapi.model.BusArrivalItemDto
import io.github.amanshuraikwar.nxtbuz.common.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.common.model.*
import io.github.amanshuraikwar.nxtbuz.common.model.arrival.ArrivingBus
import io.github.amanshuraikwar.nxtbuz.common.model.arrival.BusArrivals
import io.github.amanshuraikwar.nxtbuz.common.model.arrival.BusStopArrival
//import io.github.amanshuraikwar.nxtbuz.data.busarrival.delegates.BusArrivalStateFlowDelegate
import io.github.amanshuraikwar.nxtbuz.data.room.dao.BusRouteDao
import io.github.amanshuraikwar.nxtbuz.data.room.dao.BusStopDao
import io.github.amanshuraikwar.nxtbuz.data.room.dao.OperatingBusDao
import io.github.amanshuraikwar.nxtbuz.common.model.room.OperatingBusEntity
import io.github.amanshuraikwar.nxtbuz.data.room.dao.StarredBusStopsDao
import io.github.amanshuraikwar.nxtbuz.common.util.TimeUtil
import io.github.amanshuraikwar.nxtbuz.data.IllegalDbStateException
import kotlinx.coroutines.withContext
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.OffsetTime
import org.threeten.bp.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BusArrivalRepository @Inject constructor(
//    private val starredBusStopsDao: StarredBusStopsDao,
    private val busRouteDao: BusRouteDao,
    private val operatingBusDao: OperatingBusDao,
    private val busStopDao: BusStopDao,
//    private val busArrivalStateFlowDelegate: BusArrivalStateFlowDelegate,
    private val busApi: LtaApi,
    private val dispatcherProvider: CoroutinesDispatcherProvider
)/* : BusArrivalStateFlowDelegate by busArrivalStateFlowDelegate*/ {

    suspend fun getBusArrivals(busStopCode: String): List<BusStopArrival> =
        withContext(dispatcherProvider.io) {

//            val starredBusServiceNumberSet =
//                starredBusStopsDao
//                    .findByBusStopCode(busStopCode)
//                    .map { it.busServiceNumber }
//                    .toSet()
//
//            fun BusArrivalItemDto.isStarred() =
//                starredBusServiceNumberSet.contains(this.serviceNumber)
//
//            fun OperatingBusEntity.isStarred() =
//                starredBusServiceNumberSet.contains(this.busServiceNumber)

//            fun OperatingBusEntity.isStarred() =
//                starredBusServiceNumberSet.contains(this.busServiceNumber)

            val operatingBusServiceNumberMap =
                operatingBusDao
                    .findByBusStopCode(busStopCode)
                    .groupBy { it.busServiceNumber }
                    .mapValues { (_, v) -> v[0] }
                    .toMutableMap()

//            val busArrivalItemList = busApi.getBusArrivals(busStopCode).busArrivals

            val busStopArrivalList = mutableListOf<BusStopArrival>()

            //val busArrivalList =
            busApi.getBusArrivals(busStopCode)
                .busArrivals
                .forEach { busArrivalItemDto ->

                    if (
                        operatingBusServiceNumberMap.containsKey(busArrivalItemDto.serviceNumber)
                    ) {
                        operatingBusServiceNumberMap.remove(busArrivalItemDto.serviceNumber)
                        busStopArrivalList.add(
                            busArrivalItemDto.toBusArrival(
                                busStopCode = busStopCode
                            )
                        )
                    } else {
                        // bus service number returned from remote api is not in local db
                        // schedule DB update
                        // TODO-amanshuraikwar (26 May 2021 11:58:35 AM):
                    }

//                    if (!operatingBusServiceNumberMap.containsKey(busArrivalItemDto.serviceNumber)) {
//                        throw IllegalDbStateException(
//                            "No operating bus row found for service number " +
//                                    "${busArrivalItemDto.serviceNumber} and stop code " +
//                                    "$busStopCode in local DB."
//                        )
//                    }

//                    operatingBusServiceNumberMap.remove(busArrivalItem.serviceNumber)
//
//                    busArrivalItem.toBusArrival(
//                        busStopCode,
//                        busArrivalItem.isStarred()
//                    )
                }
            //.toMutableList()

            // add remaining buses as Arrivals.DataNotAvailable or Arrivals.NotOperating
            operatingBusServiceNumberMap.forEach { (_, operatingBusEntity) ->
                busStopArrivalList.add(
                    operatingBusEntity.toBusArrivalError(
                        //operatingBusEntity.isStarred()
                    )
                )
            }

            busStopArrivalList
        }

    suspend fun getBusArrivals(busStopCode: String, busServiceNumber: String): BusStopArrival =
        withContext(dispatcherProvider.io) {

//            val starredBusServiceNumberSet =
//                starredBusStopsDao
//                    .findByBusStopCode(busStopCode)
//                    .map { it.busServiceNumber }
//                    .toSet()
//
//            fun BusArrivalItemDto.isStarred() =
//                starredBusServiceNumberSet.contains(this.serviceNumber)
//
//            fun OperatingBusEntity.isStarred() =
//                starredBusServiceNumberSet.contains(this.busServiceNumber)

            val operatingBusEntity =
                operatingBusDao
                    .getOperatingBus(
                        busStopCode = busStopCode,
                        busServiceNumber = busServiceNumber
                    )
                    ?: run {
                        // bus service number returned from remote api is not in local db
                        // schedule DB update
                        // TODO-amanshuraikwar (26 May 2021 11:58:35 AM):
                        throw IllegalDbStateException(
                            "No operating bus row found for service number " +
                                    "$busServiceNumber and stop code " +
                                    "$busStopCode in local DB."
                        )
                    }

//            val operatingBusEntity =
//                operatingBusServiceNumberMap[busServiceNumber] ?: throw Exception(
//                    "No operating bus row found for service number " +
//                            "$busServiceNumber and stop code " +
//                            "$busStopCode in local DB."
//                )

            val busArrivalItemList =
                busApi.getBusArrivals(busStopCode, busServiceNumber).busArrivals

            if (busArrivalItemList.isNotEmpty()) {

                val busArrivalItem = busArrivalItemList[0]
                return@withContext busArrivalItem.toBusArrival(
                    busStopCode,
                    //busArrivalItem.isStarred()
                )

            } else {

                return@withContext operatingBusEntity.toBusArrivalError(
                    //operatingBusEntity.isStarred()
                )
            }
        }

    private suspend inline fun BusArrivalItemDto.toBusArrival(
        busStopCode: String,
    ): BusStopArrival {

        val (_, _, direction, stopSequence, distance) =
            busRouteDao.getBusRoute(busStopCode = busStopCode, busServiceNumber = serviceNumber)
//                .findByBusServiceNumberAndBusStopCode(
//                    serviceNumber,
//                    busStopCode
//                )
//                .takeIf { it.isNotEmpty() }
//                ?.get(0)
                ?: throw IllegalDbStateException(
                    "No bus route row found for service number " +
                            "$serviceNumber and stop code " +
                            "$busStopCode in local DB."
                )

        val arrivals = toArrivals(busStopCode)

//        val destinationStopDescription = arrivals.nextArrivingBus.destination.busStopDescription
//        val originStopDescription = arrivals.nextArrivingBus.destination.busStopDescription

        return BusStopArrival(
            busStopCode = busStopCode,
            busServiceNumber = serviceNumber,
            operator = operator,
//            originBusStopDescription = originStopDescription,
//            destinationBusStopDescription = destinationStopDescription,
            direction = direction,
            stopSequence = stopSequence,
            distance = distance,
            //isStarred,
            busArrivals = arrivals
        )
    }

    private suspend inline fun BusArrivalItemDto.toArrivals(
        busStopCode: String
    ): BusArrivals {
        val arrivingBusDto = arrivingBus

        if (arrivingBusDto == null || arrivingBusDto.estimatedArrival == "") {
            return BusArrivals.Error(
                message = "First arriving bus is null for bus stop $busStopCode " +
                        "and service $serviceNumber"
            )
        }

        val nextArrivingBus = arrivingBusDto.toArrivingBus()

        val followingArrivingBusList = mutableListOf<ArrivingBus>()

        var followArrivingBusDto = arrivingBus1

        if (followArrivingBusDto != null) {
            if (followArrivingBusDto.estimatedArrival != "") {
                followingArrivingBusList.add(followArrivingBusDto.toArrivingBus())
            }
        }

        followArrivingBusDto = arrivingBus2

        if (followArrivingBusDto != null) {
            if (followArrivingBusDto.estimatedArrival != "") {
                followingArrivingBusList.add(followArrivingBusDto.toArrivingBus())
            }
        }

        return BusArrivals.Arriving(nextArrivingBus, followingArrivingBusList)
    }

    private suspend inline fun ArrivingBusItemDto.toArrivingBus(): ArrivingBus {

        val time =
            ChronoUnit.MINUTES.between(
                OffsetDateTime.now(), OffsetDateTime.parse(estimatedArrival)
            )

        val origin: ArrivingBusStop =
            busStopDao
                .getBusStop(originCode)
                ?.let { busStopEntity ->
                    ArrivingBusStop(
                        busStopCode = busStopEntity.code,
                        roadName = busStopEntity.roadName,
                        busStopDescription = busStopEntity.description
                    )
                }
                ?: throw IllegalDbStateException(
                    "No bus stop row found for stop code $originCode (origin) in local DB."
                )

        val destination: ArrivingBusStop =
            busStopDao
                .getBusStop(destinationCode)
                ?.let { busStopEntity ->
                    ArrivingBusStop(
                        busStopCode = busStopEntity.code,
                        roadName = busStopEntity.roadName,
                        busStopDescription = busStopEntity.description
                    )
                }
                ?: throw IllegalDbStateException(
                    "No bus stop row found for stop code " +
                            "$destinationCode (destination) in local DB."
                )

        return ArrivingBus(
            origin,
            destination,
            time.toInt().coerceAtLeast(0),
            //if (time >= 60) "60+" else if (time > 0) String.format("%02d", time) else "Arr",
            latitude.toDouble(),
            longitude.toDouble(),
            visitNumber.toInt(),
            BusLoad.valueOf(load),
            feature == "WAB",
            BusType.valueOf(type)
        )
    }

    private suspend inline fun OperatingBusEntity.toBusArrivalError(
        //isStarred: Boolean
    ): BusStopArrival {

        val (_, _, direction, stopSequence, distance) =
            busRouteDao
                .getBusRoute(
                    busServiceNumber = busServiceNumber,
                    busStopCode = busStopCode
                )
                ?: throw IllegalDbStateException(
                    "No bus route row found for service number " +
                            "$busServiceNumber and stop code " +
                            "$busStopCode in local DB."
                )

        val (
            _,
            _,
            wdFirstBus: OffsetTime?,
            wdLastBus: OffsetTime?,
            satFirstBus: OffsetTime?,
            satLastBus: OffsetTime?,
            sunFirstBus: OffsetTime?,
            sunLastBus: OffsetTime?
        ) = this

        val busArrivals: BusArrivals = when {
            TimeUtil.isWeekday() -> {
                getBusArrivalsError(wdFirstBus, wdLastBus)
            }
            TimeUtil.isSaturday() -> {
                getBusArrivalsError(satFirstBus, satLastBus)
            }
            TimeUtil.isSunday() -> {
                getBusArrivalsError(sunFirstBus, sunLastBus)
            }
            else -> {
                throw IllegalDbStateException(
                    "This day is neither a weekday nor a saturday or sunday."
                )
            }
        }

        return BusStopArrival(
            busStopCode = busStopCode,
            busServiceNumber = busServiceNumber,
            operator = "N/A",
            direction = direction,
            stopSequence = stopSequence,
            distance = distance,
            //isStarred,
            busArrivals = busArrivals
        )
    }

    private fun getBusArrivalsError(
        firstBus: OffsetTime?,
        lastBus: OffsetTime?
    ): BusArrivals {
        if (firstBus != null) {
            if (OffsetTime.now().isBefore(firstBus)) {
                return BusArrivals.NotOperating
            }
        }
        if (lastBus != null) {
            if (OffsetTime.now().isAfter(lastBus)) {
                return BusArrivals.NotOperating
            }
        }
        return BusArrivals.DataNotAvailable
    }

//    suspend fun getStarredBusStopsArrivals(): List<StarredBusArrival> =
//        withContext(dispatcherProvider.io) {
//            val starredBusStops = starredBusStopsDao.findAll()
//            starredBusStops.map { (busStopCode, busServiceNumber) ->
//                async(dispatcherProvider.pool8) {
//                    getBusArrivals(busStopCode)
//                        .find { it.busServiceNumber == busServiceNumber }
//                        ?.let { busArrival ->
//                            StarredBusArrival(
//                                busStopCode,
//                                busServiceNumber,
//                                busStopDao
//                                    .findByCode(busStopCode)
//                                    .takeIf { it.isNotEmpty() }
//                                    ?.get(0)
//                                    ?.description
//                                    ?: throw Exception(
//                                        "No bus stop row found for stop code " +
//                                                "$busStopCode in local DB."
//                                    ),
//                                busArrival.busArrivals
//                            )
//                        }
//                        ?: throw Exception(
//                            "Bus arrival for bus stop " +
//                                    "$busStopCode and service number " +
//                                    "$busServiceNumber not fetched."
//                        )
//                }
//            }.awaitAll()
//        }
}