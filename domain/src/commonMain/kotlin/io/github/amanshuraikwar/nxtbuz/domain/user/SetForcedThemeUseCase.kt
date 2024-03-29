package io.github.amanshuraikwar.nxtbuz.domain.user

import io.github.amanshuraikwar.nxtbuz.commonkmm.NxtBuzTheme
import io.github.amanshuraikwar.nxtbuz.repository.UserRepository

class SetForcedThemeUseCase constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(theme: NxtBuzTheme) {
        return userRepository.setForcedTheme(theme)
    }
}