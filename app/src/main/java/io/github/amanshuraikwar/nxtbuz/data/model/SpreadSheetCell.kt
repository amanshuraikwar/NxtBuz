package io.github.amanshuraikwar.nxtbuz.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SpreadSheetCell(
    val sheetTitle: String,
    val startColumn: String,
    val cellYPosition: Int,
    val endColumn: String
) : Parcelable {
    override fun toString(): String {
        return "$sheetTitle!" +
                "$startColumn$cellYPosition:" +
                endColumn
    }
}