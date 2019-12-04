package io.github.amanshuraikwar.howmuch.data.googlesheetsapi

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential

/**
 * @author Amanshu Raikwar.
 */
interface SheetsDataSource {

    companion object {
        const val VALUE_INPUT_OPTION = "RAW"
    }

    fun readSpreadSheet(
        spreadsheetId: String,
        spreadsheetRange: String,
        googleAccountCredential: GoogleAccountCredential
    ): List<List<Any>>

    fun appendToSpreadSheet(
        spreadsheetId: String,
        spreadsheetRange: String,
        valueInputOption: String = VALUE_INPUT_OPTION,
        values: List<List<Any>>,
        googleAccountCredential: GoogleAccountCredential
    ): String

    fun updateSpreadSheet(
        spreadsheetId: String,
        spreadsheetRange: String,
        valueInputOption: String = VALUE_INPUT_OPTION,
        values: List<List<Any>>,
        googleAccountCredential: GoogleAccountCredential
    ): String

    fun createSpreadSheet(
        spreadSheetTitle: String,
        sheetTitles: List<String>,
        googleAccountCredential: GoogleAccountCredential
    ): String

    fun deleteRows(
        spreadsheetId: String,
        sheetTitle: String,
        startIndex: Int,
        endIndex: Int,
        googleAccountCredential: GoogleAccountCredential
    ): String

    fun getSheetTitles(
        spreadsheetId: String,
        googleAccountCredential: GoogleAccountCredential
    ): List<String>
}