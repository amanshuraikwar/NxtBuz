package io.github.amanshuraikwar.nxtbuz.common.util.flow

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine

class ReturnableFlow<T, U>(
    replay: Int = 0,
) {

    val flow = MutableSharedFlow<ReturnableFlowData<T, U>>(
        replay = replay
    )

    suspend fun emit(value: T): U {
        return coroutineScope {
            suspendCancellableCoroutine { cont ->
                launch {
                    flow.emit(ReturnableFlowData(value, cont))
                }
            }
        }
    }

    suspend inline fun collect(crossinline action: suspend (value: T) -> U) {
        flow.collect { (value, cont) ->
            cont.resumeWith(Result.success(action(value)))
        }
    }
}