package io.github.amanshuraikwar.howmuch.domain.transaction

import io.github.amanshuraikwar.howmuch.data.transaction.TransactionRepository
import io.github.amanshuraikwar.howmuch.data.user.UserRepository
import java.lang.Exception
import java.lang.IllegalStateException
import javax.inject.Inject

class SetupNewSpreadSheetUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val transactionRepository: TransactionRepository
) {
    suspend operator fun invoke() {

        val user = userRepository.getSignedInUser() ?: throw InvalidUserStateException.NotSignedIn

        val googleAccountCredential =
            userRepository.getGoogleAccountCredential()
                ?: throw InvalidUserStateException.NotSignedIn

        val spreadSheetId = userRepository.getSpreadSheetId(user)

        if (spreadSheetId.isNullOrEmpty()) {
            val newSpreadSheetId =
                transactionRepository.initialiseNewSpreadSheet(googleAccountCredential)
            userRepository.setSpreadSheetId(user, newSpreadSheetId)
        } else {
            throw InvalidUserStateException.SpreadSheetExists
        }
    }
}

const val USER_NOT_SIGNED_IN = "User not signed in"
const val USER_SPREAD_SHEET_EXISTS = "User already has a spreadsheet"
const val USER_SPREAD_SHEET_NOT_EXISTS = "User already does not have a spreadsheet"

sealed class InvalidUserStateException(msg: String): IllegalStateException(msg) {
    object NotSignedIn : InvalidUserStateException(USER_NOT_SIGNED_IN)
    object SpreadSheetExists : InvalidUserStateException(USER_SPREAD_SHEET_EXISTS)
    object SpreadSheetNotExists : InvalidUserStateException(USER_SPREAD_SHEET_NOT_EXISTS)
}