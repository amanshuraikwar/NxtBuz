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
    fun findByCode(code: String): BusStopEntity?

    @Query("SELECT * FROM BusStopEntity ORDER BY (((latitude-:latitude)*(latitude-:latitude))+((longitude-:longitude)*(longitude-:longitude))) ASC LIMIT :limit")
    fun findClose(latitude: Double, longitude: Double, limit: Int): List<BusStopEntity>
}