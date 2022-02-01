package io.github.amanshuraikwar.nxtbuz.domain.user

import io.github.amanshuraikwar.nxtbuz.commonkmm.user.LaunchBusStopsPage
import io.github.amanshuraikwar.nxtbuz.repository.UserRepository

open class GetLaunchBusStopPageUseCase constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): LaunchBusStopsPage {
        return userRepository.getLaunchBusStopsPage()
    }
}