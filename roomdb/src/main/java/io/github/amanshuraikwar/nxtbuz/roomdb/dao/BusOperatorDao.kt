package io.github.amanshuraikwar.nxtbuz.roomdb.dao

import androidx.room.*
import io.github.amanshuraikwar.nxtbuz.roomdb.model.BusOperatorRoomDbEntity

@Suppress("unused")
@Dao
interface BusOperatorDao {
    @Insert
    suspend fun insertAll(entities: List<BusOperatorRoomDbEntity>)

    @Update
    suspend fun updateAll(entities: List<BusOperatorRoomDbEntity>)

    @Delete
    suspend fun delete(entity: BusOperatorRoomDbEntity)

    @Query("DELETE from BusOperatorEntity")
    suspend fun deleteAll()

    @Query("SELECT * FROM BusOperatorEntity")
    suspend fun findAll(): List<BusOperatorRoomDbEntity>

    @Query("SELECT * FROM BusOperatorEntity WHERE busServiceNumber = :busServiceNumber")
    suspend fun findByBusServiceNumber(busServiceNumber: String): List<BusOperatorRoomDbEntity>

    @Query("SELECT * FROM BusOperatorEntity WHERE busServiceNumber = :busServiceNumber AND busStopCode = :busStopCode")
    suspend fun findByBusServiceNumberAndBusStopCode(
        busServiceNumber: String,
        busStopCode: String
    ): List<BusOperatorRoomDbEntity>
}