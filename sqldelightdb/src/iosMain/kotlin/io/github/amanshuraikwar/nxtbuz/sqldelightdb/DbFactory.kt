package io.github.amanshuraikwar.nxtbuz.sqldelightdb

import co.touchlab.sqliter.DatabaseConfiguration
import com.squareup.sqldelight.drivers.native.NativeSqliteDriver
import com.squareup.sqldelight.drivers.native.wrapConnection
import io.github.amanshuraikwar.nxtbuz.db.NxtBuzDb

actual class DbFactory(
    private val dbBasePath: String
) {
    // ref: https://github.com/cashapp/sqldelight/issues/1787

    actual fun createDb(): NxtBuzDb {
        val databaseConfiguration = DatabaseConfiguration(
            name = "nxtbuz.db",
            version = NxtBuzDb.Schema.version,
            create = { connection ->
                wrapConnection(connection) { NxtBuzDb.Schema.create(it) }
            },
            upgrade = { connection, oldVersion, newVersion ->
                wrapConnection(connection) { NxtBuzDb.Schema.migrate(it, oldVersion, newVersion) }
            },
            extendedConfig = DatabaseConfiguration.Extended(
                basePath = dbBasePath
            )
        )
        return NxtBuzDb(NativeSqliteDriver(configuration = databaseConfiguration))
    }
}