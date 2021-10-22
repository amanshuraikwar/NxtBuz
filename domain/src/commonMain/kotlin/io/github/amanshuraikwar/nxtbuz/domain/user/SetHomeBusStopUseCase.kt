package io.github.amanshuraikwar.nxtbuz.domain.user

import io.github.amanshuraikwar.nxtbuz.repository.UserRepository

open class SetHomeBusStopUseCase(
    private val repo: UserRepository
) {
    suspend operator fun invoke(
        busStopCode: String,
    ) {
        return repo.setHomeBusStopCode(busStopCode = busStopCode)
    }
}