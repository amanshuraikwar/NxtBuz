package io.github.amanshuraikwar.nxtbuz.data.room.transactions

import androidx.room.*

@Dao
interface TransactionDao {

    @Insert
    fun insertAll(transactionEntities: List<TransactionEntity>)

    @Delete
    fun delete(transactionEntity: TransactionEntity)

    @Query("DELETE from TransactionEntity")
    fun deleteAll()

    @Update
    fun updateAll(vararg transactionEntities: TransactionEntity)

    @Query("SELECT * FROM TransactionEntity")
    fun findAll(): List<TransactionEntity>

    @Query("SELECT * FROM TransactionEntity WHERE id = :id")
    fun findById(id: String): List<TransactionEntity>

    @Query("SELECT count(*) FROM TransactionEntity")
    fun getCount(): Int

    @Query("SELECT * FROM TransactionEntity WHERE datetime >= :dateTimeMillisec")
    fun findAfter(dateTimeMillisec: Long): List<TransactionEntity>
}