package io.github.amanshuraikwar.nxtbuz.domain.user

import io.github.amanshuraikwar.nxtbuz.repository.UserRepository

class UpdatePlayStoreReviewTimeUseCase constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke() {
        userRepository.updatePlayStoreReviewTime()
    }
}