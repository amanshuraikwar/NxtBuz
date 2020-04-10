package io.github.amanshuraikwar.nxtbuz.data.user

import android.annotation.SuppressLint
import android.location.Location
import android.util.Log
import androidx.annotation.FloatRange
import androidx.annotation.IntRange
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.tasks.Tasks
import io.github.amanshuraikwar.nxtbuz.data.busapi.*
import io.github.amanshuraikwar.nxtbuz.data.di.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.data.model.*
import io.github.amanshuraikwar.nxtbuz.data.prefs.PreferenceStorage
import io.github.amanshuraikwar.nxtbuz.data.room.RoomDataSource
import io.github.amanshuraikwar.nxtbuz.data.room.busroute.BusRouteEntity
import io.github.amanshuraikwar.nxtbuz.data.room.busstops.BusStopEntity
import io.github.amanshuraikwar.nxtbuz.data.room.operatingbus.OperatingBusEntity
import io.github.amanshuraikwar.nxtbuz.data.room.starredbusstops.StarredBusStopEntity
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.OffsetTime
import org.threeten.bp.temporal.ChronoUnit
import java.lang.Double.max
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "UserRepository"

@Singleton
class UserRepository @Inject constructor(
    private val preferenceStorage: PreferenceStorage,
    private val roomDataSource: RoomDataSource,
    private val sgBusApi: SgBusApi,
    private val dispatcherProvider: CoroutinesDispatcherProvider,
    private val fusedLocationProviderClient: FusedLocationProviderClient
) {

    suspend fun getUserState(): UserState = withContext(dispatcherProvider.io) {
        return@withContext if (preferenceStorage.onboardingCompleted) {
            UserState.SetupComplete
        } else {
            UserState.New
        }
    }

    sealed class SetupState {
        data class InProgress(@FloatRange(from = 0.0, to = 1.0) val progress: Double) : SetupState()
        object Complete : SetupState()
    }

    @ExperimentalCoroutinesApi
    fun setupFlow(): Flow<SetupState> =
        flow {
            setup(this)
        }

    suspend fun setup(flowCollector: FlowCollector<SetupState>) = withContext(dispatcherProvider.io) {

        flowCollector.emit(SetupState.InProgress(0.0))

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
            flowCollector.emit(
                SetupState.InProgress(
                    0.8.coerceAtMost((skip.toDouble() / 32000.0) * 0.8)
                )
            )
        }

        deferredList.awaitAll()

        Log.i(TAG, "setup: Skip when complete = $skip")

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

        flowCollector.emit(SetupState.InProgress(0.9))

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
        flowCollector.emit(SetupState.InProgress(1.0))
        flowCollector.emit(SetupState.Complete)
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

            val starredBusServiceNumberSet =
                roomDataSource.starredBusStopsDao
                    .findByBusStopCode(busStopCode)
                    .map { it.busServiceNumber }
                    .toSet()

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

                    val arrivals = busArrivalItem.toArrivals(
                        starredBusServiceNumberSet.contains(busArrivalItem.serviceNumber)
                    )

                    val destinationStopDescription: String =
                        when (arrivals) {
                            is Arrivals.NotOperating -> "N/A"
                            is Arrivals.Arriving -> arrivals.arrivingBusList[0].destination.busStopDescription
                        }

                    val originStopDescription: String =
                        when (arrivals) {
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

    private fun BusArrivalItem.toArrivals(starred: Boolean): Arrivals {

        if (arrivingBus == null || arrivingBus.estimatedArrival == "") {
            return Arrivals.NotOperating(starred)
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

        return Arrivals.Arriving(starred, arrivingBusList)
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

    @Suppress("BlockingMethodInNonBlockingContext")
    @SuppressLint("MissingPermission")
    suspend fun getLastKnownLocation(): Location? = withContext(dispatcherProvider.io) {
        Tasks.await(fusedLocationProviderClient.lastLocation)
    }

    suspend fun searchBusStops(query: String, limit: Int): List<BusStop> =
        withContext(dispatcherProvider.io) {
            roomDataSource.busStopDao
                .searchLikeDescription(query, limit)
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

    suspend fun toggleBusStopStar(busStopCode: String, busServiceNumber: String) =
        withContext(dispatcherProvider.io) {
            val isAlreadyStarred =
                roomDataSource.starredBusStopsDao.findByBusStopCodeAndBusServiceNumber(
                    busStopCode, busServiceNumber
                ).isNotEmpty()
            if (isAlreadyStarred) {
                roomDataSource.starredBusStopsDao.deleteByBusStopCodeAndBusServiceNumber(
                    busStopCode, busServiceNumber
                )
            } else {
                roomDataSource.starredBusStopsDao.insertAll(
                    listOf(
                        StarredBusStopEntity(busStopCode, busServiceNumber)
                    )
                )
            }
        }

    suspend fun getStarredBusStopsArrivals(): List<StarredBusArrival> =
        withContext(dispatcherProvider.io) {
            val starredBusStops = roomDataSource.starredBusStopsDao.findAll()
            starredBusStops.map { (busStopCode, busServiceNumber) ->
                async(dispatcherProvider.pool8) {
                    getBusArrivals(busStopCode)
                        .find { it.serviceNumber == busServiceNumber }
                        ?.arrivals
                        ?.let {
                            if (it is Arrivals.Arriving) {
                                it as Arrivals.Arriving
                            } else {
                                null
                            }
                        }
                        ?.let {
                            it.arrivingBusList[0]
                        }?.let {
                            StarredBusArrival.Arriving(
                                busStopCode,
                                busServiceNumber,
                                roomDataSource.busStopDao.findByCode(busStopCode)[0].description,
                                it
                            )
                        }
                        ?: StarredBusArrival.NotOperating(busStopCode, busServiceNumber)
                }
            }.awaitAll()
        }
}

sealed class StarredBusArrival(
    val busStopCode: String,
    val busServiceNumber: String
) {
    class Arriving(
        busStopCode: String,
        busServiceNumber: String,
        val busStopDescription: String,
        val arrivingBus: ArrivingBus
    ) : StarredBusArrival(
        busStopCode, busServiceNumber
    )

    class NotOperating(
        busStopCode: String,
        busServiceNumber: String
    ) : StarredBusArrival(
        busStopCode, busServiceNumber
    )
}