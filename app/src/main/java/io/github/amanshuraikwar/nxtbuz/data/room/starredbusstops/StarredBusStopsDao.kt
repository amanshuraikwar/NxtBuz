package io.github.amanshuraikwar.nxtbuz.data.room.starredbusstops

import androidx.room.*

@Dao
interface StarredBusStopsDao {

    @Insert
    fun insertAll(entities: List<StarredBusStopEntity>)

    @Delete
    fun delete(entity: StarredBusStopEntity)

    @Query("DELETE from StarredBusStopEntity")
    fun deleteAll()

    @Query("SELECT * FROM StarredBusStopEntity")
    fun findAll(): List<StarredBusStopEntity>

    @Query("SELECT * FROM StarredBusStopEntity WHERE busStopCode = :busStopCode")
    suspend fun findByBusStopCode(busStopCode: String): List<StarredBusStopEntity>

    @Query("SELECT * FROM StarredBusStopEntity WHERE busStopCode = :busStopCode AND busServiceNumber = :busServiceNumber ")
    suspend fun findByBusStopCodeAndBusServiceNumber(busStopCode: String, busServiceNumber: String): List<StarredBusStopEntity>

    @Query("DELETE FROM StarredBusStopEntity WHERE busStopCode = :busStopCode AND busServiceNumber = :busServiceNumber ")
    fun deleteByBusStopCodeAndBusServiceNumber(busStopCode: String, busServiceNumber: String)
}