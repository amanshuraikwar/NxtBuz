package io.github.amanshuraikwar.howmuch.data.room.busstops

import androidx.room.*

@Dao
interface BusStopDao {

    @Insert
    fun insertAll(entities: List<BusStopEntity>)

    @Delete
    fun delete(entity: BusStopEntity)

    @Query("DELETE from BusStopEntity")
    fun deleteAll()

    @Query("SELECT * FROM BusStopEntity")
    fun findAll(): List<BusStopEntity>

    @Query("SELECT * FROM BusStopEntity WHERE code = :code")
    fun findByCode(code: String): List<BusStopEntity>

    @Query("SELECT * FROM BusStopEntity ORDER BY (((latitude-:latitude)*(latitude-:latitude))+((longitude-:longitude)*(longitude-:longitude))) ASC LIMIT :limit")
    fun findCloseLimit(latitude: Double, longitude: Double, limit: Int): List<BusStopEntity>

    @Query("SELECT * FROM BusStopEntity WHERE (((latitude-:latitude)*(latitude-:latitude))+((longitude-:longitude)*(longitude-:longitude))) < :distance")
    fun findCloseDistance(latitude: Double, longitude: Double, distance: Double): List<BusStopEntity>

    @Query("SELECT * FROM BusStopEntity WHERE description like '%' || :description || '%' limit :limit")
    fun searchLikeDescription(description: String, limit: Int): List<BusStopEntity>
}