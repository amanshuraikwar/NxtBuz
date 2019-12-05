package io.github.amanshuraikwar.howmuch.data.transaction

import android.util.Log
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import io.github.amanshuraikwar.howmuch.data.di.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.howmuch.data.googlesheetsapi.GoogleSheetsApiService
import io.github.amanshuraikwar.howmuch.data.model.Category
import io.github.amanshuraikwar.howmuch.data.model.Money
import kotlinx.coroutines.*
import org.threeten.bp.OffsetDateTime
import java.lang.IllegalStateException
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "LocalTransactionDataMan"

@Singleton
class RemoteTransactionDataManager @Inject constructor(
    private val googleSheetsApiService: GoogleSheetsApiService,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) {

    // creates a new spreadsheet & metadata and transaction sheet
    fun createNewSpreadSheet(googleAccountCredential: GoogleAccountCredential)
            : String {

        return googleSheetsApiService.createSpreadSheet(
            spreadSheetTitle = createSpreadsheetTitle(),
            sheetTitles = getDefaultSheetTitles(),
            googleAccountCredential = googleAccountCredential
        )
    }

    fun initCategoriesMetadata(
        spreadsheetId: String,
        googleAccountCredential: GoogleAccountCredential
    )
            : String {

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

    fun initWalletMetadata(
        spreadsheetId: String,
        googleAccountCredential: GoogleAccountCredential
    )
            : String {

        Log.i(TAG, "initWalletMetadata: Started ${Thread.currentThread().name}")

        googleSheetsApiService
            .updateSpreadSheet(
                spreadsheetId = spreadsheetId,
                spreadsheetRange = Constants.WALLETS_SPREAD_SHEET_RANGE_WITH_HEADING,
                values = Constants.DEFAULT_WALLETS_WITH_HEADING,
                googleAccountCredential = googleAccountCredential
            )

        Log.i(TAG, "initWalletMetadata: Completed ${Thread.currentThread().name}")

        return spreadsheetId
    }

    fun initTransactions(
        spreadsheetId: String,
        googleAccountCredential: GoogleAccountCredential,
        sheetTitle: String = Constants.TRANSACTIONS_SHEET_TITLE
    )
            : String {

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

    //region categories
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
                    try {
                        item.asCategory(i)
                    } catch (e: IndexOutOfBoundsException) {
                        throw SpreadSheetException.InvalidCategoryException(spreadsheetId)
                    }
                }
            }
            ?.awaitAll()
            ?: throw SpreadSheetException.NoCategoriesFound(spreadsheetId)
    }

    private fun List<Any>.asCategory(index: Int): Category {
        return Category(
            id = "${Constants.METADATA_SHEET_TITLE}!${Constants.CATEGORIES_START_COL}${Constants.CATEGORIES_START_ROW_WITHOUT_HEADING + index}:${Constants.CATEGORIES_END_COL}",
            name = this[0].toString(),
            monthlyLimit = Money(this[3].toString())
        )
    }

    private fun getDefaultSheetTitles()
            : List<String> {

        return listOf(
            Constants.METADATA_SHEET_TITLE,
            Constants.TRANSACTIONS_SHEET_TITLE
        )
    }

    private fun createSpreadsheetTitle() = "HowMuch-${OffsetDateTime.now().toEpochSecond()}"
}

sealed class SpreadSheetException(
    val spreadsheetId: String,
    msg: String
) : IllegalStateException(msg) {

    class NoCategoriesFound(
        spreadsheetId: String
    ) : SpreadSheetException(spreadsheetId, "No categories found in the spread sheet.")

    class InvalidCategoryException(
        spreadsheetId: String
    ) : SpreadSheetException(spreadsheetId, "Invalid category found in the spread sheet.")
}