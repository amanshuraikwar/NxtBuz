package io.github.amanshuraikwar.nxtbuz.iosumbrella

import co.touchlab.stately.freeze
import io.github.amanshuraikwar.nxtbuz.commonkmm.SearchResult
import io.github.amanshuraikwar.nxtbuz.iosumbrella.model.IosSearchOutput
import io.github.amanshuraikwar.nxtbuz.searchdata.SearchRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import platform.Foundation.NSLog

class SearchUseCase constructor(
    private val searchRepository: SearchRepository,
) {
    suspend operator fun invoke(query: String, limit: Int): SearchResult {
        return searchRepository.search(query, limit)
    }

    operator fun invoke(query: String, limit: Int, callback: (IosSearchOutput) -> Unit) {
        IosDataCoroutineScopeProvider.coroutineScope.launch(
            CoroutineExceptionHandler { _, th ->
                th.printStackTrace()
                println(th.message + th.cause)
                callback(
                    IosSearchOutput.Error(message = th.stackTraceToString() ?: "Something went wrong!").freeze()
                )
            }
        ) {
            callback(
                IosSearchOutput.Success(searchRepository.search(query, limit)).freeze()
            )
        }
    }
}