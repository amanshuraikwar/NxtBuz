package io.github.amanshuraikwar.nxtbuz.domain

import io.github.amanshuraikwar.nxtbuz.commonkmm.goinghome.GoingHomeBusResult
import io.github.amanshuraikwar.nxtbuz.domain.busstop.GetNearbyGoingHomeBusesUseCase
import io.github.amanshuraikwar.nxtbuz.domain.model.FlowCancellationSignal
import io.github.amanshuraikwar.nxtbuz.domain.model.IosResult
import io.github.amanshuraikwar.nxtbuz.repository.BusArrivalRepository
import io.github.amanshuraikwar.nxtbuz.repository.BusRouteRepository
import io.github.amanshuraikwar.nxtbuz.repository.BusStopRepository
import io.github.amanshuraikwar.nxtbuz.repository.UserRepository

class IosGetNearbyGoingHomeBusesUseCase(
    userRepository: UserRepository,
    busStopRepository: BusStopRepository,
    busRouteRepository: BusRouteRepository,
    busArrivalRepository: BusArrivalRepository
) : GetNearbyGoingHomeBusesUseCase(
    userRepository = userRepository,
    busStopRepository = busStopRepository,
    busRouteRepository = busRouteRepository,
    busArrivalRepository = busArrivalRepository
) {
    operator fun invoke(
        lat: Double,
        lng: Double,
        onStart: (FlowCancellationSignal) -> Unit,
        callback: (IosResult<GoingHomeBusResult>) -> Unit
    ) {
        callback.fromFlowCancellable(
            onStart,
            {
                invoke(lat, lng)
            }
        )
    }
}

