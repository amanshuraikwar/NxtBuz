package io.github.amanshuraikwar.howmuch.data.room

import android.util.Log
import io.github.amanshuraikwar.howmuch.data.model.Category
import io.github.amanshuraikwar.howmuch.data.model.Money
import io.github.amanshuraikwar.howmuch.data.model.SpreadSheetCell
import io.github.amanshuraikwar.howmuch.data.room.categories.CategoryDao
import io.github.amanshuraikwar.howmuch.data.room.categories.CategoryEntity
import io.github.amanshuraikwar.howmuch.data.room.userspreadsheet.UserSpreadSheetDao
import io.github.amanshuraikwar.howmuch.data.room.userspreadsheet.UserSpreadSheetEntity
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
        id.asSpreadSheetCell(), name, Money(monthlyLimit.toString())
    )

    private fun String.asSpreadSheetCell(): SpreadSheetCell {

        if (!this.matches(Regex("[A-Za-z\\-]+[0-9]*![A-Z]+[0-9]+:[A-Z]+"))) {
            throw IllegalArgumentException("$this is not a valid spread sheet cell id.")
        }

        val parts = mutableListOf("", "", "", "")

        var tmpParts = this.split("!")
        parts[0] = tmpParts[0]

        tmpParts = tmpParts[1].split(":")
        parts[3] = tmpParts[1]

        val regex = "[A-Z]+|[0-9]+".toRegex()
        var match =
            regex.find(tmpParts[0])
                ?: throw IllegalArgumentException("$this is not a valid spread sheet cell id.")

        parts[1] = tmpParts[0].substring(match.range)
        match =
            match.next()
                ?: throw IllegalArgumentException("$this is not a valid spread sheet cell id.")

        parts[2] = tmpParts[0].substring(match.range)

        return SpreadSheetCell(
            parts[0],
            parts[1],
            try {
                parts[2].toInt()
            } catch (e: NumberFormatException) {
                throw IllegalArgumentException("$this is not a valid spread sheet cell id.")
            },
            parts[3]
        )
    }

    private fun Category.asCategoryEntity() : CategoryEntity = CategoryEntity(
        id, name, monthlyLimit.amount
    )
}