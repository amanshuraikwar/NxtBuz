package io.github.amanshuraikwar.nxtbuz.data.starred.delegate

import io.github.amanshuraikwar.ltaapi.LtaApi
import io.github.amanshuraikwar.ltaapi.model.ArrivingBusItemDto
import io.github.amanshuraikwar.ltaapi.model.BusArrivalItemDto
import io.github.amanshuraikwar.nxtbuz.common.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.common.model.*
import io.github.amanshuraikwar.nxtbuz.data.room.dao.BusRouteDao
import io.github.amanshuraikwar.nxtbuz.data.room.dao.BusStopDao
import io.github.amanshuraikwar.nxtbuz.data.room.dao.OperatingBusDao
import io.github.amanshuraikwar.nxtbuz.common.model.room.OperatingBusEntity
import io.github.amanshuraikwar.nxtbuz.data.room.dao.StarredBusStopsDao
import io.github.amanshuraikwar.nxtbuz.util.TimeUtil
import kotlinx.coroutines.withContext
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.OffsetTime
import org.threeten.bp.temporal.ChronoUnit
import javax.inject.Inject

class BusArrivalsDelegateImpl @Inject constructor(
    private val starredBusStopsDao: StarredBusStopsDao,
    private val operatingBusDao: OperatingBusDao,
    private val busRouteDao: BusRouteDao,
    private val busStopDao: BusStopDao,
    private val busApi: LtaApi,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) : BusArrivalsDelegate {

    override suspend fun getBusArrivals(busStopCode: String): List<BusArrival> =
        withContext(dispatcherProvider.io) {

            val starredBusServiceNumberSet =
                starredBusStopsDao
                    .findByBusStopCode(busStopCode)
                    .map { it.busServiceNumber }
                    .toSet()

            fun BusArrivalItemDto.isStarred() =
                starredBusServiceNumberSet.contains(this.serviceNumber)

            fun OperatingBusEntity.isStarred() =
                starredBusServiceNumberSet.contains(this.busServiceNumber)

            val operatingBusServiceNumberMap =
                operatingBusDao
                    .findByBusStopCode(busStopCode)
                    .groupBy { it.busServiceNumber }
                    .mapValues { (_, v) -> v[0] }
                    .toMutableMap()

            val busArrivalItemList = busApi.getBusArrivals(busStopCode).busArrivals

            val busArrivalList = busArrivalItemList
                .map { busArrivalItem ->

                    if (!operatingBusServiceNumberMap.containsKey(busArrivalItem.serviceNumber)) {
                        throw Exception(
                            "No operating bus row found for service number " +
                                    "${busArrivalItem.serviceNumber} and stop code " +
                                    "$busStopCode in local DB."
                        )
                    }

                    operatingBusServiceNumberMap.remove(busArrivalItem.serviceNumber)

                    busArrivalItem.toBusArrival(
                        busStopCode,
                        busArrivalItem.isStarred()
                    )
                }
                .toMutableList()

            // add remaining buses as Arrivals.DataNotAvailable or Arrivals.NotOperating
            operatingBusServiceNumberMap.forEach { (_, operatingBusEntity) ->
                busArrivalList.add(
                    operatingBusEntity.toBusArrivalError(
                        operatingBusEntity.isStarred()
                    )
                )
            }

            busArrivalList
        }

    private suspend inline fun BusArrivalItemDto.toBusArrival(
        busStopCode: String,
        isStarred: Boolean
    ): BusArrival {

        val (_, _, direction, stopSequence, distance) =
            busRouteDao
                .findByBusServiceNumberAndBusStopCode(
                    serviceNumber,
                    busStopCode
                )
                .takeIf { it.isNotEmpty() }
                ?.get(0)
                ?: throw Exception(
                    "No bus route row found for service number " +
                            "$serviceNumber and stop code " +
                            "$busStopCode in local DB."
                )

        val arrivals = toArrivals(busStopCode)

        val destinationStopDescription = arrivals.nextArrivingBus.destination.busStopDescription
        val originStopDescription = arrivals.nextArrivingBus.destination.busStopDescription

        return BusArrival(
            serviceNumber,
            operator,
            originStopDescription,
            destinationStopDescription,
            direction,
            stopSequence,
            distance,
            isStarred,
            arrivals
        )
    }


    private suspend inline fun BusArrivalItemDto.toArrivals(busStopCode: String): Arrivals.Arriving {

        if (arrivingBus == null || arrivingBus!!.estimatedArrival == "") {
            throw Exception(
                "First arriving bus is null for bus stop $busStopCode and service $serviceNumber"
            )
        }

        val nextArrivingBus = arrivingBus!!.asArrivingBus()

        val followingArrivingBusList = mutableListOf<ArrivingBus>()

        if (arrivingBus1 != null) {
            if (arrivingBus1!!.estimatedArrival != "") {
                followingArrivingBusList.add(arrivingBus1!!.asArrivingBus())
            }
        }


        if (arrivingBus2 != null) {
            if (arrivingBus2!!.estimatedArrival != "") {
                followingArrivingBusList.add(arrivingBus2!!.asArrivingBus())
            }
        }

        return Arrivals.Arriving(nextArrivingBus, followingArrivingBusList)

    }

    private suspend inline fun ArrivingBusItemDto.asArrivingBus(): ArrivingBus {

        val time =
            ChronoUnit.MINUTES.between(
                OffsetDateTime.now(), OffsetDateTime.parse(estimatedArrival)
            )

        val origin: ArrivingBusStop =
            busStopDao
                .findByCode(originCode)
                .takeIf { it.isNotEmpty() }
                ?.get(0)
                ?.let { busStopEntity ->
                    ArrivingBusStop(
                        busStopCode = busStopEntity.code,
                        roadName = busStopEntity.roadName,
                        busStopDescription = busStopEntity.description
                    )
                }
                ?: throw Exception(
                    "No bus stop row found for stop code $originCode (origin) in local DB."
                )

        val destination: ArrivingBusStop =
            busStopDao
                .findByCode(destinationCode)
                .takeIf { it.isNotEmpty() }
                ?.get(0)
                ?.let { busStopEntity ->
                    ArrivingBusStop(
                        busStopCode = busStopEntity.code,
                        roadName = busStopEntity.roadName,
                        busStopDescription = busStopEntity.description
                    )
                }
                ?: throw Exception(
                    "No bus stop row found for stop code " +
                            "$destinationCode (destination) in local DB."
                )

        return ArrivingBus(
            origin,
            destination,
            if (time >= 60) "60+" else if (time > 0) String.format("%02d", time) else "Arr",
            latitude.toDouble(),
            longitude.toDouble(),
            visitNumber.toInt(),
            BusLoad.valueOf(load),
            feature,
            BusType.valueOf(type)
        )
    }

    private suspend inline fun OperatingBusEntity.toBusArrivalError(
        isStarred: Boolean
    ): BusArrival {

        val (_, _, direction, stopSequence, distance) =
            busRouteDao
                .findByBusServiceNumberAndBusStopCode(
                    busServiceNumber,
                    busStopCode
                )
                .takeIf { it.isNotEmpty() }
                ?.get(0)
                ?: throw Exception(
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

        val arrivals: Arrivals = when {
            TimeUtil.isWeekday() -> {
                getArrivalsError(wdFirstBus, wdLastBus)
            }
            TimeUtil.isSaturday() -> {
                getArrivalsError(satFirstBus, satLastBus)
            }
            TimeUtil.isSunday() -> {
                getArrivalsError(sunFirstBus, sunLastBus)
            }
            else -> {
                throw Exception("This day is neither a weekday nor a saturday or sunday.")
            }
        }

        return BusArrival(
            busServiceNumber,
            "N/A",
            "N/A",
            "N/A",
            direction,
            stopSequence,
            distance,
            isStarred,
            arrivals
        )
    }

    private fun getArrivalsError(
        firstBus: OffsetTime?,
        lastBus: OffsetTime?
    ): Arrivals {
        if (firstBus != null) {
            if (OffsetTime.now().isBefore(firstBus)) {
                return Arrivals.NotOperating
            }
        }
        if (lastBus != null) {
            if (OffsetTime.now().isAfter(lastBus)) {
                return Arrivals.NotOperating
            }
        }
        return Arrivals.DataNotAvailable
    }
}