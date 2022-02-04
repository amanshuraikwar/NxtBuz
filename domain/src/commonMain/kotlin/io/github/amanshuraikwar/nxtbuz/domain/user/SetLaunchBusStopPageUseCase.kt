package io.github.amanshuraikwar.nxtbuz.domain.user

import io.github.amanshuraikwar.nxtbuz.commonkmm.user.LaunchBusStopsPage
import io.github.amanshuraikwar.nxtbuz.repository.UserRepository

open class SetLaunchBusStopPageUseCase constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(launchBusStopsPage: LaunchBusStopsPage) {
        userRepository.setLaunchBusStopsPage(launchBusStopsPage)
    }
}