package io.github.amanshuraikwar.nsapi.provider

import co.touchlab.sqliter.DatabaseConfiguration
import com.squareup.sqldelight.drivers.native.NativeSqliteDriver
import com.squareup.sqldelight.drivers.native.wrapConnection
import io.github.amanshuraikwar.nsapi.NsApi
import io.github.amanshuraikwar.nsapi.NsApiRepository
import io.github.amanshuraikwar.nsapi.db.NsApiDb
import io.github.amanshuraikwar.nxtbuz.commonkmm.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.preferencestorage.SettingsFactory
import io.github.amanshuraikwar.nxtbuz.repository.TrainStopRepository

actual class NsApiFactory(
    private val settingsSuiteName: String,
    private val dispatcherProvider: CoroutinesDispatcherProvider,
    private val subscriptionKey: String,
    private val addLoggingInterceptors: Boolean,
    private val dbBasePath: String,
) : TrainStopRepository.Factory {
    actual override fun create(): TrainStopRepository {
        val databaseConfiguration = DatabaseConfiguration(
            name = "nsapi.db",
            version = NsApiDb.Schema.version,
            create = { connection ->
                wrapConnection(connection) {
                    NsApiDb.Schema.create(it)
                }
            },
            upgrade = { connection, oldVersion, newVersion ->
                wrapConnection(connection) {
                    NsApiDb.Schema.migrate(it, oldVersion, newVersion)
                }
            },
            extendedConfig = DatabaseConfiguration.Extended(
                basePath = dbBasePath
            )
        )
        val db = NsApiDb(NativeSqliteDriver(configuration = databaseConfiguration))

        return NsApiRepository(
            settingsFactory = {
                SettingsFactory(settingsSuiteName = settingsSuiteName)
                    .createSettings()
            },
            dispatcherProvider = dispatcherProvider,
            nsApi = NsApi.createInstance(
                subscriptionKey = subscriptionKey,
                addLoggingInterceptors = addLoggingInterceptors
            ),
            nsApiDb = db
        )
    }
}