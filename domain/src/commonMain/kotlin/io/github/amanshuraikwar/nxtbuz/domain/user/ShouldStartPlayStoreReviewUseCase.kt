package io.github.amanshuraikwar.nxtbuz.domain.user

import io.github.amanshuraikwar.nxtbuz.repository.UserRepository

class ShouldStartPlayStoreReviewUseCase constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): Boolean {
        return userRepository.shouldStartPlayStoreReview()
    }
}