package io.github.amanshuraikwar.nxtbuz.domain.user

import io.github.amanshuraikwar.nxtbuz.commonkmm.NxtBuzTheme
import io.github.amanshuraikwar.nxtbuz.repository.UserRepository

class GetForcedThemeUseCase constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): NxtBuzTheme {
        return userRepository.getForcedTheme()
    }
}