package io.github.amanshuraikwar.nxtbuz.domain.location

import android.util.Log
import io.github.amanshuraikwar.nxtbuz.common.model.map.MapEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

private const val TAG = "PushMapEventUseCase"

class PushMapEventUseCase @Inject constructor(
    @Named("mapScope") private val coroutineScope: CoroutineScope,
    @Named("mapEventFlow") private val mapEventFlow: MutableSharedFlow<MapEvent>
) {

//    suspend operator fun invoke(mapEvent: MapEvent): MapResult {
//        return mapEventFlow.emit(mapEvent)
//    }

    operator fun invoke(mapEvent: MapEvent) {
        coroutineScope.launch {
            Log.d(TAG, "invoke: $mapEvent")
            mapEventFlow.emit(mapEvent)
        }
    }
}