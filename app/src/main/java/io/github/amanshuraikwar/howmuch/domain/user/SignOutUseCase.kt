package io.github.amanshuraikwar.howmuch.domain.user

import io.github.amanshuraikwar.howmuch.data.user.UserRepository
import javax.inject.Inject

class SignOutUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke() {
        val user = userRepository.signOut()
    }
}