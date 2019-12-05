package io.github.amanshuraikwar.howmuch.domain.transaction

import io.github.amanshuraikwar.howmuch.data.user.UserRepository
import io.github.amanshuraikwar.howmuch.domain.user.UserState
import javax.inject.Inject

abstract class RequireUserSpreadSheetUseCase<T>(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): T {

        val user = userRepository.getSignedInUser() ?: throw InvalidUserStateException.NotSignedIn

        val googleAccountCredential =
            userRepository.getGoogleAccountCredential()
                ?: throw InvalidUserStateException.NotSignedIn

        val spreadSheetId =
            userRepository.getSpreadSheetId(user)
                ?: throw InvalidUserStateException.SpreadSheetNotExists

        return execute(
            UserState.SpreadSheetCreated(user, googleAccountCredential, spreadSheetId)
        )
    }

    abstract suspend fun execute(userState: UserState.SpreadSheetCreated): T
}