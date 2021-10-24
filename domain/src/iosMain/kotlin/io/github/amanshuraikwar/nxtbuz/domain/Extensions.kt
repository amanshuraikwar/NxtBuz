package io.github.amanshuraikwar.nxtbuz.domain

import io.github.amanshuraikwar.nxtbuz.commonkmm.exception.IllegalDbStateException
import io.github.amanshuraikwar.nxtbuz.domain.model.IosResult
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
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