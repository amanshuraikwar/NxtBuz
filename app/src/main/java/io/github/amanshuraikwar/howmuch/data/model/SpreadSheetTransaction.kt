package io.github.amanshuraikwar.howmuch.data.model

data class SpreadSheetTransaction(
    val cell: SpreadSheetCell,
    var datetime: Long,
    var amount: Money,
    var title: String,
    val categoryId: String
)