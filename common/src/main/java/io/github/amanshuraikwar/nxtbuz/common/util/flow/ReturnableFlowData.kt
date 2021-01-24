package io.github.amanshuraikwar.nxtbuz.common.util.flow

import kotlinx.coroutines.CancellableContinuation

data class ReturnableFlowData<T, U>(
    val param: T,
    val cont: CancellableContinuation<U>,
)