package io.github.amanshuraikwar.howmuch.data.user

import io.github.amanshuraikwar.howmuch.data.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.howmuch.data.user.model.User
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val googleAuthUserDataSource: GoogleAuthUserDataSource,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) {

    private var user: User? = null

    init {
        user = googleAuthUserDataSource.getUser()
    }

    suspend fun getSignedInUser(): User? = withContext(dispatcherProvider.io) {
        return@withContext user ?: run { user = googleAuthUserDataSource.getUser(); user }
    }

    suspend fun getSpreadSheetId(user: User): String? =  withContext(dispatcherProvider.io) {
        return@withContext getSpreadSheetIdForEmail(user.email)
    }

    private fun getSpreadSheetIdForEmail(email: String): String? {
        return null
    }

    suspend fun signOut() = withContext(dispatcherProvider.io) {
        user = null
    }
}