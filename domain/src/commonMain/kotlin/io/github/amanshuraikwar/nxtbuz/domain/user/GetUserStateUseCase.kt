package io.github.amanshuraikwar.nxtbuz.domain.user

import io.github.amanshuraikwar.nxtbuz.commonkmm.user.UserState
import io.github.amanshuraikwar.nxtbuz.repository.UserRepository

open class GetUserStateUseCase constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): UserState {
        return userRepository.getUserState()
    }
}