package io.github.amanshuraikwar.nxtbuz.data.googlesheetsapi

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.HttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.model.*
import com.google.api.services.sheets.v4.model.DimensionRange
import com.google.api.services.sheets.v4.model.DeleteDimensionRequest
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest
import io.github.amanshuraikwar.nxtbuz.BuildConfig
import javax.inject.Inject
import javax.inject.Singleton

const val VALUE_INPUT_OPTION = "RAW"

@Singleton
class GoogleSheetsApiService @Inject constructor(
    private val transport: HttpTransport,
    private val jsonFactory: JsonFactory
) {

    fun readSpreadSheet(
        spreadsheetId: String,
        spreadsheetRange: String,
        googleAccountCredential: GoogleAccountCredential
    ): List<List<Any>> {

        val sheetsAPI =
            Sheets
                .Builder(
                    transport,
                    jsonFactory,
                    googleAccountCredential
                )
                .setApplicationName(BuildConfig.APPLICATION_ID)
                .build()

        return sheetsAPI.spreadsheets().values()
            .get(spreadsheetId, spreadsheetRange)
            .execute()
            .getValues()
            ?: listOf()
    }

    fun appendToSpreadSheet(
        spreadsheetId: String,
        spreadsheetRange: String,
        valueInputOption: String,
        values: List<List<Any>>,
        googleAccountCredential: GoogleAccountCredential
    ): String {

        val sheetsAPI =
            Sheets
                .Builder(
                    transport,
                    jsonFactory,
                    googleAccountCredential
                )
                .setApplicationName(BuildConfig.APPLICATION_ID)
                .build()

        val body = ValueRange().setValues(values)

        sheetsAPI
            .spreadsheets()
            .values()
            .append(spreadsheetId, spreadsheetRange, body)
            .setValueInputOption(valueInputOption)
            .execute()

        return spreadsheetId
    }

    fun updateSpreadSheet(
        spreadsheetId: String,
        spreadsheetRange: String,
        valueInputOption: String = VALUE_INPUT_OPTION,
        values: List<List<Any>>,
        googleAccountCredential: GoogleAccountCredential
    ): String {

        val sheetsAPI =
            Sheets
                .Builder(
                    transport,
                    jsonFactory,
                    googleAccountCredential
                )
                .setApplicationName(BuildConfig.APPLICATION_ID)
                .build()



        val body = ValueRange().setValues(values)

        sheetsAPI
            .spreadsheets()
            .values()
            .update(spreadsheetId, spreadsheetRange, body)
            .setValueInputOption(valueInputOption)
            .execute()

        return spreadsheetId
    }

    fun createSpreadSheet(
        spreadSheetTitle: String,
        sheetTitles: List<String>,
        googleAccountCredential: GoogleAccountCredential
    ): String {

        val sheetsAPI =
            Sheets
                .Builder(
                    transport,
                    jsonFactory,
                    googleAccountCredential
                )
                .setApplicationName(BuildConfig.APPLICATION_ID)
                .build()

        val newSpreadSheet = Spreadsheet()
        newSpreadSheet.properties = SpreadsheetProperties().setTitle(spreadSheetTitle)

        val sheetProperties = SheetProperties()
        sheetProperties.title = "hello hello"

        val sheets = mutableListOf<Sheet>()

        sheetTitles.forEach { sheetTitle ->
            val sheet = Sheet()
            sheet.properties = SheetProperties().setTitle(sheetTitle)
            sheets.add(sheet)
        }

        newSpreadSheet.sheets = sheets

        val response =
            sheetsAPI
                .spreadsheets()
                .create(newSpreadSheet)
                .execute()

        return response.spreadsheetId
    }

    fun deleteRows(
        spreadsheetId: String,
        sheetTitle: String,
        startIndex: Int,
        endIndex: Int,
        googleAccountCredential: GoogleAccountCredential
    ): String {

        val sheetsAPI =
            Sheets
                .Builder(
                    transport,
                    jsonFactory,
                    googleAccountCredential
                )
                .setApplicationName(BuildConfig.APPLICATION_ID)
                .build()



        val spreadsheet = sheetsAPI.spreadsheets().get(spreadsheetId).execute()

        val content = BatchUpdateSpreadsheetRequest()
        val request = Request()
        val deleteDimensionRequest = DeleteDimensionRequest()
        val dimensionRange = DimensionRange()
        dimensionRange.dimension = "ROWS"
        dimensionRange.startIndex = startIndex
        dimensionRange.endIndex = endIndex

        dimensionRange.sheetId =
            spreadsheet
                .sheets
                .find { it.properties.title == sheetTitle }!!
                .properties.sheetId

        deleteDimensionRequest.range = dimensionRange

        request.deleteDimension = deleteDimensionRequest

        content.requests = listOf(request)

        sheetsAPI.spreadsheets().batchUpdate(spreadsheetId, content).execute()

        return spreadsheetId
    }

    fun getSheetTitles(
        spreadsheetId: String,
        googleAccountCredential: GoogleAccountCredential
    ): List<String> {

        val sheetsAPI =
            Sheets
                .Builder(
                    transport,
                    jsonFactory,
                    googleAccountCredential
                )
                .setApplicationName("test")
                .build()

        return sheetsAPI
                .spreadsheets()
                .get(spreadsheetId)
                .execute()
                .sheets
                .map { it.properties.title }
    }
}