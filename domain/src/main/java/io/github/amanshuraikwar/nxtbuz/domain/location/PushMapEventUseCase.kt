package io.github.amanshuraikwar.nxtbuz.domain.location

import io.github.amanshuraikwar.nxtbuz.common.model.map.MapEvent
import io.github.amanshuraikwar.nxtbuz.common.model.map.MapResult
import io.github.amanshuraikwar.nxtbuz.common.util.flow.ReturnableFlow
import javax.inject.Inject
import javax.inject.Named

class PushMapEventUseCase @Inject constructor(
    @Named("mapEventFlow") private val mapEventFlow: ReturnableFlow<MapEvent, MapResult>
) {

    suspend operator fun invoke(mapEvent: MapEvent): MapResult {
        return mapEventFlow.emit(mapEvent)
    }
}