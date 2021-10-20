package io.github.amanshuraikwar.nxtbuz.iosumbrella

import io.github.amanshuraikwar.nxtbuz.commonkmm.user.UserState
import io.github.amanshuraikwar.nxtbuz.repository.UserRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class GetUserStateUseCase constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(callback: (UserState) -> Unit) {
        IosDataCoroutineScopeProvider.coroutineScope.launch {
            callback(userRepository.getUserState())
        }
    }
}