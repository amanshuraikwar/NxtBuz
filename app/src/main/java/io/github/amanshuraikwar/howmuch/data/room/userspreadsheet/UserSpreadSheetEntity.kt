package io.github.amanshuraikwar.howmuch.data.room.userspreadsheet

import androidx.room.Entity

@Entity(primaryKeys = ["email"])
data class UserSpreadSheetEntity(
    var email: String,
    var spreadsheetId: String
)