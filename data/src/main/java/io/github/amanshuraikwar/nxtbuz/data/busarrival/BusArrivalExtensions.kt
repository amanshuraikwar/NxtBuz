package io.github.amanshuraikwar.nxtbuz.data.busarrival

import io.github.amanshuraikwar.nxtbuz.common.model.room.BusRouteEntity
import io.github.amanshuraikwar.nxtbuz.common.model.room.BusStopEntity
import io.github.amanshuraikwar.nxtbuz.common.model.room.OperatingBusEntity
import io.github.amanshuraikwar.nxtbuz.data.room.dao.BusRouteDao
import io.github.amanshuraikwar.nxtbuz.data.room.dao.BusStopDao
import io.github.amanshuraikwar.nxtbuz.data.room.dao.OperatingBusDao

suspend inline fun BusRouteDao.getBusRoute(
    busStopCode: String,
    busServiceNumber: String,
): BusRouteEntity? {
    return findByBusServiceNumberAndBusStopCode(
        busServiceNumber = busServiceNumber,
        busStopCode = busStopCode
    ).takeIf { it.isNotEmpty() }?.get(0)
}

suspend inline fun BusStopDao.getBusStop(
    busStopCode: String,
): BusStopEntity? {
    return findByCode(busStopCode)
        .takeIf { it.isNotEmpty() }
        ?.get(0)
}

suspend inline fun OperatingBusDao.getOperatingBus(
    busStopCode: String,
    busServiceNumber: String
): OperatingBusEntity? {
    return findByBusStopCodeAndBusServiceNumber(
        busStopCode = busStopCode,
        busServiceNumber = busServiceNumber
    ).takeIf { it.isNotEmpty() }?.get(0)
}