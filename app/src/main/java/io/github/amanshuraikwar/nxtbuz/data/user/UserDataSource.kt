package io.github.amanshuraikwar.nxtbuz.data.user

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.util.ExponentialBackOff
import com.google.api.services.sheets.v4.SheetsScopes
import io.github.amanshuraikwar.nxtbuz.data.model.User
import javax.inject.Inject

interface UserDataSource {
    fun getUser(): User?
    fun getGoogleAccountCredential(): GoogleAccountCredential?
}

class GoogleAuthUserDataSource @Inject constructor(
    private val context: Context
) : UserDataSource {

    override fun getUser(): User? {
        return GoogleSignIn.getLastSignedInAccount(context)?.user()
    }

    override fun getGoogleAccountCredential(): GoogleAccountCredential? {
        return GoogleSignIn.getLastSignedInAccount(context)?.credential(context)
    }

    private fun GoogleSignInAccount.user(): User? {
        return User(
            id ?: return null,
            displayName ?: return null,
            email ?: return null,
            photoUrl?.toString()
        )
    }

    private fun GoogleSignInAccount.credential(context: Context): GoogleAccountCredential {
        return GoogleAccountCredential
            .usingOAuth2(
                context,
                listOf(SheetsScopes.SPREADSHEETS)
            )
            .setBackOff(ExponentialBackOff())
            .setSelectedAccount(this.account)
    }

}