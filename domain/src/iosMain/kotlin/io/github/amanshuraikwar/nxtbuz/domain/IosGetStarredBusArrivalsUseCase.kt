package io.github.amanshuraikwar.nxtbuz.domain

import io.github.amanshuraikwar.nxtbuz.commonkmm.starred.StarredBusArrival
import io.github.amanshuraikwar.nxtbuz.domain.arrivals.GetBusArrivalsUseCase
import io.github.amanshuraikwar.nxtbuz.domain.busstop.GetBusStopUseCase
import io.github.amanshuraikwar.nxtbuz.domain.model.IosResult
import io.github.amanshuraikwar.nxtbuz.domain.starred.GetStarredBusArrivalsUseCase
import io.github.amanshuraikwar.nxtbuz.domain.starred.GetStarredBusServicesUseCase
import io.github.amanshuraikwar.nxtbuz.domain.starred.ShowErrorStarredBusArrivalsUseCase

class IosGetStarredBusArrivalsUseCase(
    getStarredBusServicesUseCase: GetStarredBusServicesUseCase,
    getBusArrivalsUseCase: GetBusArrivalsUseCase,
    getBusStopUseCase: GetBusStopUseCase,
    showErrorStarredBusArrivalsUseCase: ShowErrorStarredBusArrivalsUseCase,
) : GetStarredBusArrivalsUseCase(
    getStarredBusServicesUseCase = getStarredBusServicesUseCase,
    getBusArrivalsUseCase = getBusArrivalsUseCase,
    getBusStopUseCase = getBusStopUseCase,
    showErrorStarredBusArrivalsUseCase = showErrorStarredBusArrivalsUseCase,
) {
    operator fun invoke(
        callback: (IosResult<List<StarredBusArrival>>) -> Unit
    ) {
        callback from {
            invoke()
        }
    }
}