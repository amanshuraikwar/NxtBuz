package io.github.amanshuraikwar.nsapi.provider

import io.github.amanshuraikwar.nxtbuz.repository.TrainStopRepository

expect class NsApiFactory : TrainStopRepository.Factory {
    override fun create(): TrainStopRepository
}