package io.github.amanshuraikwar.nxtbuz.data.room.dao

import androidx.room.*
import io.github.amanshuraikwar.nxtbuz.common.model.room.BusArrivalEntity

@Suppress("unused")
@Dao
interface BusArrivalDao {
    @Insert
    suspend fun insertAll(entities: List<BusArrivalEntity>)

    @Update
    suspend fun updateAll(entities: List<BusArrivalEntity>)

    @Delete
    suspend fun delete(entity: BusArrivalEntity)

    @Query("DELETE from BusArrivalEntity")
    suspend fun deleteAll()

    @Query("SELECT * FROM BusArrivalEntity")
    suspend fun findAll(): List<BusArrivalEntity>

    @Query("SELECT * FROM BusArrivalEntity WHERE busServiceNumber = :busServiceNumber")
    suspend fun findByBusServiceNumber(busServiceNumber: String): List<BusArrivalEntity>

    @Query("SELECT * FROM BusArrivalEntity WHERE busServiceNumber = :busServiceNumber AND busStopCode = :busStopCode")
    suspend fun findByBusServiceNumberAndBusStopCode(
        busServiceNumber: String,
        busStopCode: String
    ): List<BusArrivalEntity>

    @Query("SELECT * FROM BusArrivalEntity WHERE busStopCode = :busStopCode")
    suspend fun findByBusStopCode(
        busStopCode: String
    ): List<BusArrivalEntity>
}