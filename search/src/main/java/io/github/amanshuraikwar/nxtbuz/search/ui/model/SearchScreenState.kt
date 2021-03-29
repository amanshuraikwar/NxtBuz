package io.github.amanshuraikwar.nxtbuz.search.ui.model

sealed class SearchScreenState {
    data class Success(
        val searchResultList: List<SearchResult>
    ) : SearchScreenState()

    data class Failed(
        val errorMessage: String
    ) : SearchScreenState()
}