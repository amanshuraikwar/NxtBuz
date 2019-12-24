package io.github.amanshuraikwar.howmuch.data.room

import io.github.amanshuraikwar.howmuch.data.model.Category
import io.github.amanshuraikwar.howmuch.data.model.Transaction
import io.github.amanshuraikwar.howmuch.data.room.categories.CategoryDao
import io.github.amanshuraikwar.howmuch.data.room.transactions.TransactionDao
import io.github.amanshuraikwar.howmuch.data.room.transactions.TransactionEntity
import io.github.amanshuraikwar.howmuch.data.room.userspreadsheet.UserSpreadSheetDao
import io.github.amanshuraikwar.howmuch.data.room.userspreadsheet.UserSpreadSheetEntity
import io.github.amanshuraikwar.howmuch.util.ColorUtil
import io.github.amanshuraikwar.howmuch.util.asCategory
import io.github.amanshuraikwar.howmuch.util.asCategoryEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoomDataSource @Inject constructor(
    private val spreadSheetDao: UserSpreadSheetDao,
    private val categoryDao: CategoryDao,
    private val transactionDao: TransactionDao,
    private val colorUtil: ColorUtil
) {

    fun getSpreadsheetIdForEmail(email: String): String? =
        spreadSheetDao.findByEmail(email)?.spreadsheetId

    fun addSpreadsheetIdForEmail(spreadsheetId: String, email: String) {

        var spreadsheet: UserSpreadSheetEntity? = spreadSheetDao.findByEmail(email)

        if (spreadsheet == null) {
            // if null insert
            spreadsheet =
                UserSpreadSheetEntity(
                    email,
                    spreadsheetId
                )
            spreadSheetDao.insertAll(spreadsheet)
        } else {
            // else update
            spreadsheet.spreadsheetId = spreadsheetId
            spreadSheetDao.updateAll(spreadsheet)
        }
    }

    fun getCategories(): List<Category> {
        return categoryDao.findAll().map { it.asCategory(colorUtil) }
    }

    fun refreshCategories(categories: List<Category>) {
        categoryDao.deleteAll()
        categoryDao.insertAll(categories.map { it.asCategoryEntity() })
    }

    fun areTransactionsEmpty(): Boolean {
        return transactionDao.getCount() == 0
    }

    fun getTransactionsAfter(dateTimeMillisec: Long): List<TransactionEntity> {
        return transactionDao.findAfter(dateTimeMillisec)
    }

    fun addTransaction(transactionEntity: TransactionEntity) {
        transactionDao.insertAll(listOf(transactionEntity))
    }
}