package io.github.amanshuraikwar.nxtbuz.repository

import io.github.amanshuraikwar.nxtbuz.commonkmm.SearchResult

interface SearchRepository {
    suspend fun search(query: String, limit: Int): SearchResult
}