package io.github.amanshuraikwar.nxtbuz.iosumbrella

import io.github.amanshuraikwar.nxtbuz.repository.BusRouteRepository
import io.github.amanshuraikwar.nxtbuz.repository.BusStopRepository
import io.github.amanshuraikwar.nxtbuz.commonkmm.user.SetupState
import io.github.amanshuraikwar.nxtbuz.repository.UserRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.native.concurrent.freeze

class DoSetupUseCase constructor(
    private val userRepository: UserRepository,
    private val busStopRepository: BusStopRepository,
    private val busRouteRepository: BusRouteRepository
) {
    operator fun invoke(): Flow<SetupState> = flow outerFlow@{
        userRepository.markSetupIncomplete()

        busStopRepository.setup()
            .ensureValidProgress()
            .collect { value ->
                this@outerFlow.emit(
                    SetupState.InProgress(
                        0.2 * value.coerceAtMost(1.0)
                    )
                )
            }

        busRouteRepository.setup()
            .ensureValidProgress()
            .collect { value ->
                this@outerFlow.emit(
                    SetupState.InProgress(
                        0.2 + (0.8 * value).coerceAtMost(1.0)
                    )
                )
            }

        userRepository.markSetupComplete()

        emit(SetupState.Complete)
    }

    private fun Flow<Double>.ensureValidProgress(): Flow<Double> = map { value ->
        when {
            value < 0 -> {
                0.0
            }
            value > 1 -> {
                1.0
            }
            else -> {
                value
            }
        }
    }

    operator fun invoke(callback: (SetupState) -> Unit) {
        IosDataCoroutineScopeProvider.coroutineScope.launch {
            invoke().collect {
                callback(it.freeze())
            }
        }
    }
}