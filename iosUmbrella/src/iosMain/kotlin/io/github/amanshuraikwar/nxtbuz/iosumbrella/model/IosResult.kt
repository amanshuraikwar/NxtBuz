package io.github.amanshuraikwar.nxtbuz.iosumbrella.model

sealed class IosResult<T> {
    data class Error<T>(val errorMessage: String): IosResult<T>()
    data class Success<T>(val data: T): IosResult<T>()
}