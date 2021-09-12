package io.github.amanshuraikwar.nxtbuz.roomdb.dao

import androidx.room.*
import io.github.amanshuraikwar.nxtbuz.roomdb.model.BusRouteRoomDbEntity
import kotlin.math.max

@Suppress("unused")
@Dao
interface BusRouteDao {
    @Insert
    suspend fun insertAll(entities: List<BusRouteRoomDbEntity>)

    @Delete
    suspend fun delete(entity: BusRouteRoomDbEntity)

    @Query("DELETE from BusRouteEntity")
    suspend fun deleteAll()

    @Query("SELECT * FROM BusRouteEntity")
    suspend fun findAll(): List<BusRouteRoomDbEntity>

    @Query("SELECT * FROM BusRouteEntity WHERE busServiceNumber = :busServiceNumber")
    suspend fun findByBusServiceNumber(busServiceNumber: String): List<BusRouteRoomDbEntity>

    @Query("SELECT * FROM BusRouteEntity WHERE busServiceNumber = :busServiceNumber AND busStopCode = :busStopCode")
    suspend fun findByBusServiceNumberAndBusStopCode(
        busServiceNumber: String,
        busStopCode: String
    ): List<BusRouteRoomDbEntity>

    @Query("SELECT * FROM BusRouteEntity WHERE busServiceNumber like '%' || :busServiceNumber || '%' group by busServiceNumber, direction order by direction")
    suspend fun searchLikeBusServiceNumberAllOrder(busServiceNumber: String): List<BusRouteRoomDbEntity>

    suspend fun searchLikeBusServiceNumber(
        busServiceNumber: String,
        limit: Int
    ): List<BusRouteRoomDbEntity> {
        val list = searchLikeBusServiceNumberAllOrder(busServiceNumber)
            .groupBy { it.busServiceNumber }
            .map { (_, v) -> v.sortedBy { it.direction }[0] }

        return list.dropLast(max(0, list.size - limit))
    }
}