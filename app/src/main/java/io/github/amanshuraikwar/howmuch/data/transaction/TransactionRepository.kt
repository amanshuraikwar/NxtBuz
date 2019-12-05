package io.github.amanshuraikwar.howmuch.data.transaction

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import io.github.amanshuraikwar.howmuch.data.di.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.howmuch.data.model.Category
import io.github.amanshuraikwar.howmuch.data.room.RoomDataSource
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.lang.IllegalStateException
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "TransactionRepository"

@Singleton
class TransactionRepository @Inject constructor(
    private val remoteTransactionDataManager: RemoteTransactionDataManager,
    private val roomDataSource: RoomDataSource,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) {

    suspend fun initialiseNewSpreadSheet(googleAccountCredential: GoogleAccountCredential)
            : String = withContext(dispatcherProvider.io) {

        val spreadSheetId = remoteTransactionDataManager.createNewSpreadSheet(
            googleAccountCredential
        )

        val categoriesMeta = async {
            remoteTransactionDataManager.initCategoriesMetadata(
                spreadSheetId, googleAccountCredential
            )
        }

        val walletMeta = async {
            remoteTransactionDataManager.initWalletMetadata(
                spreadSheetId, googleAccountCredential
            )
        }

        val transactions = async {
            remoteTransactionDataManager.initTransactions(
                spreadSheetId, googleAccountCredential
            )
        }

        categoriesMeta.await()
        walletMeta.await()
        transactions.await()

        return@withContext spreadSheetId
    }

    suspend fun getCategories(
        spreadSheetId: String,
        googleAccountCredential: GoogleAccountCredential
    ): List<Category> = withContext(dispatcherProvider.io) {

        var localCategories = roomDataSource.getCategories()

        if (localCategories.isEmpty()) {

            val remoteFetchedCategories =
                remoteTransactionDataManager.fetchCategories(spreadSheetId, googleAccountCredential)

            roomDataSource.refreshCategories(remoteFetchedCategories)

            localCategories = roomDataSource.getCategories()

            if (localCategories.isEmpty()) {
                // todo custom exception
                throw IllegalStateException("Categories could not be stored in local DB.")
            }
        }

        return@withContext localCategories
    }
}