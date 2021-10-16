package io.github.amanshuraikwar.nxtbuz.domain.user

import io.github.amanshuraikwar.nxtbuz.userdata.UserRepository
import javax.inject.Inject

class SetUseSystemThemeUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(useSystemTheme: Boolean) {
        userRepository.setUseSystemTheme(useSystemTheme)
    }
}