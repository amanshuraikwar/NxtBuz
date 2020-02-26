package io.github.amanshuraikwar.howmuch.domain.user

import io.github.amanshuraikwar.howmuch.data.user.UserRepository
import io.github.amanshuraikwar.howmuch.data.user.UserState
import javax.inject.Inject

class GetUserStateUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): UserState {
        return userRepository.getUserState()
    }
}