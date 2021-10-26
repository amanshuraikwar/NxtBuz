package io.github.amanshuraikwar.nxtbuz.domain.user

import io.github.amanshuraikwar.nxtbuz.repository.UserRepository

class GetUseSystemThemeUseCase constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): Boolean {
        return userRepository.getUseSystemTheme()
    }
}