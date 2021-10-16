package io.github.amanshuraikwar.nxtbuz.roomdb.dao

import androidx.room.*
import io.github.amanshuraikwar.nxtbuz.roomdb.model.BusArrivalRoomDbEntity

@Suppress("unused")
@Dao
internal interface BusArrivalDao {
    @Insert
    suspend fun insertAll(entities: List<BusArrivalRoomDbEntity>)

    @Update
    suspend fun updateAll(entities: List<BusArrivalRoomDbEntity>)

    @Delete
    suspend fun delete(entity: BusArrivalRoomDbEntity)

    @Query("DELETE from BusArrivalEntity")
    suspend fun deleteAll()

    @Query("SELECT * FROM BusArrivalEntity")
    suspend fun findAll(): List<BusArrivalRoomDbEntity>

    @Query("SELECT * FROM BusArrivalEntity WHERE busServiceNumber = :busServiceNumber")
    suspend fun findByBusServiceNumber(busServiceNumber: String): List<BusArrivalRoomDbEntity>

    @Query("SELECT * FROM BusArrivalEntity WHERE busServiceNumber = :busServiceNumber AND busStopCode = :busStopCode")
    suspend fun findByBusServiceNumberAndBusStopCode(
        busServiceNumber: String,
        busStopCode: String
    ): List<BusArrivalRoomDbEntity>

    @Query("SELECT * FROM BusArrivalEntity WHERE busStopCode = :busStopCode")
    suspend fun findByBusStopCode(
        busStopCode: String
    ): List<BusArrivalRoomDbEntity>
}