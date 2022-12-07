package io.github.amanshuraikwar.nxtbuz.domain.user

import io.github.amanshuraikwar.nxtbuz.commonkmm.NxtBuzCountry
import io.github.amanshuraikwar.nxtbuz.repository.UserRepository

open class GetCountryUseCase constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): NxtBuzCountry {
        return userRepository.getCountry()
    }
}

open class SetCountryUseCase constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(country: NxtBuzCountry) {
        return userRepository.setCountry(country = country)
    }
}