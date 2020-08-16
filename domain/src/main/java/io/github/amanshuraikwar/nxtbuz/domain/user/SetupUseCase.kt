package io.github.amanshuraikwar.nxtbuz.domain.user

import io.github.amanshuraikwar.nxtbuz.common.model.SetupState
import io.github.amanshuraikwar.nxtbuz.data.busroute.BusRouteRepository
import io.github.amanshuraikwar.nxtbuz.data.busstop.BusStopRepository
import io.github.amanshuraikwar.nxtbuz.data.user.UserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class SetupUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val busStopRepository: BusStopRepository,
    private val busRouteRepository: BusRouteRepository
) {

    @InternalCoroutinesApi
    @ExperimentalCoroutinesApi
    operator fun invoke(): Flow<SetupState> = flow outerFlow@{
        coroutineScope { userRepository.markSetupIncomplete() }
        busStopRepository.setup().collect(
            object : FlowCollector<Double> {
                override suspend fun emit(value: Double) {
                    this@outerFlow.emit(
                        SetupState.InProgress(
                            0.2 * value.coerceAtMost(1.0)
                        )
                    )
                }
            }
        )
        busRouteRepository.setup().collect(
            object : FlowCollector<Double> {
                override suspend fun emit(value: Double) {
                    this@outerFlow.emit(
                        SetupState.InProgress(
                            0.2 + (0.8 * value).coerceAtMost(1.0)
                        )
                    )
                }
            }
        )
        coroutineScope { userRepository.markSetupComplete() }
        emit(SetupState.Complete)
    }
}