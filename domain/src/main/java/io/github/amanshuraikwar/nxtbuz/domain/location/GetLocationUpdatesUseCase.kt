package io.github.amanshuraikwar.nxtbuz.domain.location

import io.github.amanshuraikwar.nxtbuz.common.model.location.LocationOutput
import io.github.amanshuraikwar.nxtbuz.data.location.LocationEmitter
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject

class GetLocationUpdatesUseCase @Inject constructor(
    private val locationEmitter: LocationEmitter,
) {
    suspend operator fun invoke(): SharedFlow<LocationOutput> {
        return locationEmitter.getLocation()
    }
}


