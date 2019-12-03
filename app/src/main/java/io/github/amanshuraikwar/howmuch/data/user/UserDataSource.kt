package io.github.amanshuraikwar.howmuch.data.user

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import io.github.amanshuraikwar.howmuch.data.user.model.User
import javax.inject.Inject

interface UserDataSource {
    fun getUser(): User?
}

class GoogleAuthUserDataSource @Inject constructor(
    private val context: Context
) : UserDataSource {

    override fun getUser(): User? {
        return GoogleSignIn.getLastSignedInAccount(context)?.user()
    }

    private fun GoogleSignInAccount.user(): User? {
        return User(
            id ?: return null,
            displayName ?: return null,
            email ?: return null,
            photoUrl?.toString()
        )
    }
}