package io.github.amanshuraikwar.howmuch.data.model

data class SpreadSheetTransaction(
    val id: String,
    var date: String,
    var time: String,
    var amount: Money,
    var title: String,
    val categoryId: String
)