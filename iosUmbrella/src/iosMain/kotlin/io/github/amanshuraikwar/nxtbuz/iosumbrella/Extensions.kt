package io.github.amanshuraikwar.nxtbuz.iosumbrella

import co.touchlab.stately.freeze
import io.github.amanshuraikwar.nxtbuz.commonkmm.exception.IllegalDbStateException
import io.github.amanshuraikwar.nxtbuz.iosumbrella.model.IosResult
import io.ktor.utils.io.errors.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

inline fun <T> ((IosResult<T>) -> Unit).returnIosResult(
    crossinline predicate: suspend () -> T
) {
    IosDataCoroutineScopeProvider.coroutineScope.launch(
        CoroutineExceptionHandler { _, th ->
            println(th)
            this@returnIosResult(
                IosResult.Error<T>(
                    // TODO-amanshuraikwar (12 Oct 2021 11:24:30 PM): handle gracefully
                    when (th) {
                        is IllegalDbStateException -> {
                            "IllegalDbStateException"
                        }
                        is IOException -> {
                            "IOException"
                        }
                        else -> {
                            "Something went wrong."
                        }
                    }
                ).freeze()
            )
        }
    ) {
        this@returnIosResult(
            IosResult.Success(
                predicate()
            ).freeze()
        )
    }
}

inline fun <T> ((IosResult<T>) -> Unit).returnIosResultFromFlow(
    crossinline predicate: suspend () -> Flow<T>
) {
    IosDataCoroutineScopeProvider.coroutineScope.launch(
        CoroutineExceptionHandler { _, th ->
            println(th)
            this@returnIosResultFromFlow(
                IosResult.Error<T>(
                    // TODO-amanshuraikwar (12 Oct 2021 11:24:30 PM): handle gracefully
                    when (th) {
                        is IllegalDbStateException -> {
                            "IllegalDbStateException"
                        }
                        is IOException -> {
                            "IOException"
                        }
                        else -> {
                            "Something went wrong."
                        }
                    }
                ).freeze()
            )
        }
    ) {
        predicate().collect {
            this@returnIosResultFromFlow(
                IosResult.Success(it).freeze()
            )
        }
    }
}