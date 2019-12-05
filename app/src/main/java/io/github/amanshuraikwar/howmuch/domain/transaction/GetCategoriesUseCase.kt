package io.github.amanshuraikwar.howmuch.domain.transaction

import io.github.amanshuraikwar.howmuch.data.model.Category
import io.github.amanshuraikwar.howmuch.data.transaction.TransactionRepository
import io.github.amanshuraikwar.howmuch.data.user.UserRepository
import javax.inject.Inject

class GetCategoriesUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val transactionRepository: TransactionRepository
) {
    suspend operator fun invoke(): List<Category> {

        val user = userRepository.getSignedInUser() ?: throw InvalidUserStateException.NotSignedIn

        val googleAccountCredential =
            userRepository.getGoogleAccountCredential()
                ?: throw InvalidUserStateException.NotSignedIn

        val spreadSheetId =
            userRepository.getSpreadSheetId(user)
                ?: throw InvalidUserStateException.SpreadSheetNotExists

        return transactionRepository.getCategories(spreadSheetId, googleAccountCredential)
    }
}