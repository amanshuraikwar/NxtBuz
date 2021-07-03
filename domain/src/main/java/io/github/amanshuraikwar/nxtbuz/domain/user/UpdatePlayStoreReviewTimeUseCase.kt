package io.github.amanshuraikwar.nxtbuz.domain.user

import io.github.amanshuraikwar.nxtbuz.data.user.UserRepository
import javax.inject.Inject

class UpdatePlayStoreReviewTimeUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke() {
        userRepository.updatePlayStoreReviewTime()
    }
}