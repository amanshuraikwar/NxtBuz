package io.github.amanshuraikwar.nxtbuz.data.room.busstops

import androidx.room.*

@Dao
interface BusStopDao {

    @Insert
    suspend fun insertAll(entities: List<BusStopEntity>)

    @Query("DELETE from BusStopEntity")
    suspend fun deleteAll()

    @Query("SELECT * FROM BusStopEntity")
    suspend fun findAll(): List<BusStopEntity>

    @Query("SELECT * FROM BusStopEntity WHERE code = :code")
    suspend fun findByCode(code: String): List<BusStopEntity>

    @Query("SELECT * FROM BusStopEntity ORDER BY (((latitude-:latitude)*(latitude-:latitude))+((longitude-:longitude)*(longitude-:longitude))) ASC LIMIT :limit")
    suspend fun findCloseLimit(latitude: Double, longitude: Double, limit: Int): List<BusStopEntity>

    @Query("SELECT * FROM BusStopEntity WHERE (((latitude-:latitude)*(latitude-:latitude))+((longitude-:longitude)*(longitude-:longitude))) < :distance")
    suspend fun findCloseDistance(latitude: Double, longitude: Double, distance: Double): List<BusStopEntity>

    @Query("SELECT * FROM BusStopEntity WHERE description like '%' || :description || '%' limit :limit")
    suspend fun searchLikeDescription(description: String, limit: Int): List<BusStopEntity>
}