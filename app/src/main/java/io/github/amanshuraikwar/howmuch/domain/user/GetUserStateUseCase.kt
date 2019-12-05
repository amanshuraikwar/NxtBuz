package io.github.amanshuraikwar.howmuch.domain.user

import io.github.amanshuraikwar.howmuch.data.user.UserRepository
import io.github.amanshuraikwar.howmuch.domain.transaction.InvalidUserStateException
import javax.inject.Inject

class GetUserStateUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): UserState {

        val user = userRepository.getSignedInUser() ?: return UserState.NotSignedIn

        val googleAccountCredential =
            userRepository.getGoogleAccountCredential()
                ?: return UserState.NotSignedIn

        val spreadSheetId =
            userRepository.getSpreadSheetId(user)
                ?: return UserState.SignedIn(user, googleAccountCredential)

        return UserState.SpreadSheetCreated(user, googleAccountCredential, spreadSheetId)
    }
}