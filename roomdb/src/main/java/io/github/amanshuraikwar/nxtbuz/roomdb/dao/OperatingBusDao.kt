package io.github.amanshuraikwar.nxtbuz.roomdb.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import io.github.amanshuraikwar.nxtbuz.roomdb.model.OperatingBusRoomDbEntity

@Suppress("unused")
@Dao
interface OperatingBusDao {
    @Insert
    suspend fun insertAll(entities: List<OperatingBusRoomDbEntity>)

    @Delete
    suspend fun delete(entity: OperatingBusRoomDbEntity)

    @Query("DELETE from OperatingBusEntity")
    suspend fun deleteAll()

    @Query("SELECT * FROM OperatingBusEntity")
    suspend fun findAll(): List<OperatingBusRoomDbEntity>

    @Query("SELECT * FROM OperatingBusEntity WHERE busStopCode = :busStopCode")
    suspend fun findByBusStopCode(busStopCode: String): List<OperatingBusRoomDbEntity>

    @Query("SELECT * FROM OperatingBusEntity WHERE busStopCode = :busStopCode AND busServiceNumber = :busServiceNumber")
    suspend fun findByBusStopCodeAndBusServiceNumber(
        busStopCode: String,
        busServiceNumber: String
    ): List<OperatingBusRoomDbEntity>
}