package io.github.amanshuraikwar.nxtbuz.data.transaction

import android.util.Log
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import io.github.amanshuraikwar.nxtbuz.data.di.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.data.googlesheetsapi.GoogleSheetsApiService
import io.github.amanshuraikwar.nxtbuz.data.model.*
import io.github.amanshuraikwar.nxtbuz.util.ColorUtil
import kotlinx.coroutines.*
import org.threeten.bp.OffsetDateTime
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "LocalTransactionDataMan"

@Singleton
class RemoteTransactionDataManager @Inject constructor(
    private val googleSheetsApiService: GoogleSheetsApiService,
    private val dispatcherProvider: CoroutinesDispatcherProvider,
    private val colorUtil: ColorUtil
) {

    // creates a new spreadsheet & metadata and transaction sheet
    fun createNewSpreadSheet(
        googleAccountCredential: GoogleAccountCredential
    ): String {

        return googleSheetsApiService.createSpreadSheet(
            spreadSheetTitle = createSpreadsheetTitle(),
            sheetTitles = getDefaultSheetTitles(),
            googleAccountCredential = googleAccountCredential
        )
    }

    fun initCategoriesMetadata(
        spreadsheetId: String,
        googleAccountCredential: GoogleAccountCredential
    ): String {

        Log.i(TAG, "initCategoriesMetadata: Started ${Thread.currentThread().name}")

        googleSheetsApiService.updateSpreadSheet(
            spreadsheetId = spreadsheetId,
            spreadsheetRange = Constants.CATEGORIES_SPREAD_SHEET_RANGE_WITH_HEADING,
            values = Constants.DEFAULT_CATEGORIES_WITH_HEADING,
            googleAccountCredential = googleAccountCredential
        )

        Log.i(TAG, "initCategoriesMetadata: Completed ${Thread.currentThread().name}")

        return spreadsheetId
    }

    fun initTransactions(
        spreadsheetId: String,
        googleAccountCredential: GoogleAccountCredential,
        sheetTitle: String = Constants.TRANSACTIONS_SHEET_TITLE
    ): String {

        Log.i(TAG, "initTransactions: Started ${Thread.currentThread().name}")

        googleSheetsApiService.updateSpreadSheet(
            spreadsheetId = spreadsheetId,
            spreadsheetRange = "$sheetTitle!${Constants.TRANSACTIONS_CELL_RANGE_WITH_HEADING}",
            values = Constants.TRANSACTIONS_HEADING,
            googleAccountCredential = googleAccountCredential
        )

        Log.i(TAG, "initTransactions: Completed ${Thread.currentThread().name}")

        return spreadsheetId
    }

    suspend fun fetchCategories(
        spreadsheetId: String,
        googleAccountCredential: GoogleAccountCredential
    ): List<Category> = withContext(dispatcherProvider.io) {

        return@withContext googleSheetsApiService
            .readSpreadSheet(
                spreadsheetId = spreadsheetId,
                spreadsheetRange = Constants.CATEGORIES_SPREAD_SHEET_RANGE_WITHOUT_HEADING,
                googleAccountCredential = googleAccountCredential
            )
            .takeIf {
                it.isNotEmpty()
            }
            ?.mapIndexed { i, item ->
                async {
                    item.asCategory(i, spreadsheetId)
                }
            }
            ?.awaitAll()
            ?: throw SpreadSheetException.NoCategoriesFound(spreadsheetId)
    }

    private fun List<Any>.asCategory(
        index: Int,
        spreadsheetId: String // required for throwing exception
    ): Category {

        val cell = SpreadSheetCell(
            Constants.METADATA_SHEET_TITLE,
            Constants.CATEGORIES_START_COL.toString(),
            Constants.CATEGORIES_START_ROW_WITHOUT_HEADING + index,
            Constants.CATEGORIES_END_COL.toString()
        )

        return try {
            Category(
                cell = cell,
                name = this[0].toString(),
                monthlyLimit = Money(this[1].toString()),
                color = colorUtil.getCategoryColor(this[0].toString())
            )
        } catch (e: IndexOutOfBoundsException) {
            throw SpreadSheetException.InvalidCategory(cell, spreadsheetId)
        }
    }

    suspend fun fetchTransactions(
        spreadsheetId: String,
        googleAccountCredential: GoogleAccountCredential,
        sheetTitle: String = Constants.TRANSACTIONS_SHEET_TITLE
    ): List<SpreadSheetTransaction> = withContext(dispatcherProvider.io) {

        return@withContext googleSheetsApiService
            .readSpreadSheet(
                spreadsheetId = spreadsheetId,
                spreadsheetRange =
                "$sheetTitle!${Constants.TRANSACTIONS_CELL_RANGE_WITHOUT_HEADING}",
                googleAccountCredential = googleAccountCredential
            )
            .mapIndexed { i, item ->
                async {
                    item.toTransaction(
                        Constants.TRANSACTION_START_ROW_WITHOUT_HEADING + i,
                        sheetTitle,
                        spreadsheetId
                    )
                }
            }
            .awaitAll()
    }

    private fun List<Any>.toTransaction(
        cellPosition: Int,
        sheetTitle: String,
        spreadsheetId: String // required for throwing exception
    ): SpreadSheetTransaction {

        val cell = SpreadSheetCell(
            sheetTitle,
            Constants.TRANSACTION_START_COL.toString(),
            cellPosition,
            Constants.TRANSACTION_END_COL.toString()
        )

        if (this.size != Constants.TRANSACTION_ROW_COLUMN_COUNT) {

            val errorMessage = "Transaction entry at cell position $cellPosition" +
                    " has only ${this.size} column entries" +
                    " instead of ${Constants.TRANSACTION_ROW_COLUMN_COUNT}."

            throw SpreadSheetException.InvalidTransaction(
                cell,
                spreadsheetId,
                errorMessage
            )
        }

        val amount: Double

        try {

            amount = this[1].toString().toDouble()

        } catch (e: NumberFormatException) {

            val errorMessage = "Transaction entry at $cell" +
                    " of sheet $sheetTitle" +
                    " has invalid amount ${this[2]}."

            throw SpreadSheetException.InvalidTransaction(
                cell,
                spreadsheetId,
                errorMessage
            )
        }

        return SpreadSheetTransaction(
            cell = cell,
            datetime = this[0].toString().toLong(),
            amount = Money(amount.toString()),
            title = this[2].toString(),
            categoryId = this[3].toString()
        )
    }

    private fun getDefaultSheetTitles(): List<String> {

        return listOf(
            Constants.METADATA_SHEET_TITLE,
            Constants.TRANSACTIONS_SHEET_TITLE
        )
    }

    private fun createSpreadsheetTitle() = "HowMuch-${OffsetDateTime.now().toEpochSecond()}"
}