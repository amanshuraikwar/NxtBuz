package io.github.amanshuraikwar.nxtbuz.data.busroute

import android.util.Log
import io.github.amanshuraikwar.nxtbuz.data.busapi.SgBusApi
import io.github.amanshuraikwar.nxtbuz.data.busapi.model.BusRouteItem
import io.github.amanshuraikwar.nxtbuz.data.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.data.busroute.model.BusRoute
import io.github.amanshuraikwar.nxtbuz.data.busroute.model.BusRouteNode
import io.github.amanshuraikwar.nxtbuz.data.room.busroute.BusRouteDao
import io.github.amanshuraikwar.nxtbuz.data.room.busroute.BusRouteEntity
import io.github.amanshuraikwar.nxtbuz.data.room.busstops.BusStopDao
import io.github.amanshuraikwar.nxtbuz.data.room.operatingbus.OperatingBusDao
import io.github.amanshuraikwar.nxtbuz.data.room.operatingbus.OperatingBusEntity
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import org.threeten.bp.OffsetTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BusRouteRepository @Inject constructor(
    private val busRouteDao: BusRouteDao,
    private val operatingBusDao: OperatingBusDao,
    private val busStopDao: BusStopDao,
    private val busApi: SgBusApi,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) {

    @ExperimentalCoroutinesApi
    fun setup(): Flow<Double> =
        flow {
            setupActual(this)
        }

    private suspend fun setupActual(flowCollector: FlowCollector<Double>) = coroutineScope {

        flowCollector.emit(0.0)

        busRouteDao.deleteAll()
        operatingBusDao.deleteAll()

        val startTimeMillis = System.currentTimeMillis()
        val busStopServiceNumberMap = mutableMapOf<String, MutableSet<BusRouteItem>>()
        val serviceNumberBusRouteMap = mutableMapOf<String, MutableSet<BusRouteItem>>()
        val remoteBusRouteDeferredList = mutableListOf<Deferred<Unit>>()

        var skip = 0
        var shouldStop = false

        // run infinitely
        // until the api starts responding with empty list
        // that's when we know we have queried all possible items
        while (!shouldStop) {

            // thread local to pass skip variable safely
            val threadLocal = ThreadLocal<Int>()

            remoteBusRouteDeferredList.add(
                async(dispatcherProvider.pool8 + threadLocal.asContextElement(skip)) {

                    val threadLocalSkip =
                        threadLocal.get() ?: throw Exception("Something went wrong.")

                    val busRouteItemList = busApi.getBusRoutes(threadLocalSkip).busRouteList

                    // if bus route list fetched is empty
                    // set flag to stop the loop
                    if (busRouteItemList.isEmpty()) {
                        shouldStop = true
                    }

                    busRouteItemList.forEach {

                        synchronized(busStopServiceNumberMap) {
                            if (!busStopServiceNumberMap.containsKey(it.busStopCode)) {
                                busStopServiceNumberMap[it.busStopCode] = mutableSetOf()
                            }
                            busStopServiceNumberMap[it.busStopCode]?.add(it)
                        }

                        synchronized(serviceNumberBusRouteMap) {
                            if (!serviceNumberBusRouteMap.containsKey(it.serviceNumber)) {
                                serviceNumberBusRouteMap[it.serviceNumber] = mutableSetOf()
                            }
                            serviceNumberBusRouteMap[it.serviceNumber]?.add(it)
                        }
                    }
                }
            )

            // we wait for all the coroutine jobs to finish if EMPTY_ASYNC_LIMIT jobs are spawned
            // to limit the number of possible empty async coroutines
            // prevent the infinite loop to spawn a huge number of empty response remote api calls
            // only to wait for them to finish
            // also to prevent over-loading the thread pool
            if (remoteBusRouteDeferredList.size == EMPTY_ASYNC_LIMIT) {
                remoteBusRouteDeferredList.awaitAll()
                remoteBusRouteDeferredList.clear()
            }

            skip += 500

            flowCollector.emit(
                0.8.coerceAtMost((skip.toDouble() / 32000.0) * 0.8)
            )
        }

        // wait for any remaining coroutine jobs to complete
        remoteBusRouteDeferredList.awaitAll()

        // add all operating buses for every bus stop to local DB
        busStopServiceNumberMap
            .map { (busStopCode, busRouteItem) ->
                async(dispatcherProvider.pool8) {
                    operatingBusDao.insertAll(
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

        flowCollector.emit(0.9)

        // store bus route info in local DB
        serviceNumberBusRouteMap
            .map { (serviceNumber, busRouteItem) ->
                async(dispatcherProvider.pool8) {
                    busRouteDao.insertAll(
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

        Log.i(
            TAG,
            "setupActual: Setup took ${(System.currentTimeMillis() - startTimeMillis) / 1000} seconds"
        )

        flowCollector.emit(1.0)
    }

    suspend fun getBusRoute(
        busServiceNumber: String,
        direction: Int? = null,
        busStopCode: String? = null
    ): BusRoute = withContext(dispatcherProvider.io) {

        val busRouteEntityList = busRouteDao.findByBusServiceNumber(busServiceNumber)

        val direction1 = busRouteEntityList.filter { it.direction == 1 }
        val direction2 = busRouteEntityList.filter { it.direction == 2 }

        val (selectedBusRouteEntityList: List<BusRouteEntity>, direction: Int) = when {
            busRouteEntityList.isEmpty() -> {
                // todo separate error state
                emptyList<BusRouteEntity>() to 0
            }
            direction == 1 -> {
                direction1 to 1
            }
            direction == 2 -> {
                direction2 to 2
            }
            busStopCode != null -> {
                when {
                    direction1.find { it.busStopCode == busStopCode } != null -> {
                        direction1 to 1
                    }
                    direction2.find { it.busStopCode == busStopCode } != null -> {
                        direction2 to 2
                    }
                    else -> {
                        // todo separate error state
                        emptyList<BusRouteEntity>() to 0
                    }
                }
            }
            else -> {
                direction1 to 1
            }
        }

        val busRouteNodeList =
            selectedBusRouteEntityList
                .map { busRouteEntity ->
                    async(dispatcherProvider.pool8) {
                        val busStopEntity = busStopDao.findByCode(busRouteEntity.busStopCode).let {
                            if (it.isEmpty()) throw Exception("No bus stop found for bus stop code ${busRouteEntity.busStopCode}")
                            it[0]
                        }
                        BusRouteNode(
                            busServiceNumber,
                            busStopEntity.code,
                            busRouteEntity.direction,
                            busRouteEntity.stopSequence,
                            busRouteEntity.distance,
                            busStopEntity.latitude,
                            busStopEntity.longitude
                        )
                    }
                }
                .awaitAll()
                .sortedBy { it.stopSequence }

        BusRoute(
            busServiceNumber,
            direction,
            busRouteNodeList
        )
    }

    companion object {
        const val EMPTY_ASYNC_LIMIT = 16
        private const val TAG = "BusRouteRepository"
        fun String.toTime() =
            substring(0..1).let { if (it == "24") "00" else it } +
                    ":" +
                    substring(2..3).let { if (it == "0`") "00" else it } +
                    ":00+08:00"
    }
}