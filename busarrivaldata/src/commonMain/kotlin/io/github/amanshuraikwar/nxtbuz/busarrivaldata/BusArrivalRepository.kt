package io.github.amanshuraikwar.nxtbuz.busarrivaldata

import io.github.amanshuraikwar.nxtbuz.commonkmm.Bus
import io.github.amanshuraikwar.nxtbuz.commonkmm.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.commonkmm.TimeUtil
import io.github.amanshuraikwar.nxtbuz.commonkmm.arrival.*
import io.github.amanshuraikwar.nxtbuz.commonkmm.exception.IllegalDbStateException
import io.github.amanshuraikwar.nxtbuz.localdatasource.*
import io.github.amanshuraikwar.nxtbuz.remotedatasource.ArrivingBusItemDto
import io.github.amanshuraikwar.nxtbuz.remotedatasource.BusArrivalItemDto
import io.github.amanshuraikwar.nxtbuz.remotedatasource.RemoteDataSource
import io.github.amanshuraikwar.nxtbuz.repository.BusArrivalRepository
import kotlinx.coroutines.withContext
import kotlinx.datetime.*

class BusArrivalRepositoryImpl constructor(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) : BusArrivalRepository {
    override suspend fun getOperatingBusServices(busStopCode: String): List<Bus> {
        return withContext(dispatcherProvider.io) {
            localDataSource
                .findOperatingBuses(busStopCode)
                .map { operatingBusEntity ->
                    Bus(
                        serviceNumber = operatingBusEntity.busServiceNumber
                    )
                }
        }
    }

