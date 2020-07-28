package io.github.amanshuraikwar.nxtbuz.data.room.busroute

import androidx.room.*
import io.github.amanshuraikwar.nxtbuz.data.room.busstops.BusStopEntity
import kotlin.math.max

@Dao
interface BusRouteDao {

    @Insert
    fun insertAll(entities: List<BusRouteEntity>)

    @Delete
    fun delete(entity: BusRouteEntity)

    @Query("DELETE from BusRouteEntity")
    fun deleteAll()

    @Query("SELECT * FROM BusRouteEntity")
    fun findAll(): List<BusRouteEntity>

    @Query("SELECT * FROM BusRouteEntity WHERE busServiceNumber = :busServiceNumber")
    fun findByBusServiceNumber(busServiceNumber: String): List<BusRouteEntity>

    @Query("SELECT * FROM BusRouteEntity WHERE busServiceNumber = :busServiceNumber AND busStopCode = :busStopCode")
    suspend fun findByBusServiceNumberAndBusStopCode(busServiceNumber: String, busStopCode: String): List<BusRouteEntity>

    @Query("SELECT * FROM BusRouteEntity WHERE busServiceNumber like '%' || :busServiceNumber || '%' group by busServiceNumber, direction order by direction")
    suspend fun searchLikeBusServiceNumberAllOrder(busServiceNumber: String): List<BusRouteEntity>

    suspend fun searchLikeBusServiceNumber(busServiceNumber: String, limit: Int): List<BusRouteEntity> {
        val list = searchLikeBusServiceNumberAllOrder(busServiceNumber)
            .groupBy { it.busServiceNumber }
            .map { (_, v) -> v.sortedBy { it.direction }[0] }

        return list.dropLast(max(0, list.size - limit))
    }
}