package io.github.amanshuraikwar.nxtbuz.domain

import io.github.amanshuraikwar.nxtbuz.commonkmm.exception.IllegalDbStateException
import io.github.amanshuraikwar.nxtbuz.domain.model.FlowCancellationSignal
import io.github.amanshuraikwar.nxtbuz.domain.model.IosResult
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlin.native.concurrent.freeze

inline infix fun <T, U : Flow<T>> ((IosResult<T>) -> Unit).fromFlow(
    crossinline predicate: suspend () -> U
) {
    IosDataCoroutineScopeProvider.coroutineScope.launch(
        CoroutineExceptionHandler { _, th ->
            println(th)
            this@fromFlow(
                IosResult.Error<T>(
                    // TODO-amanshuraikwar (12 Oct 2021 11:24:30 PM): handle gracefully
                    when (th) {
                        is IllegalDbStateException -> {
                            "IllegalDbStateException"
                        }
//                        is IOException -> {
//                            "IOException"
//                        }
                        else -> {
                            "Something went wrong."
                        }
                    }
                ).freeze()
            )
        }
    ) {
        predicate().collect {
            this@fromFlow(
                IosResult.Success(it).freeze()
            )
        }
    }
}

inline fun <T, U : Flow<T>> ((IosResult<T>) -> Unit).fromFlowCancellable(
    crossinline onStart: (FlowCancellationSignal) -> Unit,
    crossinline predicate: suspend () -> U
) {
    IosDataCoroutineScopeProvider.coroutineScope.launch(
        CoroutineExceptionHandler { _, th ->
            println(th)
            if (th !is CancellationException) {
                this@fromFlowCancellable(
                    IosResult.Error<T>(
                        // TODO-amanshuraikwar (24 Oct 2021 11:56:11 AM): handle io exception
                        when (th) {
                            is IllegalDbStateException -> {
                                "IllegalDbStateException"
                            }
                            else -> {
                                "Something went wrong."
                            }
                        }
                    ).freeze()
                )
            }
        }
    ) {
        val deferred = async(start = CoroutineStart.LAZY) {
            predicate().collect {
                if (isActive) {
                    this@fromFlowCancellable(
                        IosResult.Success(it).freeze()
                    )
                }
            }
        }

        onStart(
            object : FlowCancellationSignal {
                override fun cancel() {
                    deferred.cancel()
                }
            }.freeze()
        )

        if (!deferred.isCancelled) {
            deferred.await()
        }
    }
}

inline infix fun <T> ((IosResult<T>) -> Unit).from(
    crossinline predicate: suspend () -> T
) {
    IosDataCoroutineScopeProvider.coroutineScope.launch(
        CoroutineExceptionHandler { _, th ->
            println(th)
            this@from(
                IosResult.Error<T>(
                    // TODO-amanshuraikwar (12 Oct 2021 11:24:30 PM): handle gracefully
                    when (th) {
                        is IllegalDbStateException -> {
                            "IllegalDbStateException"
                        }
//                        is IOException -> {
//                            "IOException"
//                        }
                        else -> {
                            "Something went wrong."
                        }
                    }
                ).freeze()
            )
        }
    ) {
        this@from(
            IosResult.Success(
                predicate()
            ).freeze()
        )
    }
}