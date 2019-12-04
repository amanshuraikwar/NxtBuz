package io.github.amanshuraikwar.howmuch.data.transaction

import android.util.Log
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import io.github.amanshuraikwar.howmuch.data.googlesheetsapi.GoogleSheetsApiService
import org.threeten.bp.OffsetDateTime
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "LocalTransactionDataMan"

@Singleton
class RemoteTransactionDataManager @Inject constructor(
    private val googleSheetsApiService: GoogleSheetsApiService
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

    fun initCategoriesMetadata(spreadsheetId: String,
                                       googleAccountCredential: GoogleAccountCredential)
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

    fun initWalletMetadata(spreadsheetId: String,
                                   googleAccountCredential: GoogleAccountCredential)
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

    fun initTransactions(spreadsheetId: String,
                                 googleAccountCredential: GoogleAccountCredential,
                                 sheetTitle: String = Constants.TRANSACTIONS_SHEET_TITLE)
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

    private fun getDefaultSheetTitles()
            : List<String> {

        return listOf(
            Constants.METADATA_SHEET_TITLE,
            Constants.TRANSACTIONS_SHEET_TITLE
        )
    }

    private fun createSpreadsheetTitle() = "HowMuch-${OffsetDateTime.now().nano}"
}