package io.github.amanshuraikwar.nxtbuz.domain.user

import io.github.amanshuraikwar.nxtbuz.repository.UserRepository

class SetUseSystemThemeUseCase constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(useSystemTheme: Boolean) {
        userRepository.setUseSystemTheme(useSystemTheme)
    }
}