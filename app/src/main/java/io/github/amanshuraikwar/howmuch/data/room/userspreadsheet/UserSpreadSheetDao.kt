package io.github.amanshuraikwar.howmuch.data.room.userspreadsheet

import androidx.room.*
import io.github.amanshuraikwar.howmuch.data.room.userspreadsheet.UserSpreadSheetEntity

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