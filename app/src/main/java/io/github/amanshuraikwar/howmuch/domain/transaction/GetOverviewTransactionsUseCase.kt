package io.github.amanshuraikwar.howmuch.domain.transaction

import io.github.amanshuraikwar.howmuch.data.model.Category
import io.github.amanshuraikwar.howmuch.data.model.Money
import io.github.amanshuraikwar.howmuch.data.model.SpreadSheetCell
import io.github.amanshuraikwar.howmuch.data.model.Transaction
import io.github.amanshuraikwar.howmuch.data.room.transactions.SpreadSheetSyncStatus
import io.github.amanshuraikwar.howmuch.data.transaction.TransactionRepository
import io.github.amanshuraikwar.howmuch.data.user.UserRepository
import io.github.amanshuraikwar.howmuch.domain.user.UserState
import org.threeten.bp.OffsetDateTime
import javax.inject.Inject

class GetOverviewTransactionsUseCase @Inject constructor(
    userRepository: UserRepository,
    private val transactionRepository: TransactionRepository
) : RequireUserSpreadSheetUseCase<List<Transaction>>(userRepository) {

    override suspend fun execute(userState: UserState.SpreadSheetCreated): List<Transaction> {

        return transactionRepository.getTransactionsAfter(
            userState.spreadSheetId,
            userState.googleAccountCredential,
            OffsetDateTime.now().minusDays(7).toInstant().toEpochMilli()
        )
    }
}