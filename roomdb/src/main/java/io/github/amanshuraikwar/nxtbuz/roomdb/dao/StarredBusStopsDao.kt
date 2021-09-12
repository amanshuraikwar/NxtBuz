package io.github.amanshuraikwar.nxtbuz.roomdb.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import io.github.amanshuraikwar.nxtbuz.roomdb.model.StarredBusStopRoomDbEntity

@Suppress("unused")
@Dao
interface StarredBusStopsDao {
    @Insert
    fun insertAll(entities: List<StarredBusStopRoomDbEntity>)

    @Delete
    fun delete(entity: StarredBusStopRoomDbEntity)

    @Query("DELETE from StarredBusStopEntity")
    fun deleteAll()

    @Query("SELECT * FROM StarredBusStopEntity")
    suspend fun findAll(): List<StarredBusStopRoomDbEntity>

    @Query("SELECT * FROM StarredBusStopEntity WHERE busStopCode = :busStopCode")
    suspend fun findByBusStopCode(busStopCode: String): List<StarredBusStopRoomDbEntity>

    @Query("SELECT * FROM StarredBusStopEntity WHERE busStopCode = :busStopCode AND busServiceNumber = :busServiceNumber ")
    suspend fun findByBusStopCodeAndBusServiceNumber(
        busStopCode: String,
        busServiceNumber: String
    ): List<StarredBusStopRoomDbEntity>

    @Query("DELETE FROM StarredBusStopEntity WHERE busStopCode = :busStopCode AND busServiceNumber = :busServiceNumber ")
    fun deleteByBusStopCodeAndBusServiceNumber(busStopCode: String, busServiceNumber: String)
}