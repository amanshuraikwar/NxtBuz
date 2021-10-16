package io.github.amanshuraikwar.nxtbuz.iosumbrella.model

import io.github.amanshuraikwar.nxtbuz.commonkmm.SearchResult

sealed class IosSearchOutput {
    data class Error(val message: String) : IosSearchOutput()
    data class Success(val searchResult: SearchResult) : IosSearchOutput()
}