package io.github.amanshuraikwar.howmuch.domain.transaction

import io.github.amanshuraikwar.howmuch.data.model.Transaction
import io.github.amanshuraikwar.howmuch.data.transaction.TransactionRepository
import io.github.amanshuraikwar.howmuch.data.user.UserRepository
import io.github.amanshuraikwar.howmuch.domain.user.UserState
import javax.inject.Inject

class GetOverviewTransactionsUseCase @Inject constructor(
    userRepository: UserRepository,
    private val transactionRepository: TransactionRepository
) : RequireUserSpreadSheetUseCase<List<Transaction>>(userRepository) {

    override suspend fun execute(userState: UserState.SpreadSheetCreated): List<Transaction> {
        return transactionRepository.getTransactions(
            userState.spreadSheetId, userState.googleAccountCredential
        )
    }
}