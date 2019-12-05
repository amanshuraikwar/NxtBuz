package io.github.amanshuraikwar.howmuch.data.user

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import io.github.amanshuraikwar.howmuch.data.di.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.howmuch.data.model.User
import io.github.amanshuraikwar.howmuch.data.room.RoomDataSource
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val googleAuthUserDataSource: GoogleAuthUserDataSource,
    private val roomDataSource: RoomDataSource,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) {

    private var user: User? = null
    private var googleAccountCredential: GoogleAccountCredential? = null

    init {
        user = googleAuthUserDataSource.getUser()
    }

    suspend fun getSignedInUser(): User? = withContext(dispatcherProvider.io) {
        return@withContext user
            ?: run {
                user = googleAuthUserDataSource.getUser()
                user
            }
    }

    suspend fun getSpreadSheetId(user: User): String? =  withContext(dispatcherProvider.io) {
        return@withContext roomDataSource.getSpreadsheetIdForEmail(user.email)
    }

    suspend fun getGoogleAccountCredential(): GoogleAccountCredential?
            = withContext(dispatcherProvider.io) {
                return@withContext googleAccountCredential
                    ?: run {
                        googleAccountCredential =
                            googleAuthUserDataSource.getGoogleAccountCredential()
                        googleAccountCredential
                    }
            }

    suspend fun setSpreadSheetId(user: User, spreadSheetId: String) =
        withContext(dispatcherProvider.io) {
            return@withContext roomDataSource.addSpreadsheetIdForEmail(spreadSheetId, user.email)
        }

    suspend fun signOut() = withContext(dispatcherProvider.io) {
        user = null
        googleAccountCredential = null
    }
}