    override suspend fun getBusArrivals(busStopCode: String): List<BusStopArrival> {
        return withContext(dispatcherProvider.io) {
            // map of busServiceNumber -> OperatingBusEntity
            val operatingBusServiceNumberMap =
                localDataSource.findOperatingBuses(busStopCode)
                    .groupBy { it.busServiceNumber }
                    .mapValues { (_, v) -> v[0] }
                    .toMutableMap()

            val busStopArrivalList = mutableListOf<BusStopArrival>()

            remoteDataSource.getBusArrivals(busStopCode)
                .busArrivals
                .forEach { busArrivalItemDto ->

                    @Suppress("ControlFlowWithEmptyBody")
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
                        // TODO-amanshuraikwar (26 May 2021 11:58:35 AM): schedule DB update
                    }
                }

            // add remaining buses as Arrivals.DataNotAvailable or Arrivals.NotOperating
            operatingBusServiceNumberMap.forEach { (_, operatingBusEntity) ->
                busStopArrivalList.add(
                    operatingBusEntity.toBusArrivalError()
                )
            }

            busStopArrivalList
        }
    }

    override suspend fun getBusArrivals(
        busStopCode: String,
        busServiceNumber: String
    ): BusStopArrival {
        return withContext(dispatcherProvider.io) {
            val operatingBusEntity =
                localDataSource.findOperatingBus(
                    busStopCode = busStopCode,
                    busServiceNumber = busServiceNumber
                )
                    ?: run {
                        // bus service number returned from remote api is not in local db
                        throw IllegalDbStateException(
                            "No operating bus row found for service number " +
                                    "$busServiceNumber and stop code " +
                                    "$busStopCode in local DB."
                        )
                    }

            val busArrivalItemList =
                remoteDataSource.getBusArrivals(busStopCode, busServiceNumber).busArrivals

            if (busArrivalItemList.isNotEmpty()) {

                val busArrivalItem = busArrivalItemList[0]
                return@withContext busArrivalItem.toBusArrival(busStopCode)

            } else {

                return@withContext operatingBusEntity.toBusArrivalError()
            }
        }
    }

    override suspend fun isBusOperating(busStopCode: String, busServiceNumber: String): Boolean {
        val (
            _,
            _,
            wdFirstBus: LocalHourMinute?,
            wdLastBus: LocalHourMinute?,
            satFirstBus: LocalHourMinute?,
            satLastBus: LocalHourMinute?,
            sunFirstBus: LocalHourMinute?,
            sunLastBus: LocalHourMinute?
        ) = localDataSource
            .findOperatingBus(
                busStopCode = busStopCode,
                busServiceNumber = busServiceNumber,
            )
            // this bus does not arrive at the bus stop
            ?: return false

        return when {
            TimeUtil.isWeekday() -> {
                if (wdFirstBus != null && wdLastBus != null) {
                    Clock.System.now().isInBetween(wdFirstBus, wdLastBus)
                } else {
                    true
                }
            }
            TimeUtil.isSaturday() -> {
                if (satFirstBus != null && satLastBus != null) {
                    Clock.System.now().isInBetween(satFirstBus, satLastBus)
                } else {
                    true
                }
            }
            TimeUtil.isSunday() -> {
                if (sunFirstBus != null && sunLastBus != null) {
                    Clock.System.now().isInBetween(sunFirstBus, sunLastBus)
                } else {
                    true
                }
            }
            else -> {
                true
            }
        }
    }

    fun Instant.isInBetween(first: LocalHourMinute, last: LocalHourMinute): Boolean {
        return when {
            first.hour < last.hour -> {
                isAfter(first) && isBefore(last)
            }
            else -> {
                !(isBefore(first) && isAfter(last))
            }
        }
    }

    private suspend inline fun BusArrivalItemDto.toBusArrival(
        busStopCode: String,
    ): BusStopArrival {
        val (_, _, direction, stopSequence, distance) =
            localDataSource
                .findBusRoute(busStopCode = busStopCode, busServiceNumber = serviceNumber)
                ?: throw IllegalDbStateException(
                    "No bus route row found for service number " +
                            "$serviceNumber and stop code " +
                            "$busStopCode in local DB."
                )

        val arrivals = toArrivals(busStopCode)

        val busStopDescription: String =
            localDataSource
                .findBusStopByCode(busStopCode = busStopCode)
                ?.description
                ?: throw IllegalDbStateException(
                    "No bus stop row found for stop code " +
                            "$busStopCode in local DB."
                )

        return BusStopArrival(
            busStopCode = busStopCode,
            busStopDescription = busStopDescription,
            busServiceNumber = serviceNumber,
            operator = operator,
            direction = direction,
            stopSequence = stopSequence,
            distance = distance,
            busArrivals = arrivals
        )
    }

    private suspend inline fun BusArrivalItemDto.toArrivals(busStopCode: String): BusArrivals {
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
        val time = Clock.System.now()
            .periodUntil(
                estimatedArrival.toInstant(),
                TimeZone.currentSystemDefault()
            )
            .minutes
            .coerceAtLeast(0)

        val origin: ArrivingBusStop =
            localDataSource
                .findBusStopByCode(busStopCode = originCode)
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
            localDataSource
                .findBusStopByCode(busStopCode = destinationCode)
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
            time.coerceAtLeast(0),
            estimatedArrival.toInstant(),
            lat.toDouble(),
            lng.toDouble(),
            visitNumber.toInt(),
            BusLoad.valueOf(load),
            feature == "WAB",
            BusType.valueOf(type)
        )
    }

    private suspend inline fun OperatingBusEntity.toBusArrivalError(): BusStopArrival {
        val (_, _, direction, stopSequence, distance) =
            localDataSource
                .findBusRoute(
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
            wdFirstBus: LocalHourMinute?,
            wdLastBus: LocalHourMinute?,
            satFirstBus: LocalHourMinute?,
            satLastBus: LocalHourMinute?,
            sunFirstBus: LocalHourMinute?,
            sunLastBus: LocalHourMinute?
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

        val busStopDescription: String =
            localDataSource
                .findBusStopByCode(busStopCode = busStopCode)
                ?.description
                ?: throw IllegalDbStateException(
                    "No bus stop row found for stop code " +
                            "$busStopCode in local DB."
                )

        return BusStopArrival(
            busStopCode = busStopCode,
            busStopDescription = busStopDescription,
            busServiceNumber = busServiceNumber,
            operator = "N/A",
            direction = direction,
            stopSequence = stopSequence,
            distance = distance,
            busArrivals = busArrivals
        )
    }

    private fun getBusArrivalsError(
        firstBus: LocalHourMinute?,
        lastBus: LocalHourMinute?
    ): BusArrivals {
        if (firstBus != null) {
            if (firstBus.isAfter(Clock.System.now())) {
                return BusArrivals.NotOperating
            }
        }
        if (lastBus != null) {
            if (lastBus.isBefore(Clock.System.now())) {
                return BusArrivals.NotOperating
            }
        }
        return BusArrivals.DataNotAvailable
    }
}