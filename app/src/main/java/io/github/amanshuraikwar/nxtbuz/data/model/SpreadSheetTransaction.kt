package io.github.amanshuraikwar.nxtbuz.data.model

data class SpreadSheetTransaction(
    val cell: SpreadSheetCell,
    var datetime: Long,
    var amount: Money,
    var title: String,
    val categoryId: String
)