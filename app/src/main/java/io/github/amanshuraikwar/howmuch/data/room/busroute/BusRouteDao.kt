package io.github.amanshuraikwar.howmuch.data.room.busroute

import androidx.room.*
import io.github.amanshuraikwar.howmuch.data.BusServiceNumber

@Dao
interface BusRouteDao {

    @Insert
    fun insertAll(entities: List<BusRouteEntity>)

    @Delete
    fun delete(entity: BusRouteEntity)

    @Query("DELETE from BusRouteEntity")
    fun deleteAll()

    @Query("SELECT * FROM BusRouteEntity")
    fun findAll(): List<BusRouteEntity>

    @Query("SELECT * FROM BusRouteEntity WHERE busServiceNumber = :busServiceNumber")
    fun findByBusServiceNumber(busServiceNumber: String): BusRouteEntity?
}