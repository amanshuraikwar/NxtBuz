//package io.github.amanshuraikwar.nxtbuz.data.room.dao
//
//import androidx.room.Dao
//import androidx.room.Delete
//import androidx.room.Insert
//import androidx.room.Query
//import io.github.amanshuraikwar.nxtbuz.roomdb.model.OperatingBusEntity
//
//@Dao
//interface OperatingBusDao {
//
//    @Insert
//    suspend fun insertAll(entities: List<OperatingBusEntity>)
//
//    @Delete
//    suspend fun delete(entity: OperatingBusEntity)
//
//    @Query("DELETE from OperatingBusEntity")
//    suspend fun deleteAll()
//
//    @Query("SELECT * FROM OperatingBusEntity")
//    suspend fun findAll(): List<OperatingBusEntity>
//
//    @Query("SELECT * FROM OperatingBusEntity WHERE busStopCode = :busStopCode")
//    suspend fun findByBusStopCode(busStopCode: String): List<OperatingBusEntity>
//
//    @Query("SELECT * FROM OperatingBusEntity WHERE busStopCode = :busStopCode AND busServiceNumber = :busServiceNumber")
//    suspend fun findByBusStopCodeAndBusServiceNumber(
//        busStopCode: String,
//        busServiceNumber: String
//    ): List<OperatingBusEntity>
//}