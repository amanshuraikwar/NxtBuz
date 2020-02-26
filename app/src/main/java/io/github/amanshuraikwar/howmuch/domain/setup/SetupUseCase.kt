package io.github.amanshuraikwar.howmuch.domain.setup

import io.github.amanshuraikwar.howmuch.data.user.UserRepository
import javax.inject.Inject

class SetupUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke() {
        userRepository.setup()
    }
}