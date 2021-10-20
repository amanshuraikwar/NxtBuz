package io.github.amanshuraikwar.nxtbuz.iosumbrella

import io.github.amanshuraikwar.nxtbuz.commonkmm.starred.ToggleStarUpdate
import io.github.amanshuraikwar.nxtbuz.repository.StarredBusArrivalRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ToggleStarUpdateUseCase constructor(
    private val repo: StarredBusArrivalRepository
) {
    operator fun invoke(): SharedFlow<ToggleStarUpdate> {
        return repo.toggleStarUpdate
    }

    operator fun invoke(
        starUpdateCallback: (ToggleStarUpdate) -> Unit
    ) {
        IosDataCoroutineScopeProvider.coroutineScope.launch(
            CoroutineExceptionHandler { _, _ ->
                // do nothing
            }
        ) {
            invoke().collect {
                starUpdateCallback(it)
            }
        }
    }
}