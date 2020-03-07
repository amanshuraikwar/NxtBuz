package io.github.amanshuraikwar.howmuch.data.user

import android.util.Log
import io.github.amanshuraikwar.howmuch.data.BusServiceNumber
import io.github.amanshuraikwar.howmuch.data.BusStopCode
import io.github.amanshuraikwar.howmuch.data.busapi.*
import io.github.amanshuraikwar.howmuch.data.di.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.howmuch.data.model.*
import io.github.amanshuraikwar.howmuch.data.prefs.PreferenceStorage
import io.github.amanshuraikwar.howmuch.data.room.RoomDataSource
import io.github.amanshuraikwar.howmuch.data.room.busroute.BusRouteEntity
import io.github.amanshuraikwar.howmuch.data.room.busstops.BusStopEntity
import io.github.amanshuraikwar.howmuch.data.room.operatingbus.OperatingBusEntity
import io.github.amanshuraikwar.howmuch.util.TimeUtil
import kotlinx.coroutines.*
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.OffsetTime
import org.threeten.bp.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "UserRepository"

@Singleton
class UserRepository @Inject constructor(
    private val preferenceStorage: PreferenceStorage,
    private val roomDataSource: RoomDataSource,
    private val sgBusApi: SgBusApi,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) {

    suspend fun getUserState(): UserState = withContext(dispatcherProvider.io) {
        return@withContext if (preferenceStorage.onboardingCompleted) {
            UserState.SetupComplete
        } else {
            UserState.New
        }
    }

    suspend fun setup() = withContext(dispatcherProvider.io) {

        markSetupIncomplete()
        roomDataSource.deleteAllData()

        var skip = 0

        val busStopItemList = mutableListOf<BusStopItem>()
        while (true) {
            val fetchedBusStops = sgBusApi.getBusStops(skip).busStops
            if (fetchedBusStops.isEmpty()) break
            busStopItemList.addAll(fetchedBusStops)
            skip += 500
        }

        val start = System.currentTimeMillis()

        val deferredList = mutableListOf<Deferred<Unit>>()
        val busStopServiceNumberMap = mutableMapOf<String, MutableSet<BusRouteItem>>()
        val serviceNumberBusRouteMap = mutableMapOf<String, MutableSet<BusRouteItem>>()

        skip = 0

        var shouldStop = false

        while (!shouldStop) {
            val myThreadLocal = ThreadLocal<Int>()
            deferredList.add(
                async(dispatcherProvider.pool8 + myThreadLocal.asContextElement(skip)) {

                    val busRouteItemList = sgBusApi
                        .getBusRoutes(myThreadLocal.get()!!)
                        .also {
                            Log.i(
                                TAG,
                                "setup: Async: " +
                                        "${myThreadLocal.hashCode()} ${myThreadLocal.get()!!}"
                            )
                        }
                        .busRouteList

                    if (busRouteItemList.isEmpty()) {
                        Log.i(TAG, "setup: Async: stopping as skip ${myThreadLocal.get()!!}")
                        shouldStop = true
                    }

                    busRouteItemList.forEach {

                        if (!busStopServiceNumberMap.containsKey(it.busStopCode)) {
                            busStopServiceNumberMap[it.busStopCode] = mutableSetOf()
                        }
                        busStopServiceNumberMap[it.busStopCode]?.add(it)

                        if (!serviceNumberBusRouteMap.containsKey(it.serviceNumber)) {
                            serviceNumberBusRouteMap[it.serviceNumber] = mutableSetOf()
                        }
                        serviceNumberBusRouteMap[it.serviceNumber]?.add(it)

                    }
                }
            )
            if (deferredList.size == 16) {
                deferredList.awaitAll()
                deferredList.clear()
            }
            skip += 500
        }

        deferredList.awaitAll()

        roomDataSource.addBusStops(
            busStopItemList.map {
                BusStopEntity(
                    it.code,
                    it.roadName,
                    it.description,
                    it.latitude,
                    it.longitude
                )
            }
        )

        fun String.toTime() =
            substring(0..1).let { if (it == "24") "00" else it } +
                    ":" +
                    substring(2..3).let { if (it == "0`") "00" else it } +
                    ":00+08:00"

        busStopServiceNumberMap
            .map { (busStopCode, busRouteItem) ->
                async(dispatcherProvider.pool8) {
                    roomDataSource.addOperatingBus(
                        busRouteItem.map {
                            OperatingBusEntity(
                                busStopCode = busStopCode,
                                busServiceNumber = it.serviceNumber,
                                wdFirstBus = if (it.wdFirstBus == "-") {
                                    null
                                } else {
                                    OffsetTime.parse(it.wdFirstBus.toTime())
                                },
                                wdLastBus = if (it.wdLastBus == "-") {
                                    null
                                } else {
                                    OffsetTime.parse(it.wdLastBus.toTime())
                                },
                                satFirstBus = if (it.satFirstBus == "-") {
                                    null
                                } else {
                                    OffsetTime.parse(it.satFirstBus.toTime())
                                },
                                satLastBus = if (it.satLastBus == "-") {
                                    null
                                } else {
                                    OffsetTime.parse(it.satLastBus.toTime())
                                },
                                sunFirstBus = if (it.sunFirstBus == "-") {
                                    null
                                } else {
                                    OffsetTime.parse(it.sunFirstBus.toTime())
                                },
                                sunLastBus = if (it.sunLastBus == "-") {
                                    null
                                } else {
                                    OffsetTime.parse(it.sunLastBus.toTime())
                                }
                            )
                        }
                    )
                }
            }
            .awaitAll()

        serviceNumberBusRouteMap
            .map { (serviceNumber, busRouteItem) ->
                async(dispatcherProvider.pool8) {
                    roomDataSource.addBusRoute(
                        busRouteItem.map {
                            BusRouteEntity(
                                serviceNumber,
                                it.busStopCode,
                                it.direction,
                                it.stopSequence,
                                it.distance
                            )
                        }
                    )
                }
            }
            .awaitAll()

        Log.w(TAG, "setup: Async = ${(System.currentTimeMillis() - start) / 1000} sec")

        markSetupComplete()
    }

    suspend fun getCloseBusStops(
        latitude: Double,
        longitude: Double,
        limit: Int
    ): List<BusStop> = withContext(dispatcherProvider.io) {

        roomDataSource
            .getCloseBusStops(latitude, longitude, limit)
            .map { busStopEntity ->
                async(dispatcherProvider.pool8) {
                    BusStop(
                        /*BusStopCode(*/busStopEntity.code/*)*/,
                        busStopEntity.roadName,
                        busStopEntity.description,
                        busStopEntity.latitude,
                        busStopEntity.longitude,
                        roomDataSource
                            .operatingBusDao
                            .findByBusStopCode(busStopEntity.code)
                            .map {
                                Bus(/*BusServiceNumber(*/it.busServiceNumber/*)*/)
                            }
                    )
                }
            }.awaitAll()
    }

    private suspend fun markSetupComplete() = withContext(dispatcherProvider.io) {
        preferenceStorage.onboardingCompleted = true
    }

    private suspend fun markSetupIncomplete() = withContext(dispatcherProvider.io) {
        preferenceStorage.onboardingCompleted = false
    }

    suspend fun getBusArrivals(busStopCode: String): List<BusArrival> =
        withContext(dispatcherProvider.io) {

            return@withContext sgBusApi
                .getBusArrivals(busStopCode)
                .busArrivals
                .map { busArrivalItem ->

                    val (_, _, direction, stopSequence, distance) =
                        roomDataSource
                            .busRouteDao
                            .findByBusServiceNumberAndBusStopCode(
                                busArrivalItem.serviceNumber,
                                busStopCode
                            )
                            .takeIf { it.isNotEmpty() }
                            ?.get(0)
                            ?: throw Exception(
                                "No bus route node found for service number ${busArrivalItem.serviceNumber} and stop code $busStopCode."
                            )

                    val arrivals = busArrivalItem.toArrivals()

                    val destinationStopDescription: String =
                        when(arrivals) {
                            is Arrivals.NotOperating -> "N/A"
                            is Arrivals.Arriving -> arrivals.arrivingBusList[0].destination.busStopDescription
                        }

                    val originStopDescription: String =
                        when(arrivals) {
                            is Arrivals.NotOperating -> "N/A"
                            is Arrivals.Arriving -> arrivals.arrivingBusList[0].origin.busStopDescription
                        }

                    BusArrival(
                        busArrivalItem.serviceNumber,
                        busArrivalItem.operator,
                        originStopDescription,
                        destinationStopDescription,
                        direction,
                        stopSequence,
                        distance,
                        arrivals
                    )
                }
        }

    private fun BusArrivalItem.toArrivals(): Arrivals {

        if (arrivingBus == null || arrivingBus.estimatedArrival == "") {
            return Arrivals.NotOperating
        }

        val arrivingBusList = mutableListOf<ArrivingBus>()

        arrivingBusList.add(arrivingBus.asArrivingBus())

        if (arrivingBus1 != null) {
            if (arrivingBus1.estimatedArrival != "") {
                arrivingBusList.add(arrivingBus1.asArrivingBus())
            }
        }


        if (arrivingBus2 != null) {
            if (arrivingBus2.estimatedArrival != "") {
                arrivingBusList.add(arrivingBus2.asArrivingBus())
            }
        }

        return Arrivals.Arriving(arrivingBusList)
    }

    private fun ArrivingBusItem.asArrivingBus(): ArrivingBus {

        val time =
            ChronoUnit.MINUTES.between(OffsetDateTime.now(), OffsetDateTime.parse(estimatedArrival))

        val origin: ArrivingBusStop =
            roomDataSource
                .busStopDao
                .findByCode(originCode)
                .takeIf { it.isNotEmpty() }
                ?.get(0)
                ?.let {
                    ArrivingBusStop(
                        busStopCode = it.code,
                        roadName = it.roadName,
                        busStopDescription = it.description
                    )
                }
                ?: throw Exception(
                    "No bus stop found for stop code $originCode (origin)."
                )

        val destination: ArrivingBusStop =
            roomDataSource
                .busStopDao
                .findByCode(destinationCode)
                .takeIf { it.isNotEmpty() }
                ?.get(0)
                ?.let {
                    ArrivingBusStop(
                        busStopCode = it.code,
                        roadName = it.roadName,
                        busStopDescription = it.description
                    )
                }
                ?: throw Exception(
                    "No bus stop found for stop code $destinationCode (destination)."
                )

        return ArrivingBus(
            origin,
            destination,
            if (time >= 60) "60+" else if (time > 0) "${String.format("%02d", time)}" else "Arr",
            latitude.toDouble(),
            longitude.toDouble(),
            visitNumber.toInt(),
            BusLoad.valueOf(load),
            feature,
            BusType.valueOf(type)
        )
    }
}