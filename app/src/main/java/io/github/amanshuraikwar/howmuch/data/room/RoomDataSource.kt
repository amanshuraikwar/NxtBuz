package io.github.amanshuraikwar.howmuch.data.room

import io.github.amanshuraikwar.howmuch.data.model.Category
import io.github.amanshuraikwar.howmuch.data.model.Money
import io.github.amanshuraikwar.howmuch.data.room.categories.CategoryDao
import io.github.amanshuraikwar.howmuch.data.room.categories.CategoryEntity
import io.github.amanshuraikwar.howmuch.data.room.userspreadsheet.UserSpreadSheetEntity
import io.github.amanshuraikwar.howmuch.data.room.userspreadsheet.UserSpreadSheetDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoomDataSource @Inject constructor(
    private val spreadSheetDao: UserSpreadSheetDao,
    private val categoryDao: CategoryDao
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
        return categoryDao.findAll().map { it.asCategory() }
    }

    fun refreshCategories(categories: List<Category>) {
        categoryDao.deleteAll()
        categoryDao.insertAll(categories.map { it.asCategoryEntity() })
    }

    private fun CategoryEntity.asCategory() : Category = Category(
        id, name, Money(monthlyLimit.toString())
    )

    private fun Category.asCategoryEntity() : CategoryEntity = CategoryEntity(
        id, name, monthlyLimit.amount
    )
}