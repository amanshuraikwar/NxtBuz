package io.github.amanshuraikwar.nxtbuz.data.room.categories

import androidx.room.*

@Dao
interface CategoryDao {

    @Insert
    fun insertAll(categoryEntities: List<CategoryEntity>)

    @Delete
    fun delete(categoryEntity: CategoryEntity)

    @Query("DELETE from CategoryEntity")
    fun deleteAll()

    @Update
    fun updateAll(vararg categoryEntities: CategoryEntity)

    @Query("SELECT * FROM CategoryEntity")
    fun findAll(): List<CategoryEntity>

    @Query("SELECT * FROM CategoryEntity WHERE id = :id")
    fun findById(id: String): CategoryEntity?
}