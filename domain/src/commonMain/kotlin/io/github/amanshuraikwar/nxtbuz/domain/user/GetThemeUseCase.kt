package io.github.amanshuraikwar.nxtbuz.domain.user

import io.github.amanshuraikwar.nxtbuz.commonkmm.NxtBuzTheme
import io.github.amanshuraikwar.nxtbuz.repository.UserRepository

class GetThemeUseCase constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(): NxtBuzTheme {
        return userRepository.getTheme()
    }
}