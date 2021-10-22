package io.github.amanshuraikwar.nxtbuz.domain

import io.github.amanshuraikwar.nxtbuz.commonkmm.user.UserState
import io.github.amanshuraikwar.nxtbuz.domain.model.IosResult
import io.github.amanshuraikwar.nxtbuz.domain.user.GetUserStateUseCase
import io.github.amanshuraikwar.nxtbuz.repository.UserRepository

class IosGetUserStateUseCase constructor(
    userRepository: UserRepository
) : GetUserStateUseCase(
    userRepository = userRepository
) {
    operator fun invoke(callback: (IosResult<UserState>) -> Unit) {
        callback from {
            invoke()
        }
    }
}