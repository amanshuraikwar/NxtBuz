package io.github.amanshuraikwar.nxtbuz.data.room.userspreadsheet

import androidx.room.Entity

@Entity(primaryKeys = ["email"])
data class UserSpreadSheetEntity(
    var email: String,
    var spreadsheetId: String
)