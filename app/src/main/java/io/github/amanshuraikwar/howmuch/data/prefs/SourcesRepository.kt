package io.github.amanshuraikwar.howmuch.data.prefs

import android.content.Context
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import io.github.amanshuraikwar.howmuch.data.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.howmuch.domain.model.User
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Manage saving and retrieving data sources from disk.
 */
@Singleton
class SourcesRepository @Inject constructor(
    private val preferenceStorage: PreferenceStorage,
    private val dispatcherProvider: CoroutinesDispatcherProvider,
    private val context: Context
) {

    suspend fun isOnboardingComplete(): Boolean = withContext(dispatcherProvider.io) {
        return@withContext preferenceStorage.onboardingCompleted
    }

    suspend fun getUser(): User? = withContext(dispatcherProvider.io) {
        return@withContext GoogleSignIn.getLastSignedInAccount(context)?.user()
    }

    suspend fun getSpreadSheetId(user: User): String? =  withContext(dispatcherProvider.io) {
        return@withContext getSpreadSheetIdForEmail(user.email)
    }

    private fun getSpreadSheetIdForEmail(email: String): String? {
        return null
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
