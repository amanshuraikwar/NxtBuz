package io.github.amanshuraikwar.nxtbuz.domain.search

import io.github.amanshuraikwar.nxtbuz.commonkmm.SearchResult
import io.github.amanshuraikwar.nxtbuz.repository.SearchRepository

open class SearchUseCase constructor(
    private val searchRepository: SearchRepository,
) {
    suspend operator fun invoke(query: String, limit: Int): SearchResult {
        return searchRepository.search(query, limit)
    }
}