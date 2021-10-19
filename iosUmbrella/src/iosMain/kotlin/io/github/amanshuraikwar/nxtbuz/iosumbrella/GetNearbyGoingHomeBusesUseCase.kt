package io.github.amanshuraikwar.nxtbuz.iosumbrella

import io.github.amanshuraikwar.nxtbuz.busroutedata.BusRouteRepository
import io.github.amanshuraikwar.nxtbuz.busstopdata.BusStopRepository
import io.github.amanshuraikwar.nxtbuz.commonkmm.goinghome.GoingHomeBusResult
import io.github.amanshuraikwar.nxtbuz.iosumbrella.model.IosResult
import io.github.amanshuraikwar.nxtbuz.userdata.UserRepository

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
        callback.returnIosResultFromFlow {
            invoke(lat, lng)
        }
    }
}

