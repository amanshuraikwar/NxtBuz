package io.github.amanshuraikwar.nxtbuz.domain

import io.github.amanshuraikwar.nxtbuz.commonkmm.SearchResult
import io.github.amanshuraikwar.nxtbuz.domain.model.IosResult
import io.github.amanshuraikwar.nxtbuz.domain.search.SearchUseCase
import io.github.amanshuraikwar.nxtbuz.repository.SearchRepository

class IosSearchUseCase constructor(
    searchRepository: SearchRepository,
) : SearchUseCase(
    searchRepository = searchRepository
) {
    operator fun invoke(
        query: String,
        limit: Int,
        callback: (IosResult<SearchResult>) -> Unit
    ) {
        callback from {
            invoke(
                query = query,
                limit = limit
            )
        }
    }
}