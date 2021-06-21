package io.github.amanshuraikwar.nxtbuz.domain.search

import io.github.amanshuraikwar.nxtbuz.data.search.SearchRepository
import io.github.amanshuraikwar.nxtbuz.common.model.search.SearchResult
import javax.inject.Inject

class SearchUseCase @Inject constructor(
    private val searchRepository: SearchRepository,
) {

    suspend operator fun invoke(query: String, limit: Int): SearchResult {
        return searchRepository.search(query, limit)
    }
}