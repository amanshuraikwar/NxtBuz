package io.github.amanshuraikwar.nxtbuz.data.room.userspreadsheet

import androidx.room.*

@Dao
interface UserSpreadSheetDao {

    @Insert
    fun insertAll(vararg userSpreadSheetEntities: UserSpreadSheetEntity)

    @Delete
    fun deleteAll(vararg userSpreadSheetEntities: UserSpreadSheetEntity)

    @Update
    fun updateAll(vararg userSpreadSheetEntities: UserSpreadSheetEntity)

    @Query("SELECT * FROM UserSpreadSheetEntity")
    fun findAll(): List<UserSpreadSheetEntity>

    @Query("SELECT * FROM UserSpreadSheetEntity WHERE email = :email")
    fun findByEmail(email: String): UserSpreadSheetEntity?
}