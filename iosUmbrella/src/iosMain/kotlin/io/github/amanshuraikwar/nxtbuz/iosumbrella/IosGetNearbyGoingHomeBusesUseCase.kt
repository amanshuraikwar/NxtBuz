package io.github.amanshuraikwar.nxtbuz.iosumbrella

import io.github.amanshuraikwar.nxtbuz.repository.BusRouteRepository
import io.github.amanshuraikwar.nxtbuz.repository.BusStopRepository
import io.github.amanshuraikwar.nxtbuz.commonkmm.goinghome.GoingHomeBusResult
import io.github.amanshuraikwar.nxtbuz.iosumbrella.model.IosResult
import io.github.amanshuraikwar.nxtbuz.repository.UserRepository

class IosGetNearbyGoingHomeBusesUseCase(
    userRepository: UserRepository,
    busStopRepository: BusStopRepository,
    busRouteRepository: BusRouteRepository,
) : GetNearbyGoingHomeBusesUseCase(
    userRepository = userRepository,
    busStopRepository = busStopRepository,
    busRouteRepository = busRouteRepository
) {
    operator fun invoke(
        lat: Double,
        lng: Double,
        callback: (IosResult<GoingHomeBusResult>) -> Unit
    ) {
        callback fromFlow {
            invoke(lat, lng)
        }
    }
}

