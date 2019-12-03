package io.github.amanshuraikwar.howmuch.domain.user

import io.github.amanshuraikwar.howmuch.data.user.UserRepository
import javax.inject.Inject

class GetUserStateUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): UserState {
        val user = userRepository.getSignedInUser() ?: return UserState.NotSignedIn
        val spreadSheetId =
            userRepository.getSpreadSheetId(user) ?: return UserState.SignedIn(user)
        return UserState.SpreadSheetCreated(user, spreadSheetId)
    }
}