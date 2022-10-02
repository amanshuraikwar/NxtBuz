package io.github.amanshuraikwar.nsapi.provider

import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import com.squareup.sqldelight.android.AndroidSqliteDriver
import io.github.amanshuraikwar.nsapi.NsApi
import io.github.amanshuraikwar.nsapi.NsApiRepository
import io.github.amanshuraikwar.nsapi.db.NsApiDb
import io.github.amanshuraikwar.nxtbuz.commonkmm.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.preferencestorage.SettingsFactory
import io.github.amanshuraikwar.nxtbuz.repository.TrainStopRepository

actual class NsApiFactory(
    private val context: Context,
    private val dispatcherProvider: CoroutinesDispatcherProvider,
    private val subscriptionKey: String,
    private val addLoggingInterceptors: Boolean
) : TrainStopRepository.Factory {
    actual override fun create(): TrainStopRepository {
        return NsApiRepository(
            settingsFactory = {
                SettingsFactory(
                    context = context,
                    name = "nsapi"
                ).createSettings()
            },
            dispatcherProvider = dispatcherProvider,
            nsApi = NsApi.createInstance(
                subscriptionKey = subscriptionKey,
                addLoggingInterceptors = addLoggingInterceptors
            ),
            nsApiDb = NsApiDb(
                AndroidSqliteDriver(
                    NsApiDb.Schema,
                    context,
                    "nsapi.db",
                    // to improve insert performance: https://stackoverflow.com/a/65426659
                    callback = object : AndroidSqliteDriver.Callback(NsApiDb.Schema) {
                        override fun onConfigure(db: SupportSQLiteDatabase) {
                            super.onConfigure(db)
                            setPragma(db, "JOURNAL_MODE = WAL")
                            setPragma(db, "SYNCHRONOUS = 2")
                        }

                        private fun setPragma(db: SupportSQLiteDatabase, pragma: String) {
                            val cursor = db.query("PRAGMA $pragma")
                            cursor.moveToFirst()
                            cursor.close()
                        }
                    }
                )
            )
        )
    }
}