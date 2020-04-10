package io.github.amanshuraikwar.nxtbuz.data.transaction

import io.github.amanshuraikwar.nxtbuz.data.model.SpreadSheetCell
import java.lang.IllegalStateException

sealed class SpreadSheetException(
    val spreadsheetId: String,
    msg: String
) : IllegalStateException(msg) {

    class NoCategoriesFound(
        spreadsheetId: String
    ) : SpreadSheetException(spreadsheetId, "No categories found in the spread sheet.")

    class InvalidCategory(
        val cell: SpreadSheetCell,
        spreadsheetId: String
    ) : SpreadSheetException(spreadsheetId, "Invalid category found in the spread sheet.")

    class InvalidTransaction(
        val cell: SpreadSheetCell,
        spreadsheetId: String,
        msg: String
    ) : SpreadSheetException(spreadsheetId, msg)
}