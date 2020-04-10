package io.github.amanshuraikwar.nxtbuz.domain.setup

import io.github.amanshuraikwar.nxtbuz.data.user.UserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SetupUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    @ExperimentalCoroutinesApi
    operator fun invoke(): Flow<UserRepository.SetupState> {
        return userRepository.setupFlow()
    }
}