package io.github.amanshuraikwar.howmuch.data.transaction

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import io.github.amanshuraikwar.howmuch.data.di.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.howmuch.data.model.Category
import io.github.amanshuraikwar.howmuch.data.model.Transaction
import io.github.amanshuraikwar.howmuch.data.room.RoomDataSource
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import org.threeten.bp.OffsetDateTime
import java.lang.IllegalStateException
import javax.inject.Inject
import javax.inject.Singleton

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

    suspend fun getTransactions(
        spreadSheetId: String,
        googleAccountCredential: GoogleAccountCredential
    ): List<Transaction> = withContext(dispatcherProvider.io) {

        val categoriesDef = async { getCategories(spreadSheetId, googleAccountCredential) }

        val transactionsDef = async {
            remoteTransactionDataManager.fetchTransactions(spreadSheetId, googleAccountCredential)
        }

        val transactions = transactionsDef.await()
        val categories = categoriesDef.await().groupBy { it.id }.mapValues { (_, v) -> v[0] }

        return@withContext transactions
            .map { spreadSheetTransaction ->
                async {
                    Transaction(
                        spreadSheetTransaction.id,
                        OffsetDateTime.now(), // todo convert date
                        spreadSheetTransaction.amount,
                        spreadSheetTransaction.title,
                        categories[spreadSheetTransaction.categoryId]
                            // todo custom exception
                            ?: throw IllegalStateException(
                                "No category found with id = ${spreadSheetTransaction.categoryId}."
                            )
                    )
                }
            }
            .awaitAll()
    }
}