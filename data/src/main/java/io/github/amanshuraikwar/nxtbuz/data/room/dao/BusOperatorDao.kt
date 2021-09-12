//package io.github.amanshuraikwar.nxtbuz.data.room.dao
//
//import androidx.room.*
//import io.github.amanshuraikwar.nxtbuz.roomdb.model.BusOperatorEntity
//
//@Suppress("unused")
//@Dao
//interface BusOperatorDao {
//    @Insert
//    suspend fun insertAll(entities: List<BusOperatorEntity>)
//
//    @Update
//    suspend fun updateAll(entities: List<BusOperatorEntity>)
//
//    @Delete
//    suspend fun delete(entity: BusOperatorEntity)
//
//    @Query("DELETE from BusOperatorEntity")
//    suspend fun deleteAll()
//
//    @Query("SELECT * FROM BusOperatorEntity")
//    suspend fun findAll(): List<BusOperatorEntity>
//
//    @Query("SELECT * FROM BusOperatorEntity WHERE busServiceNumber = :busServiceNumber")
//    suspend fun findByBusServiceNumber(busServiceNumber: String): List<BusOperatorEntity>
//
//    @Query("SELECT * FROM BusOperatorEntity WHERE busServiceNumber = :busServiceNumber AND busStopCode = :busStopCode")
//    suspend fun findByBusServiceNumberAndBusStopCode(
//        busServiceNumber: String,
//        busStopCode: String
//    ): List<BusOperatorEntity>
//}