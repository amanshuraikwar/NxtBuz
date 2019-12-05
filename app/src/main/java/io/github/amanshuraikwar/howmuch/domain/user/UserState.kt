package io.github.amanshuraikwar.howmuch.domain.user

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import io.github.amanshuraikwar.howmuch.data.model.User

sealed class UserState {

    object NotSignedIn : UserState()

    data class SignedIn(
        val user: User,
        val googleAccountCredential: GoogleAccountCredential
    ) : UserState()

    data class SpreadSheetCreated(
        val user: User,
        val googleAccountCredential: GoogleAccountCredential,
        val spreadSheetId: String
    ): UserState()
}