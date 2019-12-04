package io.github.amanshuraikwar.howmuch.data.transaction

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import io.github.amanshuraikwar.howmuch.data.CoroutinesDispatcherProvider
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "TransactionRepository"

@Singleton
class TransactionRepository @Inject constructor(
    private val remoteTransactionDataManager: RemoteTransactionDataManager,
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
}