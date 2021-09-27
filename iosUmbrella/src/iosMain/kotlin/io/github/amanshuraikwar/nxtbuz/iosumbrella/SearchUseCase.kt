package io.github.amanshuraikwar.nxtbuz.iosumbrella

import co.touchlab.stately.freeze
import io.github.amanshuraikwar.nxtbuz.commonkmm.SearchResult
import io.github.amanshuraikwar.nxtbuz.searchdata.SearchRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class SearchUseCase constructor(
    private val searchRepository: SearchRepository,
) {
    suspend operator fun invoke(query: String, limit: Int): SearchResult {
        return searchRepository.search(query, limit)
    }

    operator fun invoke(query: String, limit: Int, callback: (SearchResult) -> Unit) {
        IosDataCoroutineScopeProvider.coroutineScope.launch(
            CoroutineExceptionHandler { _, th ->
                // TODO-amanshuraikwar (24 Sep 2021 11:49:55 AM): gracefully handle error
            }
        ) {
            callback(searchRepository.search(query, limit).freeze())
        }
    }
}