package io.github.amanshuraikwar.nxtbuz.iosumbrella

import co.touchlab.stately.freeze
import io.github.amanshuraikwar.nxtbuz.commonkmm.exception.IllegalDbStateException
import io.github.amanshuraikwar.nxtbuz.iosumbrella.model.IosResult
import io.ktor.utils.io.errors.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

inline fun <T> returnIosResult(
    crossinline resultCallback: (IosResult<T>) -> Unit,
    crossinline predicate: suspend () -> T
) {
    IosDataCoroutineScopeProvider.coroutineScope.launch(
        CoroutineExceptionHandler { _, th ->
            println(th)
            resultCallback(
                IosResult.Error(
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
                )
            )
        }
    ) {
        resultCallback(
            IosResult.Success(
                predicate().freeze()
            )
        )
    }
}