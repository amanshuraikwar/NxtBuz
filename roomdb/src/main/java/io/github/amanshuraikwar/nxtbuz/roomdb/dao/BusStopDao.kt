package io.github.amanshuraikwar.nxtbuz.roomdb.dao

import androidx.room.*
import io.github.amanshuraikwar.nxtbuz.roomdb.model.BusStopRoomDbEntity

@Suppress("unused")
@Dao
internal interface BusStopDao {
    @Insert
    suspend fun insertAll(entities: List<BusStopRoomDbEntity>)

    @Query("DELETE from BusStopEntity")
    suspend fun deleteAll()

    @Query("SELECT * FROM BusStopEntity")
    suspend fun findAll(): List<BusStopRoomDbEntity>

    @Query("SELECT * FROM BusStopEntity WHERE code = :code")
    suspend fun findByCode(code: String): List<BusStopRoomDbEntity>

    @Query("SELECT * FROM BusStopEntity ORDER BY (((latitude-:latitude)*(latitude-:latitude))+((longitude-:longitude)*(longitude-:longitude))) ASC LIMIT :limit")
    suspend fun findClose(latitude: Double, longitude: Double, limit: Int): List<BusStopRoomDbEntity>

    @Query("SELECT * FROM BusStopEntity WHERE (((latitude-:latitude)*(latitude-:latitude))+((longitude-:longitude)*(longitude-:longitude))) < :distance")
    suspend fun findCloseDistance(latitude: Double, longitude: Double, distance: Double): List<BusStopRoomDbEntity>

    @Query("SELECT * FROM BusStopEntity WHERE description like '%' || :description || '%' limit :limit")
    suspend fun searchLikeDescription(description: String, limit: Int): List<BusStopRoomDbEntity>
}