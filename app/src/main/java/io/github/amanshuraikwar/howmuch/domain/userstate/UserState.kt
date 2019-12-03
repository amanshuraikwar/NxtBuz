package io.github.amanshuraikwar.howmuch.domain.userstate

import io.github.amanshuraikwar.howmuch.domain.model.User

sealed class UserState {
    object NotSignedIn : UserState()
    data class SignedIn(val user: User) : UserState()
    data class SpreadSheetCreated(val user: User, val spreadSheetId: String): UserState()
}