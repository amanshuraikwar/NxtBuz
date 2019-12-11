package io.github.amanshuraikwar.howmuch.data.room.transactions

import androidx.room.Entity

@Entity(primaryKeys = ["id"])
data class TransactionEntity(
    val id: String,
    var datetime: Long,
    var amount: Double,
    var title: String,
    val categoryId: String,
    val spreadSheetSyncStatus: SpreadSheetSyncStatus
)

enum class SpreadSheetSyncStatus {
    PENDING,
    SYNCED
}