package io.github.amanshuraikwar.nxtbuz.domain.user

import io.github.amanshuraikwar.nxtbuz.common.model.UserState
import io.github.amanshuraikwar.nxtbuz.data.user.UserRepository
import javax.inject.Inject

class GetUserStateUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): UserState {
        return userRepository.getUserState()
    }
}