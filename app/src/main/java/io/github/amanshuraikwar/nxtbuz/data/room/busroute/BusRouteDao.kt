package io.github.amanshuraikwar.nxtbuz.data.room.busroute

import androidx.room.*

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
    fun findByBusServiceNumberAndBusStopCode(busServiceNumber: String, busStopCode: String): List<BusRouteEntity>
}