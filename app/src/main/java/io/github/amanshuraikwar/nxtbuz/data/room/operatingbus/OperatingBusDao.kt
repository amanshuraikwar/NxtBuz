package io.github.amanshuraikwar.nxtbuz.data.room.operatingbus

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface OperatingBusDao {

    @Insert
    fun insertAll(entities: List<OperatingBusEntity>)

    @Delete
    fun delete(entity: OperatingBusEntity)

    @Query("DELETE from OperatingBusEntity")
    fun deleteAll()

    @Query("SELECT * FROM OperatingBusEntity")
    fun findAll(): List<OperatingBusEntity>

    @Query("SELECT * FROM OperatingBusEntity WHERE busStopCode = :busStopCode")
    suspend fun findByBusStopCode(busStopCode: String): List<OperatingBusEntity>

    @Query("SELECT * FROM OperatingBusEntity WHERE busStopCode = :busStopCode AND busServiceNumber = :busServiceNumber")
    suspend fun findByBusStopCodeAndBusServiceNumber(
        busStopCode: String,
        busServiceNumber: String
    ): List<OperatingBusEntity>
}